package model;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.*;
import javax.persistence.*;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Stateless
@Entity
public class Card {
	@NotNull
	@Column
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int CardId;
	
	
	@NotNull
	@Column
	String CardContent;
	
	@ManyToOne
    @JoinColumn(name = "list_id")
    @JsonBackReference
    BoardList listt= new BoardList();
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_card", joinColumns = @JoinColumn(name = "card_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	@NotNull
	@Column
	List<User> assignees = new ArrayList();
	@Column
	String description;
	@Column
	@OneToMany(mappedBy="card")
	List<Comment> comments = new ArrayList();
	
	public int getCardId() {
		return CardId;
	}
	public void setCardId(int cardId) {
		CardId = cardId;
	}
	public String getCardContent() {
		return CardContent;
	}
	public void setCardContent(String cardContent) {
		CardContent = cardContent;
	}
	public BoardList getListt() {
		return listt;
	}
	public void setListt(BoardList listt) {
		this.listt = listt;
	}
	public List<User> getAssignee() {
		return assignees;
	}
	public Card(@NotNull int cardId, @NotNull String cardContent, BoardList listt, @NotNull List<User> assignees,
			String description) {
		super();
		CardId = cardId;
		CardContent = cardContent;
		this.listt = listt;
		this.assignees = assignees;
		this.description = description;
	}
	public Card() {}
	public void setAssignee(User assignee)
	{
		assignees.add(assignee);
	}
	public void setAssignees(List<User> assignees)
	{
		this.assignees=assignees;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<Comment> getComments() {
		return comments;
	}
	public void setComments(List<Comment> Comments)
	{
		comments=Comments;
	}
	public void setComment(Comment comment) {
		comments.add(comment);
	}
}
