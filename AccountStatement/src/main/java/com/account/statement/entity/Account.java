package com.account.statement.entity;

import java.util.List;
import java.util.TreeSet;

public class Account {
	
	
	private int id;
	
	@Override
	public String toString() {
		return "Account [id=" + id + ", account_type=" + account_type + ", accoount_number=" + account_number + "]";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAccount_type() {
		return account_type;
	}

	public void setAccount_type(String account_type) {
		this.account_type = account_type;
	}

	

	public String getAccount_number() {
		return account_number;
	}

	public void setAccount_number(String account_number) {
		this.account_number = account_number;
	}



	private String account_type;
	
	private String account_number;
	
	private List<Statement> statementList;
	
	

	public List<Statement> getStatementList() {
		return statementList;
	}

	public void setStatementList(List<Statement> statementList) {
		this.statementList = statementList;
	}

}
