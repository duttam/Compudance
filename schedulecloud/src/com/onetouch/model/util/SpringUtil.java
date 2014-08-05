package com.onetouch.model.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;


public class SpringUtil {

	public static ShaPasswordEncoder getPasswordEncryptor() {
			
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:com/onetouch/config/applicationContextSecurity.xml");
    	ShaPasswordEncoder encoder = (ShaPasswordEncoder)applicationContext.getBean("passwordEncoder");
    	
    	return encoder;
	}
}
