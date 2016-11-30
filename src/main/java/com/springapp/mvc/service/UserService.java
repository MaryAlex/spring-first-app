package com.springapp.mvc.service;

import com.springapp.mvc.model.Achievement;
import com.springapp.mvc.model.Task;
import com.springapp.mvc.model.User;

import java.util.List;

public interface UserService
{
	List<User> findAllUsers();
	List<User> findTopNUsers(int n);
	User findUserByEmail(String email);
	User findUserByNickname(String nickname);
	User findUserById(int id);
	void addUserIntoDB(User user);
	void updateUserInDB(User user);
	void updateUsersAnswersRate(User user, int id);
	double countUsersRate(User user, double countRateOfAllTasks);
	Boolean isGetAchievementForFirstAnswer(User user);
	Boolean isGetAchievementForAddTask(User user);
	Boolean isGetAchievementForAddComment(User user);
	Boolean isGetAchievementForAddRate(User user);
	void giveAchievementForRate(User user);
	Boolean isExistSecret(String secret);
}
