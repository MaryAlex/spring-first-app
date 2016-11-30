package com.springapp.mvc.model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;


@Entity
@Table(name="users", catalog = "task_manager")
public class User
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@Column(name = "nickname", unique = true, nullable = false, length = 255)
	private String nickname;

	@Column(name = "password", unique = false, nullable = false, length = 255)
	private String password;

	@Column(name = "username", unique = false, nullable = false, length = 255)
	private String username;

	@Column(name = "email", unique = true, nullable = false)
	private String email;

	@Column(name = "answers_rate", unique = false, nullable = false)
	private double answersRate;

	@Column(name = "secret", unique = false, nullable = false)
	private String secret;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
	@Fetch(FetchMode.SUBSELECT)
	private List<Task> tasks;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "answeredUser")
	@Fetch(FetchMode.SUBSELECT)
	private List<Rate> RatesOfAnsweredTasks;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
	@Fetch(FetchMode.SUBSELECT)
	private List<Comment> comments;

	@ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
	@Fetch(FetchMode.SUBSELECT)
	@JoinTable(name = "user_achievement", catalog = "task_manager", joinColumns = {
			@JoinColumn(name = "user_id", nullable = false, updatable = false) },
			inverseJoinColumns = { @JoinColumn(name = "achievement_id", nullable = false, updatable = false) })
	private List<Achievement> achievements;

	public List<Rate> getRatesOfAnsweredTasks()
	{
		return RatesOfAnsweredTasks;
	}

	public void setRatesOfAnsweredTasks(List<Rate> RatesOfAnsweredTasks)
	{
		this.RatesOfAnsweredTasks = RatesOfAnsweredTasks;
	}

	public String getSecret()
	{
		return secret;
	}

	public void setSecret(String secret)
	{
		this.secret = secret;
	}

	public List<Task> getTasks()
	{
		return tasks;
	}

	public void setTasks(List<Task> tasks)
	{
		this.tasks = tasks;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getNickname()
	{
		return nickname;
	}

	public void setNickname(String nickname)
	{
		this.nickname = nickname;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public double getAnswersRate()
	{
		return answersRate;
	}

	public void setAnswersRate(double answersRate)
	{
		this.answersRate = answersRate;
	}

	public List<Comment> getComments()
	{
		return comments;
	}

	public void setComments(List<Comment> comments)
	{
		this.comments = comments;

	}
	public List<Achievement> getAchievements()
	{
		return achievements;
	}

	public void setAchievements(List<Achievement> achievements)
	{
		this.achievements = achievements;
	}
}