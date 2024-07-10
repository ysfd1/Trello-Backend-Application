package controller;

import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import service.ListService;


@Stateless 
@Path("list")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class ListController 

{
	@Inject
	ListService listService;
	
	@POST
	@Path("createList")
	public String createList(Map<String, String> requestBody)
	{
		return listService.createList(requestBody);
	}
	

	@DELETE
	@Path("deleteList")
	public String deleteList(Map<String, String> requestBody)
	{
		return listService.deleteList(requestBody);
	}

}