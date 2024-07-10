package controller;

import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.Session;
import model.User;
import service.BoardService;


@Stateless 
@Path("board")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BoardController 
{
	@Inject
	BoardService boardService ;
	
	@POST
	@Path("createBoard")
	public String createBoard(Map<String, String> requestBody)
	{
		return boardService.createBoard(requestBody);
	}
	
	@GET	
	@Path("viewBoards")
	public Response viewBoards() 
	{
		return boardService.viewBoards();
		
	}

	@DELETE
	@Path("deleteBoard")
	public String deleteBoard(Map<String, String> requestBody)
	{
		return boardService.deleteBoard(requestBody);
		
	}
	
	@POST
	@Path("invite")
	public String inviteCollaborator(Map<String, String> requestBody)
	{
		return boardService.inviteCollaborator(requestBody);
	}
	
	
	@GET
	@Path("viewInvitations")
	public Response viewInvitations()
	{
		return boardService.viewInvitations();
	}
	
	
	@PUT
	@Path("updateInvitationStatus")
	public String updateInvitationStatus(Map<String, Object> requestBody)
	{
		return boardService.updateInvitationStatus(requestBody);
	}
	

}