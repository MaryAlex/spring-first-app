package com.springapp.mvc.dao;

import com.springapp.mvc.model.*;

import java.util.List;

public interface TaskDao
{
	void addTaskAndAnswersIntoDB(User user, Task task);
	List<Task> findAllTasks();
	List<Tag> findAllTags();
	List<Task> findLastNTasks(List <Task> tasks, int n);
	List<Task> findNMostPopularTasks(List <Task> tasks, int n);
	List<Task> findNUnresolvedTasks(List <Task> tasks, int n);
	List<Task> findTasksByTag(String tag);
	List<Task> findTasksByType(String type);
	Task findTaskByID(int id);
	Comment findCommentByID(int id);
	Boolean isRightAnswer(String answer, Task task);
	void changeRate(Task task, int rate, User user);
	Boolean isUsersTask(Task task, User user);
	int IndexOfUserAnsweredTask(Task task, User user);
	void addAnsweredTaskToUser(Task task, User user);
	double getTasksRate(Task task);
	double countRateOfAllTasks(User user);
	double[] countRateOfAllTasksForListOfUsers(List <User> users);
	void deleteTask(Task task);
	void deleteComment(Comment comment);
	void updateTask(Task task, User user);
	void addComment(Comment comment, Task task, User uer);
	Tag findTagByText(String text);
	void tagSeparation(Task task);
	void tagMerger(Task task);
	List<Task> search(String search);
}
