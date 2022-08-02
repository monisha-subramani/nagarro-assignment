package com.users.Service;

import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;

public class TaskClass extends TimerTask{
	
	@Autowired
	UserService userService;
	
	public String user;

	public TaskClass(String user) {
		this.user = user;
		// TODO Auto-generated constructor stub
	}
	@Override
	public void run() {
		
		
		
	}
	
}