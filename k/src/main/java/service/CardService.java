package service;


import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.*;
import javax.transaction.Transactional;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import Messaging.Producer;
import model.*;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class CardService {
	public CardService() {
	}

	@PersistenceContext(unitName = "trello")
	private EntityManager entityManager;

	@Inject
	Producer p;

	public String addCard(cardRequest R) {
		try {
			User user = Session.u;
			if (user == null) {
				return "you're not logged in, please login first.";
			}

			if (R.getAssignee() == null) {
				return "Please assign the card";
			}

			if (R.getCardcontent() == null) {
				return "Please provide card content";
			}

			if (R.getListname() == null) {
				return "Invalid list";
			}

			Card card = new Card();
			card.setCardContent(R.getCardcontent());
			card.setDescription(R.getDescription());
			TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u WHERE u.UserName =:username",
					User.class);
			query.setParameter("username", R.getAssignee());
			User assignee = query.getSingleResult();
			card.setAssignee(assignee);

			TypedQuery<BoardList> query2 = entityManager
					.createQuery("SELECT l FROM BoardList l WHERE l.listName = :listName", BoardList.class);
			query2.setParameter("listName", R.getListname());
			BoardList list = query2.getSingleResult();

			TypedQuery<Board> query3 = entityManager.createQuery("SELECT b FROM Board b WHERE b.boardName = :boardName",
					Board.class);
			query3.setParameter("boardName", list.getBoard().getBoardName());
			Board board = query3.getSingleResult();
			for (User member : board.getUsers()) {
				if (member.getUserName().equals(user.getUserName())) {
					card.setListt(list);
					list.setCard(card);
					// user.setCard(card);
					entityManager.persist(card);
					entityManager.merge(user);
					entityManager.merge(list);
					entityManager.merge(board);
					return "Card created successfully.";
				}
			}
			return "You're not a member in this board.";
		} catch (NoResultException e) {
			e.printStackTrace();
			return "No result exception";
		} catch (PersistenceException e) {
			e.printStackTrace();
			return "Persistence exception";
		}
	}

	public String moveCard(int cardId, int listId) {
	    try {
	        if (Session.u == null) {
	            return "You're not logged in. Please login first.";
	        }
	        
	        Card card = entityManager.find(Card.class, cardId);
	        BoardList newList = entityManager.find(BoardList.class, listId);
	        
	        if (card != null && newList != null) {
	            card.setListt(newList);
	            entityManager.merge(card);
	            
	            List<String> msgs = new ArrayList<>();
	            List<String> userNames = getUsersWithCommentsOnCard(cardId);
	            List<String> tempList = new ArrayList<>();
	            
	            for (String userName : userNames) {
	                if (!tempList.contains(userName) && !userName.equals(Session.u.getUserName())) {
	                    tempList.add(userName);
	                }
	            }
	            
	            for (String tempUser : tempList) {
	                msgs.add("Dear " + tempUser + ", card \"" + card.getCardContent() + "\" has been moved to " + newList.getListName());
	            }
	            
	            p.sendMessages(msgs);
	            p.sendMessage("Dear Assignee: " + card.getAssignee() + ", card \"" + card.getCardContent() + "\" has been moved to " + newList.getListName());
	            
	            return "Card moved successfully!";
	        } else {
	            return "Card or List not found!";
	        }
	    } catch (javax.persistence.NoResultException e) {
	        return "No card or list found with the provided IDs.";
	    } catch (Exception e) {
	        e.printStackTrace();
	        return "Exception occurred";
	    }
	}

	@Transactional
	public String assignCard(int cardId, String Username) {
		try {
			if (Session.u == null) {
				return "please login first.";
			}
			User assigner = Session.u;
			Card card = entityManager.find(Card.class, cardId);
			TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u WHERE u.UserName = :username",
					User.class);
			query.setParameter("username", Username);
			User assignee = query.getSingleResult();
			if (card == null || assignee == null) {
				return "assignee or card not found.";
			}
			Board board = card.getListt().getBoard();
			List<User> users = board.getUsers();
			boolean isAssignerMember = users.stream()
					.anyMatch(user -> user.getUserName().equals(assigner.getUserName()));
			boolean isAssigneeMember = users.stream()
					.anyMatch(user -> user.getUserName().equals(assignee.getUserName()));
			if (isAssignerMember && isAssigneeMember) {
				card.setAssignee(assignee);
				entityManager.merge(card);
				p.sendMessage("Card "+card.getCardContent()+" has been assigned to" + assignee.getUserName());
				return "Card assigned successfully";
			} else {
				return "Access denied.";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "Exception occurred";
		}
	}

	@Transactional
	public String addDescriptionToCard(cardUpdateRequest R) {
		try {
			if (Session.u == null)
				return "Login to add description";
			int cardId = R.getId();
			String description = R.getAddition();
			Card card = entityManager.find(Card.class, cardId);
			if (card == null) {
				return "no such card exists";
			}
			card.setDescription(description);
			entityManager.merge(card);
			List<String> msgs = new ArrayList<>();
			List<String> Usernames = getUsersWithCommentsOnCard(cardId);
			List<String> TempList = new ArrayList<>();
			for (String username : Usernames) {
				if (!TempList.contains(username) && !username.equals(Session.u.getUserName())) {
					TempList.add(username);
				}
			}
			for (int i = 0; i < TempList.size(); i++) {
				msgs.add("Dear, " + TempList.get(i) + " a description was added to " + card.getCardContent());
			}
			p.sendMessages(msgs);
			String msg="Dear Assignee: "+card.getAssignee()+" card "+ card.getCardContent() +" has new description " ;
			p.sendMessage(msg);
			return "description added";
		} catch (Exception e) {
			e.printStackTrace();
			return "Exception occurred";
		}
	}

	@Transactional
	public String addCommentToCard(cardUpdateRequest R) {
		try {
			if (Session.u == null)
				return "Login to add comments";
			int cardId = R.getId();
			Comment c = new Comment();
			c.setWriterUsername(Session.u.getUserName());
			c.setContent(R.getAddition());
			Card card = entityManager.find(Card.class, cardId);
			if (card == null) {
				return "no such card exists";
			}
			 Board board = card.getListt().getBoard();
	         boolean isUserMember = board.getUsers().stream()
	                 .anyMatch(user -> user.getUserName().equals(Session.u.getUserName()));

	         if (!isUserMember)
	         {
	             return "You are not a member of the board. Cannot edit card description.";
	         }
			List<Comment> comments = card.getComments();
			comments.add(c);
			c.setCard(card);
			entityManager.persist(c);
			entityManager.merge(card);
			List<String> msgs = new ArrayList<>();
			List<String> Usernames = getUsersWithCommentsOnCard(cardId);
			List<String> TempList = new ArrayList<>();
			for (String username : Usernames) {
				if (!TempList.contains(username) && !username.equals(Session.u.getUserName())) {
					TempList.add(username);
				}
			}
			for (int i = 0; i < TempList.size(); i++) {
				msgs.add("Dear, " + TempList.get(i) + " a comment was added to " + card.getCardContent());
				
			}
			p.sendMessages(msgs);
			String msg="Dear Assignee: "+card.getAssignee()+" a comment was added to " + card.getCardContent();
			p.sendMessage(msg);
			return "comment added";
		} catch (Exception e) {
			e.printStackTrace();
			return "Exception occurred";
		}
	}

	public List<String> getUsersWithCommentsOnCard(int cardId) {
		try {
			TypedQuery<Comment> query = entityManager
					.createQuery("SELECT c FROM Comment c WHERE c.card.CardId = :cardId", Comment.class);
			query.setParameter("cardId", cardId);
			List<Comment> comments = query.getResultList();
			List<String> usernames = new ArrayList<>();
			for (Comment comment : comments) {
				usernames.add(comment.getWriterUsername());
			}
			return usernames;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
	 @Transactional
	    public String editCommentContent(int cardId, int commentId, String request) {
	        try {
	            Comment comment = entityManager.find(Comment.class, commentId);
	            if (comment == null) {
	                return "Comment not found.";
	            }
	            if (comment.getCard().getCardId() != cardId) {
	                return "Comment does not belong to the specified card.";
	            }
	            if(!comment.getWriterUsername().equals(Session.u.getUserName()))
	            	return "You have no access to edit this comment.";
	            comment.setContent(request);
	            entityManager.merge(comment);
	            List<String> msgs = new ArrayList<>();
				List<String> Usernames = getUsersWithCommentsOnCard(cardId);
				List<String> TempList = new ArrayList<>();
				for (String username : Usernames) {
					if (!TempList.contains(username) && !username.equals(Session.u.getUserName())) {
						TempList.add(username);
					}
				}
				for (int i = 0; i < TempList.size(); i++) {
					msgs.add("Dear, " + TempList.get(i) + " a comment was edited in card " + comment.getCard().getCardContent());
				}
				p.sendMessages(msgs);
				String msg="Dear Assignee: "+comment.getCard().getAssignee()+" a comment was edited in card " + comment.getCard().getCardContent();
				p.sendMessage(msg);
	            return "Comment content updated successfully.";
	        } catch (Exception e) {
	            e.printStackTrace();
	            return "Exception occurred while editing comment content.";
	        }
	    }

	 @Transactional
	 public String editDescriptionContent(int cardId, String request) {
	     try {
	         if (Session.u == null) {
	             return "Please log in to edit card description.";
	         }

	         Card card = entityManager.find(Card.class, cardId);
	         if (card == null) {
	             return "Card not found.";
	         }

	         Board board = card.getListt().getBoard();

	         boolean isUserMember = board.getUsers().stream()
	                 .anyMatch(user -> user.getUserName().equals(Session.u.getUserName()));

	         if (!isUserMember)
	         {
	             return "You are not a member of the board. Cannot edit card description.";
	         }
	         card.setDescription(request);
	         entityManager.merge(card);
	         
	         List<String> msgs = new ArrayList<>();
				List<String> Usernames = getUsersWithCommentsOnCard(cardId);
				List<String> TempList = new ArrayList<>();
				for (String username : Usernames) {
					if (!TempList.contains(username) && !username.equals(Session.u.getUserName())) {
						TempList.add(username);
					}
				}
				for (int i = 0; i < TempList.size(); i++) 
				{
					msgs.add("Dear, " + TempList.get(i) + " description was edited in card " + card.getCardContent());
				}
				p.sendMessages(msgs);
				String msg="Dear Assignee: "+card.getAssignee()+" description was edited in card " + card.getCardContent();
				p.sendMessage(msg);
	         return "Card description updated successfully.";
	     } 
	     catch (Exception e) 
	     {
	         e.printStackTrace();
	         return "Exception occurred while editing card description.";
	     }
	 }



}