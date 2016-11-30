package com.springapp.mvc.dao.Impl;


import com.springapp.mvc.dao.TaskDao;
import com.springapp.mvc.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

@Repository("TaskDao")
public class TaskDaoImpl implements TaskDao
{
	@Autowired
	private SessionFactory sessionFactory;

	private Session getSessionFactory()
	{
		return sessionFactory.getCurrentSession();
	}

	private List getList(String type)
	{
		String expression = "from " + type;
		return getSessionFactory().createQuery(expression).list();
	}
	private List getListWithSomeCondition(String type, String column, Object value)
	{
		String expression = "from " + type + " where " + column +"=?";
		return getSessionFactory().createQuery(expression).setParameter(0, value).list();
	}

	public List<Task> findAllTasks()
	{
		return getList("Task");
	}

	public List<Tag> findAllTags()
	{
		return getList("Tag");
	}

	public void addTaskAndAnswersIntoDB(User user, Task task)
	{
		task.setUser(user);
		this.getSessionFactory().save(task);
		addNewAnswers(task, task);
		addTags(task);
		this.getSessionFactory().update(task);
	}

	private void addTags(Task task)
	{
		List<Tag> tags = task.getTags();
		for(int i = 0; i < tags.size(); i++)
		{
			if(this.isTagExist(tags.get(i))) {
				swapTags(task, tags, i);
			} else getSessionFactory().save(tags.get(i));
		}
	}

	private void swapTags(Task task, List<Tag> tags, int i)
	{
		Tag existTag = this.findTagByText(tags.get(i).getTag());
		this.deleteTagFromTask(task, i);
		this.addTaskToTag(task, existTag, i);
	}

	private void addTaskToTag(Task task, Tag tag, int i) {
		List<Tag> tags = task.getTags();
		tags.add(i, tag);
		task.setTags(tags);
	}

	private void deleteTagFromTask(Task task, int i) {
		List<Tag> tags = task.getTags();
		tags.remove(i);
		task.setTags(tags);
	}

	public Task findTaskByID(int id)
	{
		List<Task> tasks;
		tasks = getListWithSomeCondition("Task", "id", id);
		return tasks.get(0);
	}

	private Boolean isTagExist(Tag tag) {
		return this.findTagByText(tag.getTag()) != null;
	}

	public Tag findTagByText(String text)
	{
		List<Tag> tags = new ArrayList<Tag>();
		tags = this.getListWithSomeCondition("Tag", "tag", text);
		if(tags.size() > 0)
			return tags.get(0);
		else return null;
	}

	public Comment findCommentByID(int id)
	{
		List<Comment> comment;
		comment = this.getListWithSomeCondition("Comment", "id", id);
		return comment.get(0);
	}

	public Boolean isRightAnswer(final String answer, Task task)
	{
		List <Answer> tasksAnswers = task.getAnswers();
		for (Answer taskAnswer : tasksAnswers)
		{
			if(taskAnswer.getText().equals(answer))
				return true;
		}
		return false;
	}

	public List<Task> findLastNTasks(List<Task> tasksl, int n)
	{
		List <Task> tasks = new ArrayList<Task>(tasksl);
		Collections.reverse(tasks);
		if(tasks.size()>n)
			return tasks.subList(0, n);
		return tasks;
	}

	public void changeRate(Task task, int rate, User user)
	{
		Rate existRate = user.getRatesOfAnsweredTasks()
				.get(this.IndexOfUserAnsweredTask(task, user));
		existRate.setRate(rate);
		this.getSessionFactory().merge(existRate);
	}

	public Boolean isUsersTask(Task task, User user)
	{
		return task.getUser().getId() == user.getId();
	}

	public int IndexOfUserAnsweredTask(Task task, User user)
	{

		List <Rate> rates = user.getRatesOfAnsweredTasks();
		for (Rate rate : rates)
			if(rate.getAnsweredTask().getId() == task.getId())
				return rates.indexOf(rate);
		return -1;
	}

	public double countRateOfAllTasks(User user)
	{
		double rate = 0;
		for(Task task :user.getTasks())
			rate += this.getTasksRate(task);
		return rate;
	}

	public void addAnsweredTaskToUser(Task task, User user)
	{
		Rate rate = new Rate();
		rate.setAnsweredUser(user);
		rate.setAnsweredTask(task);
		getSessionFactory().save(rate);
	}

	public double getTasksRate(Task task)
	{
		double rate = 0;
		int size = 0;
		for(Rate tasksRate : task.getRatesByUsers())
		{
			rate += tasksRate.getRate();
			if(tasksRate.getRate() != 0)
				size++;
		}
		if(size == 0)
			return 0;
		return rate/size;
	}

	public List<Task> findTasksByTag(String tag)
	{
		List<Tag> tags = this.getListWithSomeCondition("Tag", "tag", tag);
		return tags.get(0).getTasks();
	}

	public List<Task> findTasksByType(String type)
	{
		List<Task> tasks = this.getListWithSomeCondition("Task", "type", type);
		return tasks;
	}

	public void deleteTask(Task task)
	{
		this.getSessionFactory().delete(task);
	}

	public void deleteComment(Comment comment)
	{
		this.getSessionFactory().delete(comment);
	}

	public void updateTask(Task editTask, User user)
	{
		Task task = this.findTaskByID(editTask.getId());
		setNewAttribute(editTask, task);
		this.getSessionFactory().update(task);
	}

	private void setNewAttribute(Task editTask, Task task)
	{
		setAllWrittenAttribute(editTask, task);
		setAllSelectedAttribute(editTask, task);
	}

