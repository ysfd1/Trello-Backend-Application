package controller;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import model.User;
import service.UserService;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateless
@Path("user")

public class UserController {

	@Inject
	private UserService userService;

	@POST
	@Path("register")
	public String registerUser(User user) {
		return userService.registerUser(user);
	}

	@POST
	@Path("login")
	public String loginUser(User user) {
		return userService.loginUser(user);

	}

	@GET
	@Path("all")
	public List<User> getAllUsers() {
		return userService.getAllUsers();
	}

	@PUT
	@Path("updateUserName")
	public String updateUsername(Map<String, String> requestBody) {
		return userService.updateUsername(requestBody);
	}

	@PUT
	@Path("updateUserPass")
	public String updatePassword(Map<String, String> requestBody) {
		return userService.updatePassword(requestBody);
	}

	@PUT
	@Path("updateUserEmail")
	public String updateEmail(Map<String, String> requestBody) {
		return userService.updateEmail(requestBody);
	}

	@GET
	@Path("loggedInUser")
	public String getEmail() {
		return User.getLoggedInUser();
	}
}
