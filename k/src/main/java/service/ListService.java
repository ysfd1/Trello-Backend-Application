package service;

import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.BadRequestException;

import model.Board;
import model.BoardList;
import model.Session;
import model.User;


@Stateless
public class ListService 
{
	@PersistenceContext(unitName = "trello")
	EntityManager entityManager;
	


	public String createList(Map<String, String> requestBody) //don't delete from array 
	{
		String message;
		
	    String boardName = requestBody.get("boardName");
	    String listName = requestBody.get("listName");
	    
	    String loggedInUserName=User.getLoggedInUser();
	    if (loggedInUserName == null) 
        {
            message="No logged in user";
            return message;
        }
	    
	    TypedQuery<Board> query = entityManager.createQuery(
	            "SELECT b FROM Board b WHERE b.boardName = :boardName",
	            Board.class
	        );
	        query.setParameter("boardName", boardName);
	        
	        try 
	        {		
	        		
	        		Board board = query.getSingleResult();
	        		if(board.getTeamLeaderUserName().equals(loggedInUserName))
	        		{		
	        		BoardList boardList=new BoardList();
	        		boardList.setListName(listName);
	        		boardList.setBoard(board);
	        		entityManager.persist(boardList);
	        		board.setList(boardList);
	        		entityManager.merge(board);
	                message = "List successfully created";
	                return message;
	        } 
	        		
	        		else
	        		{
	        			message="Couldn't create list as you're not the team leader";
	        			return message;
	        		}
	        }		
	        
	        catch (NoResultException e) 
	        {
	            message = "Board not found";
	            return message;
	        }
	        

		 
	}
	
	public String deleteList(Map<String, String> requestBody) {
	    String message;
	    String loggedInUserName = User.getLoggedInUser();
	    if (loggedInUserName == null) 
        {
            message="No logged in user";
            return message;
        }
	    String boardName = requestBody.get("boardName");
	    String listName = requestBody.get("listName");

	    TypedQuery<BoardList> query = entityManager.createQuery(
	        "SELECT l FROM BoardList l WHERE l.listName = :listName",
	        BoardList.class
	    );
	    query.setParameter("listName", listName);

	    try {
	        // DB delete
	        BoardList list = query.getSingleResult();

	        // List delete from list of board list
	        TypedQuery<Board> boardQuery = entityManager.createQuery(
	            "SELECT b FROM Board b WHERE b.boardName = :boardName",
	            Board.class
	        );
	        boardQuery.setParameter("boardName", boardName); // Bind the value for boardName parameter
	        Board board = boardQuery.getSingleResult();

	        if (board.getTeamLeaderUserName().equals(loggedInUserName)) {
	            entityManager.remove(list);
	            board.deleteList(list);
	            message = "List deleted successfully";
	        } else {
	            message = "Couldn't delete the list as you're not the team leader";
	        }
	    } catch (NoResultException e) {
	        message = "List not found";
	    }

	    return message;
	}
	
}