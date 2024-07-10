package model;

public class InvitationDTO {
	
    private int recieverId;
    String status;
    String boardName;
    String inviterUserName;

	public int getRecieverId() {
		return recieverId;
	}
	public void setRecieverId(int inviterId) {
		this.recieverId = inviterId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getBoardName() {
		return boardName;
	}
	public void setBoardName(String boardName) 
	{
		this.boardName = boardName;
	}
	public String getInviterUserName() {
		return inviterUserName;
	}
	public void setInviterUserName(String inviterUserName) {
		this.inviterUserName = inviterUserName;
	}
	
	
	
}