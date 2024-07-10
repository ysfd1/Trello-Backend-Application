package service;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import model.Board;
import model.Session;

import model.User;

@Stateless
public class UserService {
	@PersistenceContext(unitName = "trello")
	private EntityManager entityManager;

	public String registerUser(User user) {
		if (getUserByEmail(user.getEmail()) != null) {

			return "Email already registered";
		} else
			entityManager.persist(user);
		return "User registered successfully";
	}

	public String loginUser(User user) {
		User existingUser = getUserByEmail(user.getEmail());

		if (existingUser != null && existingUser.getPassword().equals(user.getPassword())) {

			Session.u = existingUser;
			User.setLoggedInUser(existingUser.getUserName());
			return "Logged in successfully";
		} else {
			return "Invalid email or password, try again!";
		}
	}
	public List<User> getAllUsers() {
		return entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
	}
	public String updateUsername (Map < String, String> requestBody)
    {
         try {
      if (Session.u == null )
          return " User not logged in " ;
    String NewName = requestBody.get("newName");
    String Email = requestBody.get("email") ; 

     User existingUser = getUserByEmail(Email);

    existingUser.setUserName(NewName);
    entityManager.merge(existingUser);
    Session.u = existingUser;
	User.setLoggedInUser(NewName);
      return "Username updated successfully ";
         }
      catch (Exception e)
         {
          e.printStackTrace();
          return "Failed to update Email ";
      }
    }

	public String updatePassword(Map<String, String> requestBody) {
		try {
			if (Session.u == null)
				return "return User not logged in ";
			String NewPass = requestBody.get("newpass");
			String Email = requestBody.get("email");

			User existingUser = getUserByEmail(Email);

			existingUser.setPassword(NewPass);
			entityManager.merge(existingUser);
			Session.u = existingUser;
			return "Password updated successfully ";
		} catch (Exception e) {
			e.printStackTrace();
			return "Failed to update password ";
		}

	}

	public String updateEmail(Map<String, String> requestBody) {
		try {
			if (Session.u == null)
				return "return User not logged in ";
			String oldEmail = requestBody.get("oldEmail");
			String newEmail = requestBody.get("newEmail");

			User existingUser = getUserByEmail(oldEmail);

			existingUser.setEmail(newEmail);
			entityManager.merge(existingUser);
			Session.u = existingUser;
			return "Email updated successfully ";
		} catch (Exception e) {
			e.printStackTrace();
			return "Failed to update Email";
		}
	}

	public User getUserByEmail(String email) {
		List<User> resultList = entityManager.createQuery("SELECT u FROM User u WHERE u.Email = :Email", User.class)
				.setParameter("Email", email).getResultList();

		if (!resultList.isEmpty()) {
			return resultList.get(0);
		} else {
			return null;
		}
	}
}
