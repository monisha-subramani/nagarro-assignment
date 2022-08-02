package com.users.Utils;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.users.Service.UserService;
import com.users.controller.UserController;
import com.users.model.User;

public class SchedulerTask extends TimerTask{
	
	
	TokenHolder tokenHolder;
	
	
	
	public String token;
	
	UserService userService;
	
	Logger logger = LoggerFactory.getLogger(UserController.class);

	public SchedulerTask(String token, TokenHolder tokenHolder, UserService userService) {
		
		this.token = token;
		this.tokenHolder = tokenHolder;
		this.userService = userService;
	}
	
	@Override
	public void run() {
		logger.trace("Session Invalidation: Start of User session invalidation");
		tokenHolder.removeToken(token);
		logger.trace("Session Invalidation: User session removed from cache");
		
		User user = userService.findUserByToken(token);
		
		if(user != null) {
			user.setToken("");
			userService.updateUser(user);
			logger.trace("Session Invalidation: User session token removed from user in DB");
		}
		logger.info("Session Invalidation: User session invalidated for session token : "+token);
	}
	
}