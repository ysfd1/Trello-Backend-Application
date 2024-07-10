package service;

import java.util.*;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import model.*;

@Stateless
public class BoardService
{
	@PersistenceContext(unitName = "trello")
	EntityManager entityManager;


public String createBoard(Map<String, String> requestBody) {
    String userName = User.getLoggedInUser();
    if (userName == null)
    {
        return "No logged in user";
    }

    String boardName = requestBody.get("boardName");
    if (boardName == null || boardName.isEmpty()) {
        throw new BadRequestException("Board name is required");
    }

    try {
        User user = entityManager.createQuery("SELECT u FROM User u WHERE u.UserName = :userName", User.class)
                .setParameter("userName", userName)
                .getSingleResult();
        Board board = new Board();
        board.setBoardName(boardName);
        board.setTeamLeaderUsername(user.getUserName());
        board.setUser(user);
        user.setBoard(board); // Add board to user's collection of boards
        entityManager.persist(board);
        entityManager.merge(user);
        return board.messageApprove();
    } 
    
    catch (NoResultException ex) 
    {
        return "User not found";
    } 
    catch (PersistenceException ex) 
    {
        
        return "Board name already exists";
    }
}




public Response viewBoards() {
    String userName = User.getLoggedInUser();
    if (userName == null) {
        return Response.status(Response.Status.UNAUTHORIZED).entity("No logged in user").build();
    }

    try {
    	TypedQuery<Board> query = entityManager.createQuery(
    		    "SELECT b FROM Board b LEFT JOIN FETCH b.users u WHERE b.teamLeaderUsername = :userName", 
    		    Board.class
    		);
        query.setParameter("userName", userName);
        List<Board> boards = query.getResultList();

        for (Board board : boards) {
            board.getUsers().size(); 
        }

        return Response.ok(boards).build();

    } catch (Exception e) {
        e.printStackTrace();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error retrieving boards").build();
    }
}
	public String deleteBoard(Map<String, String> requestBody) {
	    String message;
	    String boardName = requestBody.get("boardName");
	    String userName = User.getLoggedInUser();
	    if (userName == null) 
	    {
	        return "No logged in user";
	    }
	    
	    //gets the board to check the logged in user is the Team Leader
	    TypedQuery<Board> query = entityManager.createQuery(
	        "SELECT b FROM Board b WHERE b.boardName = :boardName",
	        Board.class
	    );
	    query.setParameter("boardName", boardName);

	    try {
	        Board board = query.getSingleResult();
	        
	        //checks if the logged in user is the team leader
	        if (board.getTeamLeaderUsername().equals(userName)) 
	        {
	        	//Deletes the boardLists in that board
	        	entityManager.createQuery("DELETE FROM BoardList bl WHERE bl.board = :board")
	                    .setParameter("board", board)
	                    .executeUpdate();
	        	
	        	//Deletes the board
	            entityManager.remove(board);

	            // Delete from list of boards from user table
	            TypedQuery<User> userQuery = entityManager.createQuery(
	                "SELECT u FROM User u WHERE u.UserName = :userName",
	                User.class
	            );
	            userQuery.setParameter("userName", userName);
	            User user = userQuery.getSingleResult();
	            user.deleteBoard(board);
	            entityManager.merge(user);
	            message = "Board deleted successfully";
	            return message;
	            
	            
	        } else {
	            message = "Couldn't delete board as you're not the team leader of this board";
	            return message;
	        }
	    } catch (NoResultException e) {
	        message = "Board not found";
	        return message;
	    }
	}
	public String inviteCollaborator(Map<String, String> requestBody) 
	{
	    String message;
	    String inviterUserName = User.getLoggedInUser();
	    String receiverUserName = requestBody.get("userName");
	    String boardName = requestBody.get("boardName");
	    
	    if (inviterUserName == null) 
	    {
	        return "No logged in user";
	    }
	    

	    try {
	        TypedQuery<Board> query = entityManager.createQuery(
	            "SELECT b FROM Board b WHERE b.boardName = :boardName",
	            Board.class
	        );
	        query.setParameter("boardName", boardName);
	        Board board = null;
	        try {
	            board = query.getSingleResult();
	        } catch (NoResultException e) {
	            message = "Board not found";
	            return message;
	        }

	        // Check if the inviter user is the team leader of that board
	        if (board.getTeamLeaderUsername().equals(inviterUserName)) {
	            // Get the inviter's id by their logged in userName
	            TypedQuery<User> inviterQuery = entityManager.createQuery(
	                "SELECT u FROM User u WHERE u.UserName = :userName",
	                User.class
	            );
	            inviterQuery.setParameter("userName", inviterUserName);
	            User inviter = inviterQuery.getSingleResult();

	            // Get the receiver's id by their provided userName
	            TypedQuery<User> receiverQuery = entityManager.createQuery(
	                "SELECT u FROM User u WHERE u.UserName = :userName",
	                User.class
	            );
	            receiverQuery.setParameter("userName", receiverUserName);
	            User receiver = null;
	            try {
	                receiver = receiverQuery.getSingleResult();
	            } catch (NoResultException e) {
	                message = "Receiver user not found";
	                return message;
	            }

	            // Invite the collaborator to the specific board
	            Invitation invitation = new Invitation(inviter.getUserId(), inviterUserName, receiver.getUserId(), boardName);
	            entityManager.persist(invitation);
	            message = "Collaborator invited to board: " + boardName;
	        } else {
	            message = "Couldn't invite collaborator as you're not the team leader of this board";
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        message = "Failed to invite collaborator, an error occurred";
	    }
	    return message;
	}

	@GET
	@Path("viewInvitations")
	public Response viewInvitations() {
	    String message;
	    String recieverUserName = User.getLoggedInUser(); // Replace this with the actual logged-in user's ID
	    //int recieverId = 1;
	    
	    if (recieverUserName == null) 
	    {
	    	message="No logged in user";
            return Response.status(Response.Status.NOT_FOUND).entity(message).build();
	    }
	    
	    TypedQuery<User> query2 = entityManager.createQuery(
	            "SELECT u FROM User u WHERE u.UserName = :userName",
	            User.class
	    );
	    query2.setParameter("userName", recieverUserName);
	    User reciever = query2.getSingleResult();
	    
	    

	    TypedQuery<Invitation> query = entityManager.createQuery(
	            "SELECT r FROM Invitation r WHERE r.recieverId = :recieverId ",
	            Invitation.class
	    );

	    query.setParameter("recieverId", reciever.getUserId()); // You should get the logged-in user's ID

	    try {
	        List<Invitation> invitations = query.getResultList();

	        if (invitations.isEmpty()) {
	            message = "No invitations found for the provided IDs";
	            return Response.status(Response.Status.NOT_FOUND).entity(message).build();
	        }
	        
	        
	      

	        List<InvitationDTO> invitationDTOs = new ArrayList<>();

	        for (Invitation invitation : invitations) {
	            InvitationDTO invitationDTO = new InvitationDTO();
	            invitationDTO.setRecieverId(invitation.getRecieverId());
	            invitationDTO.setInviterUserName(invitation.getInviterUserName());
	            invitationDTO.setStatus(invitation.getIsApproved());
	            invitationDTO.setBoardName(invitation.getBoardName());
	            invitationDTOs.add(invitationDTO);
	        }

	        return Response.status(Response.Status.OK).entity(invitationDTOs).build();
	    } catch (Exception e) {
	        message = "An error occurred while processing your request";
	        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
	    }

	}

	@PUT
	@Path("updateInvitationStatus")
	public String updateInvitationStatus(Map<String, Object> requestBody) {
	    String message;
	    String inviterUserName = (String) requestBody.get("userName");
	    String approvalStr = (String) requestBody.get("approval");
	    boolean approval = Boolean.parseBoolean(approvalStr);

	    String recieverUserName = User.getLoggedInUser(); // Replace this with the actual logged-in user's ID
	    
	    if (recieverUserName == null) 
	    {
	    	message="No logged in user";
            return message;
	    }
	    
	    
	    //get the logged in user's id
	    TypedQuery<User> query2 = entityManager.createQuery(
	            "SELECT u FROM User u WHERE u.UserName = :userName",
	            User.class
	    );
	    query2.setParameter("userName", recieverUserName);
	    User reciever = query2.getSingleResult();
	    
	    //get the inviter's id
	    TypedQuery<User> query = entityManager.createQuery(
	            "SELECT u FROM User u WHERE u.UserName = :userName",
	            User.class
	    );
	    query.setParameter("userName", inviterUserName);
	    User inviterUser = new User();
	    try {
	    	inviterUser = query.getSingleResult();
	    } 
	    catch (NoResultException e) 
	    {
	        message = "Inviter user not found";
	        return message;
	    }

	    int inviterId = inviterUser.getUserId();
	    
	    //Get the invitation 
	    TypedQuery<Invitation> queryy = entityManager.createQuery(
	    	    "SELECT r FROM Invitation r WHERE r.recieverId = :recieverId AND r.inviterId = :inviterId",
	    	    Invitation.class
	    	);

	    	queryy.setParameter("recieverId", reciever.getUserId()); // Should get the logged-in user's ID
	    	queryy.setParameter("inviterId", inviterId);
	    
	    Invitation invitation;
	    try 
	    {
	        invitation = queryy.getSingleResult();
	    } 
	    catch (NoResultException e) 
	    {
	        message = "No pending invitations found for the provided receiver";
	        return message;
	    }

	    if (approval) //if the invitation is accepted the reciever is added to the list of users in that board
	    {
	        String boardName = invitation.getBoardName();
	        TypedQuery<Board> boardQuery = entityManager.createQuery(
	                "SELECT b FROM Board b WHERE b.boardName = :boardName",
	                Board.class
	        );
	        boardQuery.setParameter("boardName", boardName);
	        Board board = boardQuery.getSingleResult();

	        //collaborator is added on board
	        board.setUser(reciever);
	        entityManager.merge(board);
	        
	        // Set the invitation as approved
	        invitation.setApproved("Accepted");
	        entityManager.merge(invitation);

	        message = "You've been added as a collaborator to the board: " + boardName;
	    } 
	    else 
	    
	    {
	        // Set the invitation as rejected , doesn't add the user 
	        invitation.setApproved("Denied");
	        entityManager.merge(invitation);
	        message = "Invitation rejected";
	    }

	    return message;
	}

	
	
	
}