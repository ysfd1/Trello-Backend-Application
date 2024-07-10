package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Stateless
@Entity

public class BoardList implements Serializable {

	@Id
	@NotNull
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int listId;

	@Column
	@NotNull
	private String listName;

	@ManyToOne(targetEntity = Board.class)
	@JoinColumn(name = "boardName")
	@JsonBackReference
	Board board;

	@JsonManagedReference
	@OneToMany(mappedBy = "listt", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Card> cards = new ArrayList<Card>();

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}
	public void setCard(Card card) {
		this.cards.add(card);
	}

	public BoardList(@NotNull int listId, @NotNull String listName, Board board) {
		super();
		this.listId = listId;
		this.listName = listName;
		this.board = board;
	}

	public int getListId() {
		return listId;
	}

	public void setListId(int listId) {
		this.listId = listId;
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public BoardList() {
	}

	public BoardList(@NotNull String listName) {
		this.listName = listName;
	}

	public String getListName() {
		return listName;
	}

	public void setListName(String listName) {
		this.listName = listName;
	}

	public List<Card> getCards() {

		return null;
	}
}
