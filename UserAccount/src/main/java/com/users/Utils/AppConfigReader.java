package com.users.Utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "custom") 
public class AppConfigReader {
	
	private long sessionTime;

	public long getSessionTime() {
		return sessionTime;
	}

	public void setSessionTime(long sessionTime) {
		this.sessionTime = sessionTime;
	}

	
	
	
	

}
