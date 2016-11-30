package com.springapp.mvc.model;

import javax.persistence.*;

@Entity
@Table(name = "rate", catalog = "task_manager")
public class Rate
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User answeredUser;

	@ManyToOne
	@JoinColumn(name = "task_id")
	private Task answeredTask;

	@Column(name = "rate", unique = false)
	private int rate;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public User getAnsweredUser()
	{
		return answeredUser;
	}

	public void setAnsweredUser(User answeredUser)
	{
		this.answeredUser = answeredUser;
	}

	public Task getAnsweredTask()
	{
		return answeredTask;
	}

	public void setAnsweredTask(Task answeredTask)
	{
		this.answeredTask = answeredTask;
	}

	public int getRate()
	{
		return rate;
	}

	public void setRate(int rate)
	{
		this.rate = rate;
	}
}
