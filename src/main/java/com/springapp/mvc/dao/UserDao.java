package com.springapp.mvc.dao;

import com.springapp.mvc.model.Achievement;
import com.springapp.mvc.model.Answer;
import com.springapp.mvc.model.Task;
import com.springapp.mvc.model.User;

import java.util.List;

public interface UserDao
{
	List<User> findAllUsers();
	List<User> findTopNUsers(int n);
	User findUserByEmail(String email);
	User findUserByNickname(String nickname);
	User findUserById(int id);
	void addUserIntoDB(User user);
	void updateUserInDB(User user);
	void upAnswersRate(User user, Task task);
	double countRateByMagicFormula(User user, double countRateOfAllTasks);
	int countAnswers(User user);
	void addAchievementToUser(User user, Achievement achievement);
	Achievement getAchievementById(int id);
	Boolean isUserGetAchievementForAddTasks(User user, int countTask);
	Boolean isUserGetAchievementForAddComments(User user, int countComment);
	Boolean isUserGetAchievementForAddRate(User user, int countRate);
	Boolean isExistSecret(String secret);
}
