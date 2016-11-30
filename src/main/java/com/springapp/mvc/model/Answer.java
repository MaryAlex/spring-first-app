package com.springapp.mvc.model;

import javax.persistence.*;

@Entity
@Table(name = "answers", catalog = "task_manager")
public class Answer

{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id", unique = true, nullable = false)
	private int id;

	@Column(name = "text", unique = false, nullable = false)
	private String text;

	@ManyToOne
	@JoinColumn(name = "task_id")
	private Task task;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public Task getTask()
	{
		return task;
	}

	public void setTask(Task task)
	{
		this.task = task;
	}
}
