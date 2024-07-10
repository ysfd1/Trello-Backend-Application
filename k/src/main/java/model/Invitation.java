package model;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;


@Stateless
@Entity
public class Invitation implements Serializable
{

	@Id
    @NotNull
    @Column
    private int inviterId;
	
	@Id
    @NotNull
    @Column
    private int recieverId;
	
	@Column
	@NotNull
	private String inviterUserName;
    
	
	@NotNull
	@Column
	private String boardName;
	
	
	@NotNull
	@Column
	private String isApproved="Pending";
	
	public Invitation() {}
	
	public Invitation(@NotNull int inviterId, @NotNull String inviterUserName ,@NotNull int recieverId, String boardName) {
		super();
		this.inviterUserName=inviterUserName;
		this.inviterId = inviterId;
		this.recieverId = recieverId;
		this.boardName=boardName;
		this.isApproved = "Pending";
		
	}
	
	

	public String getInviterUserName() {
		return inviterUserName;
	}

	public void setInviterUserName(String inviterUserName) {
		this.inviterUserName = inviterUserName;
	}

	public String getBoardName() {
		return boardName;
	}

	public void setBoardName(String boardName) {
		this.boardName = boardName;
	}

	public Invitation(User inviter, int recieverId) {
		super();
		this.inviterId = inviterId;
		this.recieverId = recieverId;
		isApproved="Pending";
	}

	public int getInviterId() {
		return inviterId;
	}

	public void setInviterId(int inviterId) {
		this.inviterId = inviterId;
	}

	public int getRecieverId() {
		return recieverId;
	}

	public void setRecieverId(int recieverId) {
		this.recieverId = recieverId;
	}

	public String getIsApproved() {
		return isApproved;
	}

	public void setApproved(String isApproved) {
		this.isApproved = isApproved;
	}


	
	
}