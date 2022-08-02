package com.users.Service;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.users.Utils.AppConfigReader;
import com.users.dao.UserRepository;
import com.users.model.User;

@Service
public class UserService {
	
	@Autowired
	UserRepository userRepo;
	

	@Autowired
	private EntityManager em;
	
	@Autowired
    private AppConfigReader appConfigReader;
	
	public long getSessionExpiry() {
		return appConfigReader.getSessionTime()*1000;
			
	}

	public User findUser(String username) {
		Optional<User> o = userRepo.findById(username);
		
		if(o.isPresent())
			return o.get();
		
		return null;
	}

	public void updateUser(User user) {
		
		userRepo.save(user);
		
	}

	public User findUserByToken(String token) {
		//User user = userRepo.findByToken(token);
		
		User user = null;
		System.out.println(token);

		Query q = (Query) em.createNativeQuery("SELECT * FROM USER u where u.token  = :token", User.class);
		q.setParameter("token", token);
		//em.setProperty("token", "54a7b190-17be-45b5-9746-08f586eeb89d");
		user = (User) q.getSingleResult();
		
		
		
		
		return user;
	}
	

}
