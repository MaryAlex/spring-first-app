package com.springapp.mvc.controller;


import com.springapp.mvc.model.*;
import com.springapp.mvc.service.TaskService;
import com.springapp.mvc.service.UserService;
import org.markdownj.MarkdownProcessor;
import org.scribe.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

@Controller
@RequestMapping("/")
public class MainController {

	@Autowired
	private UserService userService;
	@Autowired
	private TaskService taskService;
	@Autowired
	UserDetailsService userDetailsServiceImpl;
	private final ProviderSignInUtils providerSignInUtils;
	@Autowired
	public MainController(ProviderSignInUtils providerSignInUtils) {
		this.providerSignInUtils = providerSignInUtils;
	}

	private User getUserInSession(HttpServletRequest request)
	{
		return userService.findUserByNickname(request.getUserPrincipal().getName());
	}
	private int isLoginId(Integer id, HttpServletRequest request)
	{
		if(id == 0)
			id = getUserInSession(request).getId();
		return id;
	}
	private String getTaskId(Task task)
	{
		return String.valueOf(task.getId());
	}
	private Map<User, Rate> createMapForUsersTop(List<User> topNUsers, double[] topNUsersRate)
	{
		Map<User, Rate> topNUsersMap= new HashMap<User, Rate>();
		int i = 0;
		for(User user: topNUsers)
		{
			i = addInMap(topNUsersRate[i], topNUsersMap, i, user);
		}
		return topNUsersMap;
	}

	private int addInMap(double v, Map<User, Rate> topNUsersMap, int i, User user)
	{
		Rate rate = new Rate();
		rate.setRate((int) v);
		i++;
		topNUsersMap.put(user, rate);
		return i;
	}

	private double [] getFinalResultUsersRate(List<User> topNUsers, double[] topNUsersRate)
	{
		int i = 0;
		for(User user: topNUsers)
		{
			topNUsersRate[i] = Math.round(userService.countUsersRate(user, topNUsersRate[i]));
			i++;
		}
		return topNUsersRate;
	}

	@RequestMapping(value = "/issecret", method = RequestMethod.GET)
	public ModelAndView isSecret(@RequestParam(value = "secret", required = false) String secret)
	{
		ModelAndView mav = new ModelAndView("redirect:login");
		if(userService.isExistSecret(secret))
		{
			mav.addObject("logged", "logged");
		}
		return mav;
	}

	@RequestMapping(value =  {"/","/welcome**"}, method = RequestMethod.GET)
	public ModelAndView welcomePage(Model model) {
		ModelAndView mav;
		mav = new ModelAndView("home");
		int N = 5;
		addObjectsToMAV(mav, N);
		return mav;
	}

	private void addObjectsToMAV(ModelAndView mav, int n)
	{
		addTopNUsersToModelAndView(mav, n);
		addObjectToModelAndView(mav, n);
	}

	private void addTopNUsersToModelAndView(ModelAndView mav, int n)
	{
		List<User> TopNUsers= userService.findTopNUsers(n);
		Map<User, Rate> topNUsersMap = getUserRateMap(TopNUsers);
		mav.addObject("TopNUsers", TopNUsers);
		mav.addObject("TopNUsersMap", topNUsersMap);
	}

	private Map<User, Rate> getUserRateMap(List<User> topNUsers)
	{
		double[] topNUsersRate = taskService.countRateOfAllTasksForListOfUsers(topNUsers);
		topNUsersRate = getFinalResultUsersRate(topNUsers, topNUsersRate);
		return createMapForUsersTop(topNUsers, topNUsersRate);
	}

	private void addObjectToModelAndView(ModelAndView mav, int n)
	{
		List<Task> tasks = taskService.findAllTasks();
		mav.addObject("lastNTasks", taskService.findLastNTasks(tasks, n));
		mav.addObject("NMostPopularTasks", taskService.findNMostPopularTasks(tasks, n));
		mav.addObject("NUnresolvedTasks", taskService.findNUnresolvedTasks(tasks, n));
		mav.addObject("tagCloud", this.clearFromNotPopularTagAndSort(taskService.findAllTags(), 50));
	}

