package com.users.Utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class TokenHolder {
	
	
	
	public static HashMap<String, String> hmap = new HashMap<>();
	
	public void setToken(String token, String role) {
		
		hmap.put(token, role);
		
	}
	
	public String getRoleForUser(String token) {
		if(hmap.containsKey(token)) {
			return hmap.get(token);
		}
		return null;
	}
	
	
	public void updateMap(HashMap hmap) {
		this.hmap = hmap;
	}
	
public void removeToken(String token) {
	
		if(hmap.containsKey(token))
		{
			hmap.remove(token);
		}
		
		
	}
	
	@Bean 
	   public Map<String, String> myMap(){
	      java.util.Map<String, String> map = new java.util.HashMap<String, String>();
	      map.put("Hello", "world");
	      return map;      
	   }
	
	public boolean checkToken(String token) {
		if(hmap.containsKey(token)) {
			
			return true;
		}
		return false;
	}

}
