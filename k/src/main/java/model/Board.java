package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Stateless
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"boardName"}))
public class Board 
{
	@Id
	@NotNull
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int boardID;
	
	@NotNull
	@Column(unique = true)
	private String boardName;
	
	@Column
	@NotNull
	private String teamLeaderUsername;
	
	@OneToMany(mappedBy="board",fetch=FetchType.EAGER)
	@JsonManagedReference
	private List<BoardList>lists=new ArrayList();
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(
		name="BoardXUser",
		joinColumns=@JoinColumn(name="boardID"),
		inverseJoinColumns=@JoinColumn(name="UserId"))
	private List<User> users=new ArrayList();
	
	
	public Board() {} //Default constructor
	


	public Board(@NotNull int boardID, @NotNull String boardName, List<BoardList> lists, List<User> users) {
		super();
		this.boardID = boardID;
		this.boardName = boardName;
		this.lists = lists;
		this.users = users;
	}
	

	public String getTeamLeaderUsername() {
		return teamLeaderUsername;
	}



	public void setTeamLeaderUsername(String teamLeaderUsername) {
		this.teamLeaderUsername = teamLeaderUsername;
	}



	public List<User> getUsers() {
		return users;
	}



	public void setUsers(List<User> users) {
		this.users = users;
	}


	public void setUser(User recieverUser)
	{
		users.add(recieverUser);
	}

	public String getBoardName() {
		return boardName;
	}


	public void setBoardName(String boardName) {
		this.boardName = boardName;
	}

	public int getBoardID() {
		return boardID;
	}

	public void setBoardID(int boardID) {
		this.boardID = boardID;
	}

	public List<BoardList> getLists() {
		return lists;
	}

	public void setLists(List<BoardList> lists) {
		this.lists = lists;
		
	}
	
	public void deleteList(BoardList boardList)
	{
		lists.remove(boardList);
	}
	
	public void setList(BoardList list) {
		lists.add(list);
	}
	
	
	public void setTeamLeaderUserName(String teamLeaderUsername)
	{
		this.teamLeaderUsername=teamLeaderUsername;
	}
	
	
	public String getTeamLeaderUserName()
	{
		return teamLeaderUsername;
	}

public String messageApprove()
{
	String message="Board created Successfully";
	return message;
	
}

public String messageError()
{
	String message="Couldn't create new board as this name already exists";
	return message;
	
}




}
