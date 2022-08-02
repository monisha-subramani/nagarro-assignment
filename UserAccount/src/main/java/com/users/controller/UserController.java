package com.users.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.client.RestTemplate;

import com.users.Service.UserService;
import com.users.Utils.Roles;
import com.users.Utils.SchedulerTask;
import com.users.Utils.TokenHolder;
import com.users.dao.UserRepository;
import com.users.model.Account;
import com.users.model.User;

@RestController
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	TokenHolder tokenHolder;
	
	@Autowired
	private RestTemplate restTemplate;
	
	
	Logger logger = LoggerFactory.getLogger(UserController.class);

	
	@RequestMapping("/welcome")
	public String index(@RequestHeader("Authorization") String sessionId) {
		
		
		System.out.println("token  "+sessionId);
		System.out.println(tokenHolder.checkToken(sessionId));
		
		
		//User user = userService.findUserByToken(token);
		
		//System.out.println("User Sucees:: "+user.getRole());
		
		return "Greetings from Spring Boot!";
	}
	
	@RequestMapping("/logout")
	public ResponseEntity logout(@RequestHeader("Authorization") String sessionId) {
		
		logger.trace("Logout requested for user");
		
		if(tokenHolder.checkToken(sessionId)) {
			tokenHolder.removeToken(sessionId);
			logger.info("User Logout: Session invalidated");
		}else {
			logger.info("User already logged out or invalid session.");
			return new ResponseEntity<>("User already logged out or invalid session.",new HttpHeaders(),HttpStatus.ALREADY_REPORTED);
			
		}
		
		User user = userService.findUserByToken(sessionId);
		
		if(user != null) {
			user.setToken("");
			userService.updateUser(user);
			logger.info("User Logout: Session removed from user details in DB");
		}
		
		logger.trace("User logged out successfuly");
		return new ResponseEntity<>("User Logged out successfully.",new HttpHeaders(),HttpStatus.OK);
		//return "User Logged out successfully.";
	}
	
	

	@PostMapping("/login")
    public ResponseEntity generateToken(@RequestHeader("Authorization") String loginCreds) {
		
		logger.trace("User Login: User Login requested");
		User user = null;
        System.out.println(loginCreds);
        String[] authParts = loginCreds.split("\\s+");
        
        if(authParts.length != 2) {
        	logger.info("User Login: Invalid Authorization details");
        	return new ResponseEntity<>("Invalid Authorization details",new HttpHeaders(),HttpStatus.BAD_REQUEST);
        }
        
        String decodedAuth = "";
        String authInfo = authParts[1];
        byte[] bytes = null;
        bytes = Base64.getDecoder().decode(authInfo);
       
        decodedAuth = new String(bytes);
        
        String[] decodedCreds = decodedAuth.split(":"); 
        
        if(decodedCreds.length != 2) {
        	logger.info("User Login: Invalid input credentials. Either Username or password is missing");
        	return new ResponseEntity<>("Invalid input credentials",new HttpHeaders(),HttpStatus.BAD_REQUEST);
        }
        
        String username = decodedCreds[0];
        logger.trace("User Login: Username is "+username);
        
        String enteredPassword = decodedCreds[1];
        username=username.toLowerCase();
        
        user = userService.findUser(username);
       
        if(user != null) {
        	logger.trace("User Login: User exists");
        	if(!user.getToken().isEmpty()) {
        		logger.info("User Login: User already logged in");
        		return new ResponseEntity<>("The user is already logged in",new HttpHeaders(),HttpStatus.ALREADY_REPORTED);
        	}
        	
        	if(username.equals(user.getUser()) && enteredPassword.equals(user.getPassword())){
        		logger.trace("User Login: Creating the session for the user");
                String token = UUID.randomUUID().toString();
                logger.trace("User Login: Session token created"+token);
                
                user.setToken(token);
                userService.updateUser(user);
           
                
                tokenHolder.setToken(token, user.getRole());
                
                logger.trace("User Login: User deatils updated with session token in DB");
                
                
               sessionTask(token, tokenHolder, userService);
                
                return ResponseEntity.ok(token);
            }else
            {
            	logger.info("User Login: Username or password is Wrong");
                return new ResponseEntity<>("Username or password is Wrong",new HttpHeaders(),HttpStatus.UNAUTHORIZED);
            }
        	
        }else {
        	logger.info("User Login: User does not exist");
        	return new ResponseEntity<>("User does not exist",new HttpHeaders(),HttpStatus.UNAUTHORIZED);
        }
    }
	
	@GetMapping("/viewStatements")
    public ResponseEntity getTransactionList(@RequestHeader("Authorization") String sessionId, @RequestParam("id") int accountId, @RequestParam(name="fromDate",required=false) String fromDate, 
    		@RequestParam(name="toDate",required=false) String toDate, @RequestParam(name="fromAmt",required=false) String frmAmt, @RequestParam(name="toAmt", required=false) String toAmt) {

		logger.trace("View Statement: requested");
		Account account = null;
		String role = tokenHolder.getRoleForUser(sessionId);
		logger.trace("View Statement: User Role is fetched - "+role);
		if(role == null) {
			logger.info("View Statement: The user is not logged in");
			return new ResponseEntity<>("The user is not logged in",new HttpHeaders(),HttpStatus.FORBIDDEN);
		}
		
		boolean statementByDate = fromDate != null && toDate != null;
		boolean statementByAmount = frmAmt != null && toAmt != null;
		
		if(role.equals(Roles.ADMIN.toString())) {
			if(!statementByAmount && fromDate != null && toDate == null) {
				logger.info("View Statement: To Date field is missing");
				return new ResponseEntity<>("To Date field is missing",new HttpHeaders(),HttpStatus.NOT_ACCEPTABLE);
			}else if(!statementByAmount && fromDate == null && toDate != null){
				logger.info("View Statement: From Date field is missing");
				return new ResponseEntity<>("From Date field is missing",new HttpHeaders(),HttpStatus.NOT_ACCEPTABLE);
			}
			
			if(!statementByDate && frmAmt != null && toAmt == null) {
				logger.info("View Statement: To Amount field is missing");
				return new ResponseEntity<>("To Amount field is missing",new HttpHeaders(),HttpStatus.NOT_ACCEPTABLE);
			}else if(!statementByDate && frmAmt == null && toAmt != null){
				logger.info("View Statement: From Amount field is missing");
				return new ResponseEntity<>("From Amount field is missing",new HttpHeaders(),HttpStatus.NOT_ACCEPTABLE);
			}
			
		}
		if(role.equals(Roles.USER.toString())) {
			if(fromDate != null || toDate != null || frmAmt != null || toAmt != null) {
				logger.info("View Statement: The user is not authorized for range search");
				return new ResponseEntity<>("The user is not authorized for range search",new HttpHeaders(),HttpStatus.UNAUTHORIZED);
			}
		}
		
		
		
		if(statementByDate && statementByAmount) {
			logger.info("View Statement: Invalid request parameters - Both Date range and Amount range provided. ");
			return new ResponseEntity<>("Please search by either Date or Amount",new HttpHeaders(),HttpStatus.NOT_ACCEPTABLE);
		}
		
		
		Date startDate;
		Date endDate;
		
		if(statementByDate) {
			logger.trace("View Statement: Search for statements by Date Range");
			try {
				
				startDate = new SimpleDateFormat("dd/MM/yyyy").parse(fromDate);
				endDate = new SimpleDateFormat("dd/MM/yyyy").parse(toDate);
				
				
				
			} catch (ParseException e) {
				logger.error("View Statement: Date values are invalid and unparsable", e);
				
				return new ResponseEntity<>("Invalid Date Fromat. Please input date in dd/MM/yyyy format",new HttpHeaders(),HttpStatus.BAD_REQUEST);
				
			}
			
			if(startDate.compareTo(endDate) > 0) {
				logger.info("View Statement:  Invalid Date range - From Date is ahead of To Date");
				return new ResponseEntity<>("Invalid Date range",new HttpHeaders(),HttpStatus.BAD_REQUEST);
				
			}
			logger.info("View Statement:  Requesting DB server to get the statements between date range");
			account =  restTemplate.getForObject("http://localhost:8080//statements/statementsByDate?id="+accountId+"&from="+fromDate+"&to="+toDate,Account.class);
		//	return ResponseEntity.ok(account);
		}else if(statementByAmount) {
			logger.trace("View Statement: Search for statements by Amount Range");
			
			double startAmt = Double.parseDouble(frmAmt);
			double endAmt = Double.parseDouble(toAmt);
			
			try {
				startAmt = Double.parseDouble(frmAmt);
				endAmt = Double.parseDouble(toAmt);
			}catch (NumberFormatException e) {
				logger.error("View Statement: Amount values are invalid and unparsable", e);
				return new ResponseEntity<>("Invalid Amount range input",new HttpHeaders(),HttpStatus.BAD_REQUEST);
				
			}
			
			if(startAmt > endAmt) {
				logger.info("View Statement:  Invalid Amount range - From Amount is greater than To Date");
				return new ResponseEntity<>("Invalid Amount range",new HttpHeaders(),HttpStatus.BAD_REQUEST);
			}
			
			logger.info("View Statement:  Requesting DB server to get the statements between Amount range");
			account =  restTemplate.getForObject("http://localhost:8080//statements/statementsByAmount?id="+accountId+"&from="+frmAmt+"&to="+toAmt,Account.class);
		//	return ResponseEntity.ok(account);
		}else {

			logger.info("View Statement: Search for statements for last 3 months");
			
			LocalDate now = LocalDate.now();
			LocalDate previous = now.minusMonths(3);
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
			
			fromDate = formatter.format(previous);
			toDate = formatter.format(now);
			
			logger.info("View Statement:  Requesting DB server to get the statements for last 3 months");
			account =  restTemplate.getForObject("http://localhost:8080//statements/statementsByDate?id="+accountId+"&from="+fromDate+"&to="+toDate,Account.class);
			
		}
       
		//account =  restTemplate.getForObject("http://localhost:8080//statements/transactionHistory/"+accountId,Account.class);
		
		
		
		if(account != null) {
			logger.trace("View Statement: Statment Data successfully returned");
			return ResponseEntity.ok(account);
		}else {
			logger.trace("View Statement: There is no data to display");
			return new ResponseEntity<>("There is no data to display",new HttpHeaders(),HttpStatus.NO_CONTENT);
		}
        //return ResponseEntity.ok(account);

    }
	
	public static void sessionTask(String token, TokenHolder tokenHolder, UserService userService) {
		Timer timer = new Timer();
		
		timer.schedule(new SchedulerTask(token, tokenHolder, userService), userService.getSessionExpiry());
	}
		
	
}


