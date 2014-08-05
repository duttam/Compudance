package com.onetouch.view.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.application.FacesMessage;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.FactoryFinder;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;




public class FacesUtils extends PropertyPlaceholderConfigurer{
	static final Logger logger = Logger.getLogger("FacesUtils.class");
	private static Map<String, String> properties = new HashMap<String, String>();
	private static final String encryptKey = "4d89g13j4j91j27c582ji693";
	private static final String encryptInitVector = "compdanc";
	public static boolean isMobileRequest(){
		String renderKitId = FacesContext.getCurrentInstance().getViewRoot().getRenderKitId();
		if(renderKitId.equalsIgnoreCase("PRIMEFACES_MOBILE"))
			return true;
		else
			return false;
	}
	public static String getSessionId(){
		FacesContext fcontext = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession)fcontext.getExternalContext().getSession(false);
		return (String)session.getId();
	}
	public static ServletContext getServletContext() {
		return (ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext();
	}
		
	public static HttpServletRequest getServletRequest() {
		return (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
	}
	
	public static HttpServletResponse getServletResponse() {
		return (HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();
		//return (HttpServletResponse)FacesContext.getExternalContext().getResponse();
	}
	
	public static void redirectToPage(String toPage ){
            String contextRoot = FacesUtils.getServletContext().getContextPath();
            try {
                logger.warn( "Redirecting to page: " + toPage );                    
                FacesContext.getCurrentInstance().getExternalContext().redirect( contextRoot  + "/" +toPage + ".jsf" );
                    
            } catch ( IOException e ) {
                    logger.error( "couldn't redirect to page " + toPage );
            }
	}
	
	public static String getRequestParameter(String name) {
		return (String)FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(name);
	}
	
	public static void resetManagedBean(String beanName) {
		getValueBinding(getJsfEl(beanName)).setValue(FacesContext.getCurrentInstance(), null);
	}
	
	public static Object getManagedBean(String beanName) {
        FacesContext fc = FacesContext.getCurrentInstance();
        ELContext elc = fc.getELContext();
        ExpressionFactory ef = fc.getApplication().getExpressionFactory();
        ValueExpression ve = ef.createValueExpression(elc, getJsfEl(beanName), Object.class);
        return ve.getValue(elc);
    }
	private static Application getApplication() {
		ApplicationFactory appFactory = (ApplicationFactory)FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
		return appFactory.getApplication(); 
	}
	public static ApplicationContext getSpringApplicationContext(){
		ApplicationContext ctx = FacesContextUtils.getWebApplicationContext(FacesContext.getCurrentInstance());
		
		return ctx;
	}
	/**
	 * Add error message.
	 * 
	 * @param msg the error message
	 */
	public static void addErrorMessage(String msg) {
		addErrorMessage(null, msg);
	}
	
	/**
	 * Add error message to a sepcific client.
	 * 
	 * @param clientId the client id 
	 * @param msg the error message
	 */	
	public static void addErrorMessage(String clientId, String msg) {
		FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
	}
	public static void addInfoMessage(String msg) {
		addInfoMessage(null, msg);
	}
	
	/**
	 * Add information message to a sepcific client.
	 * 
	 * @param clientId the client id 
	 * @param msg the information message
	 */
	public static void addInfoMessage(String clientId, String msg) {
		FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg));
	}
	private static ValueBinding getValueBinding(String el) {
		return getApplication().createValueBinding(el);
	}
		
	private static Object getElValue(String el) {
		return getValueBinding(el).getValue(FacesContext.getCurrentInstance());
	}
	
	private static String getJsfEl(String value) {
		return "#{" + value + "}";
	}
	
	
	@Override
	protected void loadProperties(final Properties props) throws IOException {
		super.loadProperties(props);
		for (final Object key : props.keySet()) {
			properties.put((String) key, props.getProperty((String) key));
		}
	}

	/**
	* Return a property loaded by the place holder.
	* @param name the property name.
	* @return the property value.
	*/
	public static String getProperty(final String name) {
		return properties.get(name);
	}
	
	public static String encrypt(String bankNumber) throws Exception{
	    //----  Use specified 3DES key and IV from other source --------------
	      byte[] plaintext = bankNumber.getBytes();
	      byte[] tdesKeyData = FacesUtils.encryptKey.getBytes();

	      byte[] myIV = FacesUtils.encryptInitVector.getBytes();

	      Cipher c3des = Cipher.getInstance("DESede/CBC/PKCS5Padding");
	      SecretKeySpec    myKey = new SecretKeySpec(tdesKeyData, "DESede");
	      IvParameterSpec ivspec = new IvParameterSpec(myIV);

	      c3des.init(Cipher.ENCRYPT_MODE, myKey, ivspec);
	      byte[] cipherText = c3des.doFinal(plaintext);
	      return (new sun.misc.BASE64Encoder().encode(cipherText));
	      //return Base64Coder.encodeString(new String(cipherText));
	    }
	public static String decrypt(String encryptedNumber)throws Exception{
		byte[] encData = new sun.misc.BASE64Decoder().decodeBuffer(encryptedNumber);
    	
    	Cipher decipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
    	byte[] tdesKeyData = FacesUtils.encryptKey.getBytes();
    	SecretKeySpec myKey = new SecretKeySpec(tdesKeyData, "DESede");
    	IvParameterSpec ivspec = new IvParameterSpec(FacesUtils.encryptInitVector.getBytes());
    	decipher.init(Cipher.DECRYPT_MODE, myKey, ivspec);
    	byte[] plainText = decipher.doFinal(encData);
    	return (new String(plainText));
	}
}
