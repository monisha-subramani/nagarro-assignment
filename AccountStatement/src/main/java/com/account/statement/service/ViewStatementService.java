package com.account.statement.service;


import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.hsqldb.StatementSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.account.statement.entity.Account;
import com.account.statement.entity.Statement;
import com.google.common.hash.Hashing;

@Service
public class ViewStatementService {
	
	@Autowired
	  JdbcTemplate jdbcTemplate;
	
	
	 public Account getstatementsByAccountID(int accountId) {
	        
	       List<Account> acclist = 
	                jdbcTemplate.query("select id, account_type, account_number from account where id = "+accountId, 
	               		new BeanPropertyRowMapper<Account>(Account.class));
	        
	      
	        Account account = null;
	        if(acclist.size() > 0) {
	        	
	        	
	        	account = acclist.get(0);
	        	String sha256hex = Hashing.sha256()
	        			  .hashString(account.getAccount_number(), StandardCharsets.UTF_8)
	        			  .toString();
	        	
	        	account.setAccount_number(sha256hex);

	        	List<Statement> statementList = getStatementList(accountId);
				
				
	        	account.setStatementList(statementList);
	        	
	        	
	        }
	      
	       
	        return account;
	      

	    }
	 
	 public Account getStatementByAmountRange(int accountId, double fromAmt, double toAmt) {
		 Account account = getstatementsByAccountID(accountId);
		 List<Statement> statementsByAmount = account.getStatementList().stream().filter(statement -> statement.getAmount() > fromAmt &&  statement.getAmount() < toAmt).collect(Collectors.toList());
     	 account.setStatementList(statementsByAmount);
     	 
     	 return account;
		 
	 }
	 
	 public List<Statement> getStatementList(int accountId){
		 String query = "select id, account_id, datefield, amount from statement where account_id = "+accountId;
     	List<Statement> statementList = jdbcTemplate.query(query, new RowMapper<Statement>() {

				@Override
				public Statement mapRow(ResultSet rs, int rowNum) throws SQLException {
					// TODO Auto-generated method stub
					Date dateField = null;
					try {
						
						dateField = new SimpleDateFormat("dd.MM.yyyy").parse(rs.getString("datefield").toString());
					} catch (ParseException e) {
						
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					double amount = Double.parseDouble(rs.getString("amount"));
					return new Statement(rs.getDouble("account_id"), rs.getInt("id"), dateField, amount);
				}
     		
     	});
     	
     	return statementList;
	 }

	public Account getStatementByDateRange(int accountId, Date from, Date to) {
		 Account account = getstatementsByAccountID(accountId);
		 List<Statement> statementsByDate = account.getStatementList().stream().filter(statement -> statement.getDatefield().getTime() > from.getTime() &&  statement.getDatefield().getTime() < to.getTime()).collect(Collectors.toList());
     	 account.setStatementList(statementsByDate);
     	 
     	 return account;
	}

}
