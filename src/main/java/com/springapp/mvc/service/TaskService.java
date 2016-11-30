package com.springapp.mvc.service;

import com.springapp.mvc.model.*;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.List;

public interface TaskService
{
	void addTaskAndAnswersIntoDB(User user, Task task);
	List<Task> findAllTasks();
	List<Tag> findAllTags();
	List<Task> findLastNTasks(List<Task> tasks, int n);
	List<Task> findNMostPopularTasks(List<Task> tasks, int n);
	List<Task> findNUnresolvedTasks(List <Task> tasks, int n);
	List<Task> findTasksByTag(String tag);
	List<Task> findTasksByType(String type);
	Task findTaskByID(int id);
	Comment findCommentByID(int id);
	Boolean isRightAnswer(String answer, int id);
	Boolean isUserAnsweredTask(int id, User user);
	Boolean isUsersTask(int id, User user);
	void changeRate(int id,int rate, User user);
	void addAnsweredTaskToUser(int id, User user);
	double getTasksRate(int id);
	double countRateOfAllTasks(User user);
	double[] countRateOfAllTasksForListOfUsers(List <User> users);
	void deleteTask(int id);
	void deleteComment(int id);
	void updateTask(Task task, User user);
	void addComment(Comment comment, int id, User user);
	void tagMerger(Task task);
	void tagSeparation(Task task);
	List<Task> search(String search);
}
