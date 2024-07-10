package controller;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import model.Card;
import model.cardRequest;
import model.cardUpdateRequest;
import service.*;
import service.CardService;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("card")
public class CardController {
	@Inject
	private CardService cardService= new CardService();
	

	@POST
	@Path("addcard")
	public String add(cardRequest card) 
	{
		
		return cardService.addCard(card);
	}
	
	@PUT
	@Path("movecard")
	public String move(@QueryParam("cardId") int cardId , @QueryParam("listId") int listId)
	{
	    return cardService.moveCard(cardId, listId);
	}
	@PUT
	@Path("assign")
	public String assign(@QueryParam("cardId") int cardId , @QueryParam("userName") String userName)
	{
	    return cardService.assignCard(cardId,userName);
	}
	@PUT
	@Path("description")
	public String addDescriptionToCard(cardUpdateRequest R) {
	    return cardService.addDescriptionToCard(R);
	}
	@PUT
	@Path("comment")
	public String addCommentToCard(cardUpdateRequest R) {

	    return cardService.addCommentToCard(R);
	}
    @PUT
    @Path("edit/comment")
    public String editCommentContent(@QueryParam("cardId") int cardId,
                                     @QueryParam("commentId") int commentId,
                                     @QueryParam("request") String request) {
        return cardService.editCommentContent(cardId, commentId, request);
    }

    @PUT
    @Path("edit/description")
    public String editDescriptionContent(@QueryParam("cardId") int cardId,
                                         @QueryParam("request") String request) {
        return cardService.editDescriptionContent(cardId, request);
    }
	
	
}