	private void setAllWrittenAttribute(Task editTask, Task task)
	{
		setWrittenAttribute(editTask, task);
		setAnswers(editTask, task);
	}

	private void setAllSelectedAttribute(Task editTask, Task task)
	{
		setSelectedAttribute(editTask, task);
		setTags(editTask, task);
	}

	private void setWrittenAttribute(Task editTask, Task task)
	{
		task.setTitle(editTask.getTitle());
		task.setText(editTask.getText());
	}

	private void setSelectedAttribute(Task editTask, Task task)
	{
		task.setDifficulty(editTask.getDifficulty());
		task.setType(editTask.getType());
	}

	private void setTags(Task editTask, Task task)
	{
		task.setTags(editTask.getTags());
		this.addTags(task);
	}

	private void setAnswers(Task editTask, Task task)
	{
		deleteUselessAnswers(editTask, task);
		addNewAnswers(editTask, task);
	}

	private void deleteSpaceInAnswers(Task task)
	{
		List<Answer> answers = task.getAnswers();
		answers.removeIf(new Predicate<Answer>()
		{
			@Override
			public boolean test(Answer answer)
			{
				return answer.getText() == null;
			}
		});
		task.setAnswers(answers);
	}

	private void addNewAnswers(Task editTask, Task task)
	{
		this.deleteSpaceInAnswers(editTask);
		for(Answer newAnswer: editTask.getAnswers())
		{
			if(newAnswer.getTask() == null)
			{
				newAnswer.setTask(task);
				this.getSessionFactory().save(newAnswer);
			}
		}
	}

	private void deleteUselessAnswers(Task editTask, Task task)
	{
		for(Answer answer: task.getAnswers())
		{
			List<Answer> editAnswers = editTask.getAnswers();
			Boolean flag = true;
			for(int i = 0; i < editAnswers.size(); i++ )
				if(answer.getText().equals(editAnswers.get(i).getText()))
				{
					removeAnswerFromList(editTask, i--, answer);
					flag = false;
					break;
				}
			if(flag)
				this.getSessionFactory().delete(answer);
		}
	}

	private void removeAnswerFromList(Task editTask, int i, Answer newAnswer)
	{
		List<Answer> answers = editTask.getAnswers();
		answers.remove(i);
		editTask.setAnswers(answers);
	}

	public void addComment(Comment comment, Task task, User user)
	{
		comment.setTask(task);
		comment.setUser(user);
		this.getSessionFactory().save(comment);
	}

	public List<Task> findNMostPopularTasks(List <Task> tasksl, int n)
	{
		List<Task> tasks = new ArrayList<Task>(tasksl);
		tasks.sort(new Comparator<Task>()
		{
			public int compare(Task p1, Task p2)
			{
				if (p1.getRatesByUsers().size() < p2.getRatesByUsers().size())
					return 1;
				if (p1.getRatesByUsers().size() > p2.getRatesByUsers().size())
					return -1;
				return 0;
			}
		});
		if(tasks.size()>n)
			return tasks.subList(0, n+1);
		return tasks;
	}

	public List<Task> findNUnresolvedTasks(List <Task> tasksl, int n)
	{
		List<Task> tasks = new ArrayList<Task>(tasksl);
		Collections.reverse(tasks);
		tasks.removeIf(new Predicate<Task>()
		{
			public boolean test(Task t)
			{
				return t.getRatesByUsers().size() != 0;
			}
		});
		if(tasks.size()>n)
			return tasks.subList(0, n);
		return tasks;
	}

	public double[] countRateOfAllTasksForListOfUsers(List<User> users)
	{
		double[] rates = new double[users.size()];
		int i = 0;
		for(User user: users)
		{
			rates[i] = Math.round(this.countRateOfAllTasks(user));
			i++;
		}
		return rates;
	}

	public void tagSeparation(Task task) {
		List<Tag> listOfTags = new ArrayList<Tag>();
		String tags = task.getTags().get(0).getTag(), separator = ",";
		int indexOfFirst = 0, indexOfSecond = tags.indexOf(separator, 0), i = 0;
		while(indexOfSecond > 0) {
			Tag tag = new Tag();
			tag.setTag(tags.substring(indexOfFirst, indexOfSecond));
			listOfTags.add(tag);
			indexOfFirst = indexOfSecond+1;
			indexOfSecond = tags.indexOf(separator, indexOfFirst);
		}
		Tag tag = new Tag();
		tag.setTag(tags.substring(indexOfFirst));
		listOfTags.add(tag);
		task.setTags(listOfTags);
	}

	public void tagMerger(Task task)
	{
		if(task.getTags().size() != 0)
		{
			List<Tag> listOfTags = task.getTags();
			String tags = "";
			for (Tag tag : listOfTags)
			{
				tags += tag.getTag() + ",";
			}
			tags = tags.substring(0, tags.length() - 1);
			List<Tag> newTags = new ArrayList<Tag>();
			Tag lineOfTag = new Tag();
			lineOfTag.setTag(tags);
			newTags.add(lineOfTag);
			task.setTags(newTags);
		}
	}

	public List<Task> search(String search)
	{
		FullTextSession fullTextSession = Search.getFullTextSession(this.getSessionFactory());
		//Transaction tx = fullTextSession.beginTransaction();
		QueryBuilder qb = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( Task.class ).get();
		org.apache.lucene.search.Query query = qb
				.keyword()
				.onFields("title", "text")
				.matching(search)
		.createQuery();
		org.hibernate.Query hibQuery =
				fullTextSession.createFullTextQuery(query,Task.class);
		List result = hibQuery.list();

		//tx.commit();
		//this.getSessionFactory().close();
		return result;
	}
}