	private List <Tag> clearFromNotPopularTagAndSort (List<Tag> tags, int n)
	{
		removeNotPopularTag(tags);
		sortByPopular(tags);
		return returnTags(tags, n);
	}

	private List<Tag> returnTags(List<Tag> tags, int n)
	{
		if(tags.size()>n)
			return tags.subList(0, n+1);
		return tags;
	}

	private void sortByPopular(List<Tag> tags)
	{
		tags.sort(new Comparator<Tag>()
		{
			@Override
			public int compare(Tag o1, Tag o2)
			{
				if (o1.getTasks().size() > o2.getTasks().size())
					return 1;
				if (o1.getTasks().size() < o2.getTasks().size())
					return -1;
				return 0;
			}
		});
	}

	private void removeNotPopularTag(List<Tag> tags)
	{
		tags.removeIf(new Predicate<Tag>()
		{
			@Override
			public boolean test(Tag tag)
			{
				return tag.getTasks().size() == 0;
			}
		});
	}

	//Spring Security see this :
	@RequestMapping(value = {"/login"})
	public ModelAndView login(
			@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout,
			@RequestParam(value = "registered", required = false) String registered,
			@RequestParam(value = "logged", required = false) String logged) {
		ModelAndView mav = new ModelAndView();
		isControllerHaveSomeMessage(error, logout, registered, logged, mav);
		mav.setViewName("login");

		return mav;
	}

	private void isControllerHaveSomeMessage(@RequestParam(value = "error", required = false) String error, @RequestParam(value = "logout", required = false) String logout, @RequestParam(value = "registered", required = false) String registered, @RequestParam(value = "logged", required = false) String logged, ModelAndView mav)
	{
		isGetMassage(error, mav, "error", "Invalid username or password! Or you just have not confirmed your email.");
		isGetMassage(logout, mav, "msg", "You've been logged out successfully.");
		isGetMassage(registered, mav, "msg", "We send mail to you.");
		isGetMassage(logged, mav, "msg", "Now you can login");
	}

	private void isGetMassage(@RequestParam(value = "error", required = false) String error, ModelAndView mav, String error2, String attributeValue)
	{
		if (error != null)
		{
			mav.addObject(error2, attributeValue);
		}
	}

	@RequestMapping(value = "/callbackvk", method = RequestMethod.POST)
	public ModelAndView callbackVk(@RequestParam(value="user_id", required=true) int user_id, @RequestParam(value="first_name", required=true) String first_name,
	                       @RequestParam(value="last_name", required=true) String last_name)throws IOException
	{
		User user = createNewUser(user_id, first_name, last_name);
		addUserIntoBD(user);
		LogIn(user);
		return new ModelAndView("redirect:welcome");
	}

