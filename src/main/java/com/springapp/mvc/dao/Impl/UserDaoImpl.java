package com.springapp.mvc.dao.Impl;

import com.springapp.mvc.dao.TaskDao;
import com.springapp.mvc.dao.UserDao;
import com.springapp.mvc.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Repository("UserDao")
public class UserDaoImpl implements UserDao
{
	@Autowired
	private SessionFactory sessionFactory;

	public Session getSessionFactory()
	{
		return sessionFactory.getCurrentSession();
	}
	
	public User findUserByEmail(String email)
	{
		List<User> users = new ArrayList<User>();

		users = getSessionFactory()
				.createQuery("from User where email=?")
				.setParameter(0, email)
				.list();

		if(users.size() > 0)
			return users.get(0);
		else return null;
	}
	public User findUserByNickname(String nickname)
	{
		List<User> users = new ArrayList<User>();

		users = getSessionFactory()
				.createQuery("from User where nickname=?")
				.setParameter(0, nickname)
				.list();

		if(users.size() > 0)
			return users.get(0);
		else return null;
	}
	public User findUserById(int id)
	{
		List<User> users = new ArrayList<User>();

		users = getSessionFactory()
				.createQuery("from User where id=?")
				.setParameter(0, id)
				.list();

		if(users.size() > 0)
			return users.get(0);
		else return null;
	}

	private void generateSecret(User user)
	{
		char[] secret = new char[16];
		int n = 16;
		for (int i = 0; i < n; i++)
		{
			secret[i] = (char)(int)(Math.random()*26+97);
		}
		user.setSecret(String.valueOf(secret));
	}

	private Boolean isSocial(String email)
	{
		String [] ends = {".tw", ".fb", ".vk"};
		for(String end:ends)
			if(email.contains(end))
				return true;
		return false;
	}

	public void addUserIntoDB(User user)
	{
		String pass = user.getPassword();
		PasswordEncoder sha = new BCryptPasswordEncoder();
		user.setPassword(sha.encode(pass));
		if(!this.isSocial(user.getEmail()))
			this.generateSecret(user);
		getSessionFactory().save(user);
	}
	public void updateUserInDB(User user)
	{
		getSessionFactory().update(user);
	}

	public void mergeUserInDB(User user)
	{
		getSessionFactory().merge(user);
	}

	public List<User> findAllUsers()
	{
		return getSessionFactory().createQuery("from User").list();
	}

	public User findUserBySecret(String secret)
	{
		List<User> users = getSessionFactory().createQuery("from User where secret=?").setParameter(0, secret).list();
		if(users.size() > 0)
			return users.get(0);
		else return null;
	}

	public double countRateByMagicFormula(User user, double countRateOfAllTasks)
	{
		return (user.getAnswersRate()*0.3) + (countRateOfAllTasks*0.7);
	}

	public void upAnswersRate(User user, Task task)
	{
		user.setAnswersRate(user.getAnswersRate() + (task.getDifficulty() * 2));
		this.mergeUserInDB(user);
	}

	public List<User> findTopNUsers(int n)
	{
		List<User> users;
		users = this.findAllUsers();
		users.sort(new Comparator<User>()
		{
			public int compare(User p1, User p2)
			{
				TaskDao dao = new TaskDaoImpl();
				if (countRateByMagicFormula(p1, dao.countRateOfAllTasks(p1)) < countRateByMagicFormula(p2, dao.countRateOfAllTasks(p2)))
					return 1;
				if (countRateByMagicFormula(p1, dao.countRateOfAllTasks(p1)) > countRateByMagicFormula(p2, dao.countRateOfAllTasks(p2)))
					return -1;
				return 0;
			}
		});
		if(users.size()>n)
			return users.subList(0, n+1);
		return users;
	}

	public int countAnswers(User user)
	{
		return user.getRatesOfAnsweredTasks().size();
	}

	public void addAchievementToUser(User user, Achievement achievement)
	{
		if(this.isUserAlreadyHasThisAchievement(user, achievement.getId()))
			return;
		List<Achievement> achievements = user.getAchievements();
		achievements.add(achievement);
		user.setAchievements(achievements);
		this.mergeUserInDB(user);
	}

	public Achievement getAchievementById(int id)
	{
		List<Achievement> achievements;

		achievements = getSessionFactory()
				.createQuery("from Achievement where id=?")
				.setParameter(0, id)
				.list();

		if(achievements.size() > 0)
			return achievements.get(0);
		else return null;
	}

	public Boolean isUserAlreadyHasThisAchievement(User user, int achievementId)
	{
		for(Achievement achievement: user.getAchievements())
			if(achievement.getId() == achievementId)
				return true;
		return false;
	}

	public Boolean isUserGetAchievementForAddTasks(User user, int countTasks)
	{
		return (user.getTasks().size() == countTasks);
	}

	public Boolean isUserGetAchievementForAddComments(User user, int countComments)
	{
		return (user.getComments().size() == countComments);
	}

	private int getCountOfRatedTask(User user)
	{
		int count = 0;
		for(Rate rate: user.getRatesOfAnsweredTasks())
			if(rate.getRate() > 0)
				count++;
		return count;
	}
	public Boolean isUserGetAchievementForAddRate(User user, int countRates)
	{
		return (this.getCountOfRatedTask(user) == (countRates - 1));
	}

	public Boolean isExistSecret(String secret)
	{
		User user = this.findUserBySecret(secret);
		if(user == null)
			return false;
		user.setSecret(null);
		return true;
	}
}
