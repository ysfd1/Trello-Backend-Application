package model;



public class cardRequest 
{
	String cardcontent;
	String listname;
	String assignee;
	String description;
	public String getListname() {
		return listname;
	}
	public void setListname(String listname) {
		this.listname = listname;
	}

	public String getCardcontent() {
		return cardcontent;
	}
	public void setCardcontent(String cardcontent) {
		this.cardcontent = cardcontent;
	}

	public String getAssignee() {
		return assignee;
	}
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
