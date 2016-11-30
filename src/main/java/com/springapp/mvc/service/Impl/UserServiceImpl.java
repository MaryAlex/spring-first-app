package com.springapp.mvc.service.Impl;

import com.springapp.mvc.dao.TaskDao;
import com.springapp.mvc.dao.UserDao;
import com.springapp.mvc.model.Achievement;
import com.springapp.mvc.model.Task;
import com.springapp.mvc.model.User;
import com.springapp.mvc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService
{
	@Autowired
	private UserDao dao;
	@Autowired
	private TaskDao daoTask;

	public void addUserIntoDB(User user)
	{
		dao.addUserIntoDB(user);
	}

	public User findUserByEmail(String email)
	{
		return dao.findUserByEmail(email);
	}

	public User findUserByNickname(String nickname)
	{
		return dao.findUserByNickname(nickname);
	}
	public User findUserById(int id)
	{
		return dao.findUserById(id);
	}

	public void updateUserInDB(User user)
	{
		dao.updateUserInDB(user);
	}

	public List<User> findAllUsers()
	{
		return dao.findAllUsers();
	}

	public void updateUsersAnswersRate(User user, int id)
	{
		dao.upAnswersRate(user, daoTask.findTaskByID(id));
	}

	public double countUsersRate(User user, double countRateOfAllTasks)
	{
		return dao.countRateByMagicFormula(user, countRateOfAllTasks);
	}

	public List<User> findTopNUsers(int n)
	{
		return dao.findTopNUsers(n);
	}

	public Boolean isGetAchievementForFirstAnswer(User user)
	{
		int idAchievementFirstAnswer = 1;
		if(dao.countAnswers(user) == 0){
			dao.addAchievementToUser(user, dao.getAchievementById(idAchievementFirstAnswer));
			return true;
		}
		return false;
	}

	public Boolean isGetAchievementForAddTask(User user)
	{
		for(int tasksCount = 10, achievementId = 2; tasksCount < 31; tasksCount+=10, achievementId++)
			if(dao.isUserGetAchievementForAddTasks(user, tasksCount))
			{
				dao.addAchievementToUser(user, dao.getAchievementById(achievementId));
				return true;
			}
		return false;
	}

	public Boolean isGetAchievementForAddComment(User user)
	{
		int countComments = 10, achievementId = 5;
		if(dao.isUserGetAchievementForAddComments(user, countComments))
		{
			dao.addAchievementToUser(user, dao.getAchievementById(achievementId));
			return true;
		}
		return false;
	}

	public Boolean isGetAchievementForAddRate(User user)
	{
		int countRates = 5;
		if(dao.isUserGetAchievementForAddRate(user, countRates))
			return true;
		return false;
	}

	public void giveAchievementForRate(User user)
	{
		int id = 6;
		dao.addAchievementToUser(user, dao.getAchievementById(id));
	}

	public Boolean isExistSecret(String secret)
	{
		return dao.isExistSecret(secret);
	}
}
