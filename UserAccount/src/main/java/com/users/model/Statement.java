package com.users.model;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Statement {
	
	public Statement() {
		
	}
	
	private double account_id;
	
	private int id;
	
	private Date datefield;
	
	private double amount;

	public double getAccount_id() {
		return account_id;
	}

	public void setAccount_id(double account_id) {
		this.account_id = account_id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDatefield() {
		return datefield;
	}

	public void setDatefield(Date datefield) {
		this.datefield = datefield;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Statement(double account_id, int id, Date datefield, double amount) {
		super();
		this.account_id = account_id;
		this.id = id;
		this.datefield = datefield;
		this.amount = amount;
	}

	

	
	
	

}