	private void LogIn(User user)
	{
		UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(user.getEmail());
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private User createNewUser(@RequestParam(value = "user_id", required = true) int user_id, @RequestParam(value = "first_name", required = true) String first_name, @RequestParam(value = "last_name", required = true) String last_name)
	{
		User user = new User();
		user.setEmail("vkontakte@" + user_id +  ".vk");
		user.setUsername(first_name + " " + last_name);
		user.setPassword(String.valueOf(user_id));
		user.setNickname(String.valueOf(user_id) + ".vk");
		return user;
	}

	@RequestMapping(value = "/signup")
	public String twitter(WebRequest request, Model model) {
		Connection<?> connection = providerSignInUtils.getConnectionFromSession(request);
		User registration = createRegistrationDTO(connection);
		model.addAttribute("user", registration);
		addUserIntoBD(registration);
		LogIn(registration);
		return "redirect:welcome";
	}

	private void addUserIntoBD(User registration)
	{
		try {
			userService.addUserIntoDB(registration);
		} catch (Exception ex)
		{
		}
	}

	private User createRegistrationDTO(Connection<?> connection) {
		User dto = new User();
		if (connection != null) {
			UserProfile socialMediaProfile = connection.fetchUserProfile();
			String userId = connection.getKey().getProviderUserId();
			dto.setEmail("twitter@" + userId +  ".tw");
			dto.setUsername(socialMediaProfile.getFirstName());
			dto.setNickname(userId + ".tw");
			dto.setPassword(userId);
		}
		return dto;
	}

	@RequestMapping(value = "/registration", method = RequestMethod.GET)
	public String registrationUser (Model model)
	{
		model.addAttribute("personAttribute", new User());
		return "registration";
	}

	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public ModelAndView addUser(@ModelAttribute("personAttribute") User user)
	{
		ModelAndView mav = new ModelAndView("redirect:login");
		try {
			registration(user, mav);
		} catch (Exception ex)
		{
			mav = reactOnError(user);
		}
		return mav;
	}

	private ModelAndView reactOnError(@ModelAttribute("personAttribute") User user)
	{
		ModelAndView mav;
		mav = new ModelAndView("registration");
		user.setPassword("");
		mav.addObject("error", "This nickname or email already exist");
		return mav;
	}

	private void registration(@ModelAttribute("personAttribute") User user, ModelAndView mav) throws MessagingException
	{
		userService.addUserIntoDB(user);
		Mail.generateAndSendEmail(user);
		mav.addObject("registered", "registered");
	}

	@RequestMapping(value = { "/add_task"}, method = RequestMethod.GET)
	public ModelAndView inputTask (Model model)
	{
		ModelAndView mav = new ModelAndView("add_task");
		addAttributes(model, mav);
		return mav;
	}

	private void addAttributes(Model model, ModelAndView mav)
	{
		mav.addObject("tags", taskService.findAllTags());
		Task task = new Task();
		task.setAnswers(new ArrayList<Answer>());
		task.setTags(new ArrayList<Tag>());
		model.addAttribute("taskAttribute", task);
	}

	@RequestMapping(value = { "/edittask"}, method = RequestMethod.GET)
	public ModelAndView editTask (Model model, @RequestParam(value="id", required=true) Integer id, HttpServletRequest request)
	{
		ModelAndView mav = new ModelAndView("add_task");
		if(!taskService.isUsersTask(id, this.getUserInSession(request)))
			return new ModelAndView("redirect:add_task");
		addObjects(model, id, mav);
		return mav;
	}

	private void addObjects(Model model, @RequestParam(value = "id", required = true) Integer id, ModelAndView mav)
	{
		addTaskToMAV(model, id);
		mav.addObject("tags", taskService.findAllTags());
	}

	private void addTaskToMAV(Model model, @RequestParam(value = "id", required = true) Integer id)
	{
		Task task = taskService.findTaskByID(id);
		taskService.tagMerger(task);
		model.addAttribute("taskAttribute", task);
	}

	@RequestMapping(value = { "/edittask"}, method = RequestMethod.POST)
	public ModelAndView updateTask (@ModelAttribute("taskAttribute") Task task, HttpServletRequest request)
	{
		ModelAndView mav = new ModelAndView("redirect:task?id=" + this.getTaskId(task));
		try {
			taskService.updateTask(task, this.getUserInSession(request));
		} catch (Exception ex)
		{
			mav.setViewName("redirect:edittask?id=" + this.getTaskId(task));
			mav.addObject("error", "This title already exist");
		}
		return mav;
	}

	@RequestMapping(value = "/add_task", method = RequestMethod.POST)
	public ModelAndView addTask(@ModelAttribute("taskAttribute") Task task, HttpServletRequest request)
	{
		ModelAndView mav = new ModelAndView("redirect:tasks");
		try {
			treatRequest(task, request, mav);
		} catch (Exception ex)
		{
			reactOnError(task, mav);
		}
		return mav;
	}

	private void reactOnError(@ModelAttribute("taskAttribute") Task task, ModelAndView mav)
	{
		mav.setViewName("add_task");
		taskService.tagMerger(task);
		mav.addObject("tags", taskService.findAllTags());
		mav.addObject("error", "This title already exist");
	}

	private void treatRequest(@ModelAttribute("taskAttribute") Task task, HttpServletRequest request, ModelAndView mav)
	{
		taskService.addTaskAndAnswersIntoDB(
				getUserInSession(request), task);
		if(userService.isGetAchievementForAddTask(this.getUserInSession(request)))
			mav.addObject("Achievement", "You have a new achievement! Look at your profile to see.");
	}

	private void convertToHTML(@ModelAttribute("taskAttribute") Task task)
	{
		MarkdownProcessor markdownProcessor = new MarkdownProcessor();
		String tasksTextMarkdown = markdownProcessor.markdown(task.getText());
		task.setText(tasksTextMarkdown);
	}

	@RequestMapping(value = {"/tasks"}, method = RequestMethod.GET)
	public ModelAndView seeAllTasks(@RequestParam(value="id", required=false) Integer id, @RequestParam(value="tag", required=false) String tag,
	                                @RequestParam(value="type", required=false) String type, HttpServletRequest request)
	{
		ModelAndView mav = new ModelAndView("tasks");
		List<Task> tasks;
		tasks = chooseNeededTasks(id, tag, type, request);
		mav.addObject("tasks", tasks);
		return  mav;
	}

	private List<Task> chooseNeededTasks(@RequestParam(value = "id", required = false) Integer id, @RequestParam(value = "tag", required = false) String tag, @RequestParam(value = "type", required = false) String type, HttpServletRequest request)
	{
		List<Task> tasks;
		if (id != null) {
			id = isLoginId(id, request);
			tasks = userService.findUserById(id).getTasks();
		} else if (tag != null){
			tasks = taskService.findTasksByTag(tag);
		} else if (type != null) {
			tasks = taskService.findTasksByType(type);
		}
		else{
			tasks = taskService.findAllTasks();
		}
		return tasks;
	}

	@RequestMapping(value = {"/users"}, method = RequestMethod.GET)
	public ModelAndView seeAllUsers()
	{
		ModelAndView mav = new ModelAndView("users");
		List<User> users = userService.findAllUsers();
		mav.addObject("users", users);
		return  mav;
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public ModelAndView search(@RequestParam(value="search", required=true) String search)
	{
		ModelAndView mav = new ModelAndView("/tasks");
		mav.addObject("tasks", taskService.search(search));
		return mav;
	}

	@RequestMapping(value = { "/task"}, method = RequestMethod.GET)
	public ModelAndView seeSomeTask(@RequestParam(value="id", required=true) int id, Model model, HttpServletRequest request)
	{
		ModelAndView mav = new ModelAndView("task");
		Task task = taskService.findTaskByID(id);
		this.convertToHTML(task);
		addModelsToTaskPage(id, model, request, mav, task);
		return mav;
	}

	private void addModelsToTaskPage(@RequestParam(value = "id", required = true) int id, Model model, HttpServletRequest request, ModelAndView mav, Task task)
	{
		mav.addObject("task", task);
		mav.addObject("rate", taskService.getTasksRate(id));
		addDynamicVar(id, request, mav);
		model.addAttribute("answer", new Answer());
		model.addAttribute("comment", new Comment());
	}

	private void addDynamicVar(@RequestParam(value = "id", required = true) int id, HttpServletRequest request, ModelAndView mav)
	{
		if(taskService.isUsersTask(id, this.getUserInSession(request)))
			mav.addObject("isUsers", "true");
		else if(taskService.isUserAnsweredTask(id, this.getUserInSession(request)))
			mav.addObject("isAnswered", "true");
		if(userService.isGetAchievementForAddRate(this.getUserInSession(request)))
			mav.addObject("Achievement", "You have a new achievement! Look at your profile to see.");
	}

	@RequestMapping(value = "/task", method = RequestMethod.POST)
	public ModelAndView isRightAnswer(@ModelAttribute("answer") Answer answer, @ModelAttribute("comment") Comment comment,
	                                  @RequestParam(value="id", required=true) int id, HttpServletRequest request)
	{
		ModelAndView mav = new ModelAndView("redirect:task");
		mav.addObject("id", id);
		if(answer.getText() != null)
		{
			if (taskService.isRightAnswer(answer.getText(), id))
			{
				treatRightAnswer(id, request, mav);
			} else mav.addObject("error", "Are you sure?");
		}
		else if(comment.getComment() != null)
		{
			addComment(comment, id, request, mav);
		}
		return mav;
	}

	private void treatRightAnswer(@RequestParam(value = "id", required = true) int id, HttpServletRequest request, ModelAndView mav)
	{
		if(userService.isGetAchievementForFirstAnswer(this.getUserInSession(request)))
			mav.addObject("Achievement", "You have a new achievement! Look at your profile to see.");
		taskService.addAnsweredTaskToUser(id, this.getUserInSession(request));
		userService.updateUsersAnswersRate(this.getUserInSession(request), id);
		mav.addObject("answerRight", "Right!");
	}

	private void addComment(@ModelAttribute("comment") Comment comment, @RequestParam(value = "id", required = true) int id, HttpServletRequest request, ModelAndView mav)
	{
		taskService.addComment(comment, id, this.getUserInSession(request));
		if(userService.isGetAchievementForAddComment(this.getUserInSession(request)))
			mav.addObject("Achievement", "You have a new achievement! Look at your profile to see.");
	}

	@RequestMapping(value = "/deletetask", method = RequestMethod.GET)
	public ModelAndView deleteTask(@RequestParam(value="id", required=true) int id)
	{
		taskService.deleteTask(id);
		return new ModelAndView("redirect:tasks?id=0");
	}

	@RequestMapping(value = "/deletecomment", method = RequestMethod.GET)
	public ModelAndView deleteComment(@RequestParam(value="id", required=true) int id)
	{
		int task_id = taskService.findCommentByID(id).getTask().getId();
		taskService.deleteComment(id);
		return new ModelAndView("redirect:task?id=" + task_id);
	}

	@RequestMapping(value = "/changerate", method = RequestMethod.POST)
	public ModelAndView changeRate(@RequestParam(value="id", required=true) int id,
	                               @RequestParam(value="rate", required=true) int rate, HttpServletRequest request,
	                               @RequestParam(value="Achievement", required=false) String achievement)
	{
		ModelAndView mav = new ModelAndView("redirect:task");
		mav.addObject("id", id);
		taskService.changeRate(id, rate, this.getUserInSession(request));
		if(!achievement.isEmpty())
			userService.giveAchievementForRate(this.getUserInSession(request));
		return mav;
	}

	@RequestMapping(value = { "/user"}, method = RequestMethod.GET)
	public ModelAndView seeUserProfile(@RequestParam(value="id", required=true) int id, HttpServletRequest request)
	{
		ModelAndView mav = new ModelAndView("user");
		User user = addUser(id, request, mav);
		addRates(mav, user);
		return mav;
	}

	private User addUser(@RequestParam(value = "id", required = true) int id, HttpServletRequest request, ModelAndView mav)
	{
		id = isLoginId(id, request);
		User user = userService.findUserById(id);
		mav.addObject("user", user);
		return user;
	}

	private void addRates(ModelAndView mav, User user)
	{
		double rateOfTasks = taskService.countRateOfAllTasks(user);
		double rate = userService.countUsersRate(user, rateOfTasks);
		mav.addObject("rate", Math.round(rate));
		mav.addObject("rateOfTasks", Math.round(rateOfTasks));
	}
}