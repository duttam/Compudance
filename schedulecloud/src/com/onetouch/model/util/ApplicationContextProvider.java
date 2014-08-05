package com.onetouch.model.util;



import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;

public class ApplicationContextProvider extends ApplicationObjectSupport{

	private static ApplicationContext ctx;
	@Override
	public void initApplicationContext(ApplicationContext context){
		ctx = context;
	}
	
	public static ApplicationContext getSpringApplicationContext() { 
       return ctx; 
    }
	public static Object getSpringManagedBean(String beanName){
		return ctx.getBean(beanName);
	}
}
