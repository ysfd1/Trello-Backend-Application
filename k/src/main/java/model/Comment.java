package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Comment 
{
	@NotNull
	@Column
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;
	public Comment(){}
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public String getWriterUsername() 
	{
		return writerUsername;
	}
	public void setWriterUsername(String writerUsername) 
	{
		this.writerUsername = writerUsername;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) 
	{
		this.content = content;
	}
	public Card getCard() {
		return card;
	}
	public void setCard(Card card)
	{
		this.card = card;
	}
	@NotNull
	String writerUsername;
	@NotNull
	String content;
	@ManyToOne
	@JoinColumn(name="CardId")
	Card card;
}
