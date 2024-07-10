package model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
//@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"UserName"}))
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int UserId;
	@NotNull
	String UserName;
	@NotNull
	String Password;
	@NotNull
	String Email;

	static String loggedInUser;

	@ManyToMany(mappedBy = "users", fetch = FetchType.EAGER)
	@JsonIgnore
	private List<Board> boards = new ArrayList<>();

	@ManyToMany(mappedBy = "assignees")
	@JsonIgnore
	private List<Card> userCards = new ArrayList<Card>();

	public int getUserId() {
		return UserId;
	}
	public void setCards(List<Card> cards) {
		this.userCards = cards;
	}
	public void setCard(Card card) {
		this.userCards.add(card);
	}
	public void setUserId(int userId) {
		UserId = userId;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public String getEmail() {
		return Email;
	}

	public User() {

	}

	public User(@NotNull String userName, @NotNull String password, @NotNull String email) {
		UserName = userName;
		Password = password;
		Email = email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public void deleteBoard(Board board) {
		boards.remove(board);
	}

	public List<Board> getBoards() {
		return boards;
	}

	public void setBoards(List<Board> boards) {
		this.boards = boards;
	}

	public void setBoard(Board board) {

		boards.add(board);
	}

	public static void setLoggedInUser(String userName) {
		User.loggedInUser = userName;
	}

	public static String getLoggedInUser() {
		return loggedInUser;
	}

}
