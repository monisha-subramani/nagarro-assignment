package com.users;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

import com.users.Utils.Roles;

public class TimerSample {
	
	
	
	public static void main(String[] args) {
		System.out.println("Before Timer task");
		
		LocalDate todayDate = LocalDate.now();
		
		System.out.println(todayDate);
		
		LocalDate pre = todayDate.minusMonths(3);
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
        System.out.println(formatter.format(pre));
		
		System.out.println(pre);
		
		System.out.println("After Timer task");
	}
	
	public static void timertask() {
		Timer timer = new Timer();
		
		timer.schedule(new NewTask("Sucess"), 5*1000);
	}
	
	

}

class NewTask extends TimerTask{
	public String val;

	public NewTask(String val) {
		this.val = val;
		// TODO Auto-generated constructor stub
	}
	@Override
	public void run() {
		System.out.println("Just simple message::: "+val);
		
	}
	
}
