package com.springapp.mvc.service.Impl;

import com.springapp.mvc.dao.TaskDao;
import com.springapp.mvc.model.Comment;
import com.springapp.mvc.model.Tag;
import com.springapp.mvc.model.Task;
import com.springapp.mvc.model.User;
import com.springapp.mvc.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("taskService")
@Transactional
public class TaskServiceImpl implements TaskService
{
	@Autowired
	private TaskDao dao;

	public void tagSeparation(Task task)
	{
		dao.tagSeparation(task);
	}

	public void tagMerger(Task task)
	{
		dao.tagMerger(task);
	}

	public void addTaskAndAnswersIntoDB(User user, Task task)
	{
		dao.tagSeparation(task);
		dao.addTaskAndAnswersIntoDB(user, task);
	}

	public List<Task> findAllTasks()
	{
		return dao.findAllTasks();
	}

	public List<Tag> findAllTags()
	{
		return dao.findAllTags();
	}

	public Task findTaskByID(int id)
	{
		return dao.findTaskByID(id);
	}

	public Comment findCommentByID(int id)
	{
		return dao.findCommentByID(id);
	}

	public Boolean isRightAnswer(String answer, int id)
	{
		return dao.isRightAnswer(answer, dao.findTaskByID(id));
	}

	public List<Task> findLastNTasks(List <Task> tasks, int n)
	{
		return dao.findLastNTasks(tasks, n);
	}

	public Boolean isUserAnsweredTask(int id, User user)
	{
		return dao.IndexOfUserAnsweredTask(dao.findTaskByID(id), user) != -1;
	}

	public Boolean isUsersTask(int id, User user)
	{
		return dao.isUsersTask(dao.findTaskByID(id), user);
	}

	public void changeRate(int id, int rate, User user)
	{
		dao.changeRate(this.findTaskByID(id), rate, user);
	}

	public void addAnsweredTaskToUser(int id, User user)
	{
		dao.addAnsweredTaskToUser(dao.findTaskByID(id), user);
	}

	public double getTasksRate(int id)
	{
		return dao.getTasksRate(dao.findTaskByID(id));
	}

	public double countRateOfAllTasks(User user)
	{
		return dao.countRateOfAllTasks(user);
	}

	public List<Task> findTasksByTag(String tag)
	{
		return dao.findTasksByTag(tag);
	}

	public List<Task> findTasksByType(String type)
	{
		return dao.findTasksByType(type);
	}

	public void deleteTask(int id)
	{
		dao.deleteTask(dao.findTaskByID(id));
	}

	public void deleteComment(int id)
	{
		dao.deleteComment(dao.findCommentByID(id));
	}

	public void updateTask(Task task, User user)
	{
		dao.tagSeparation(task);
		dao.updateTask(task, user);
	}

	public void addComment(Comment comment, int id, User user)
	{
		dao.addComment(comment, dao.findTaskByID(id),user);
	}

	public List<Task> findNMostPopularTasks(List <Task> tasks, int n)
	{
		return dao.findNMostPopularTasks(tasks, n);
	}

	public List<Task> findNUnresolvedTasks(List <Task> tasks, int n)
	{
		return dao.findNUnresolvedTasks(tasks, n);
	}

	public double[] countRateOfAllTasksForListOfUsers(List<User> users)
	{
		return dao.countRateOfAllTasksForListOfUsers(users);
	}

	public List<Task> search(String search)
	{
		return dao.search(search);
	}
}
