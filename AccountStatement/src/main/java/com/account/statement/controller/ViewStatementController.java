package com.account.statement.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.account.statement.entity.Account;
import com.account.statement.service.ViewStatementService;

@RestController
@RequestMapping("/statements")
public class ViewStatementController {
	
	@Autowired
	ViewStatementService viewStatementService;
	
	
	
	@GetMapping("/transactionHistory/{accountId}")
    public ResponseEntity getTransactionList(@PathVariable int accountId) {

       
		Account account =  viewStatementService.getstatementsByAccountID(accountId);
		
		
		
		if(account != null) {
			return ResponseEntity.ok(account);
		}else {
			return new ResponseEntity<>("There is no data to display",new HttpHeaders(),HttpStatus.NO_CONTENT);
		}
        //return ResponseEntity.ok(account);

    }
	
	
	@GetMapping("/statementsByAmount")
    public ResponseEntity getStatementsbyAmount(@RequestParam("id") int accountId, @RequestParam("from") double fromAmt, @RequestParam("to") double toAmt) {
		
		Account account =  viewStatementService.getStatementByAmountRange(accountId,fromAmt,toAmt);
		
		if(account != null) {
			return ResponseEntity.ok(account);
		}else {
			return new ResponseEntity<>("There is no data to display",new HttpHeaders(),HttpStatus.NO_CONTENT);
		}
       
    }
	
	
	@GetMapping("/statementsByDate")
    public ResponseEntity getStatementsbyDate(@RequestParam("id") int accountId, @RequestParam("from") String fromDate, @RequestParam("to") String toDate) {
		
		Date from;
		Date to;
		Account account = null;
		try {
			from = new SimpleDateFormat("dd/MM/yyyy").parse(fromDate);
			to = new SimpleDateFormat("dd/MM/yyyy").parse(toDate);
			account =  viewStatementService.getStatementByDateRange(accountId,from,to);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(account != null) {
			return ResponseEntity.ok(account);
		}else {
			return new ResponseEntity<>("There is no data to display",new HttpHeaders(),HttpStatus.NO_CONTENT);
		}
       
    }
	
}
