package com.springapp.mvc.model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "tags", catalog = "task_manager")
public class Tag
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id", unique = true, nullable = false)
	private int id;

	@Column(name = "tag", unique = true, nullable = false)
	private String tag;

	@ManyToMany(fetch = FetchType.EAGER, mappedBy = "tags", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@Fetch(FetchMode.SUBSELECT)
	private List<Task> tasks;

	public List<Task> getTasks() {
		return this.tasks;
	}

	public void setTasks(List<Task> tasks) {
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

	public String getTag()
	{
		return tag;
	}

	public void setTag(String tag)
	{
		this.tag = tag;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Tag tag1 = (Tag) o;

		if (id != tag1.id) return false;
		if (tag != null ? !tag.equals(tag1.tag) : tag1.tag != null) return false;
		return !(tasks != null ? !tasks.equals(tag1.tasks) : tag1.tasks != null);

	}

	@Override
	public int hashCode()
	{
		int result = id;
		result = 31 * result + (tag != null ? tag.hashCode() : 0);
		result = 31 * result + (tasks != null ? tasks.hashCode() : 0);
		return result;
	}
}
