package com.springapp.mvc.model;

import org.apache.solr.analysis.LowerCaseFilterFactory;
import org.apache.solr.analysis.SnowballPorterFilterFactory;
import org.apache.solr.analysis.StandardTokenizerFactory;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Parameter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Indexed
@AnalyzerDef(name = "customanalyzer",
		tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
		filters = {
				@TokenFilterDef(factory = LowerCaseFilterFactory.class),
				@TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = {
						@Parameter(name = "language", value = "English")
				})
		})
@Table(name = "tasks", catalog = "task_manager")
public class Task
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id", unique = true, nullable = false)
	private int id;

	@Field
	@Analyzer(definition = "customanalyzer")
	@Column(name = "title", unique = true, nullable = false)
	private String title;

	@Field
	@Analyzer(definition = "customanalyzer")
	@Column(name = "text", unique = false, nullable = false)
	private String text;

	@Field
	@Analyzer(definition = "customanalyzer")
	@Column(name = "type", unique = false, nullable = false)
	private String type;

	@Field
	@Analyzer(definition = "customanalyzer")
	@Column(name = "difficulty", unique = false, nullable = false)
	private int difficulty;

	@IndexedEmbedded
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@IndexedEmbedded
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "task", cascade = CascadeType.REMOVE)
	@Fetch(FetchMode.SUBSELECT)
	private List<Answer> answers;

	@IndexedEmbedded
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "answeredTask", cascade = CascadeType.REMOVE)
	@Fetch(FetchMode.SUBSELECT)
	private List<Rate> ratesByUsers;

	@IndexedEmbedded
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "task", cascade = CascadeType.REMOVE)
	@Fetch(FetchMode.SUBSELECT)
	private List<Comment> comments;

	@IndexedEmbedded
	@ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@Fetch(FetchMode.SUBSELECT)
	@JoinTable(name = "task_tag", catalog = "task_manager", joinColumns = {
			@JoinColumn(name = "task_id", nullable = false, updatable = true, insertable = true) },
			inverseJoinColumns = { @JoinColumn(name = "tag_id", nullable = false, updatable = true, insertable = true) })
	private List<Tag> tags;

	public List<Tag> getTags() {
		return this.tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public List<Rate> getRatesByUsers()
	{
		return ratesByUsers;
	}

	public void setRatesByUsers(List<Rate> RatesByUsers)
	{
		this.ratesByUsers = RatesByUsers;
	}

	public List<Answer> getAnswers()
	{
		return answers;
	}

	public void setAnswers(List<Answer> answers)
	{
		this.answers = answers;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public int getDifficulty()
	{
		return difficulty;
	}

	public void setDifficulty(int difficulty)
	{
		this.difficulty = difficulty;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	public List<Comment> getComments()
	{
		return comments;
	}

	public void setComments(List<Comment> comments)
	{
		this.comments = comments;
	}

	public String getFormatedText() {
		if(this.getText().length() < 100)
			return this.getText();
		return this.getText().substring(0, 100).concat("...");
	}
}
