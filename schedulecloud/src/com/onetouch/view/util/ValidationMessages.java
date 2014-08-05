package com.onetouch.view.util;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

	/**
	 * Code required for custom validators. See Core JavaServer Faces page 231
	 */
	public class ValidationMessages {
	    public static final String MESSAGE_RESOURCES =
	            "com.onetouch.resource.messages";
	    public static final String ERROR_MESSAGE_RESOURCES =
            "com.onetouch.resource.errormessages";
	    /**
	     * Method to get a generic FacesMessage based on the various parameters
	     *
	     * @param bundleName to use
	     * @param resourceId related to the message
	     * @param params     list
	     * @return constructed messages
	     */
	    public static FacesMessage getMessage(String bundleName, String resourceId,
	                                          Object[] params) {
	        FacesContext context = FacesContext.getCurrentInstance();
	        Application app = context.getApplication();
	        String appBundle = app.getMessageBundle();
	        Locale locale = ValidationMessages.getLocale(context);
	        ClassLoader loader = ValidationMessages.getClassLoader();
	        String summary = ValidationMessages.getString(appBundle, bundleName,
	                                                      resourceId, locale,
	                                                      loader, params);

	        if (summary == null) {
	            summary = "???" + resourceId + "???";
	        }

	        String detail = ValidationMessages.getString(appBundle, bundleName,
	                                                     resourceId, locale,
	                                                     loader, params);

	        return new FacesMessage(summary, detail);
	    }

	    /**
	     * Method to get a String after passing it through a bundle
	     *
	     * @param bundle     to use
	     * @param resourceId related to the string
	     * @param params     list
	     * @return result of bundle pass
	     */
	    public static String getString(String bundle, String resourceId,
	                                   Object[] params) {
	        FacesContext context = FacesContext.getCurrentInstance();
	        Application app = context.getApplication();
	        String appBundle = app.getMessageBundle();
	        Locale locale = ValidationMessages.getLocale(context);
	        ClassLoader loader = ValidationMessages.getClassLoader();

	        return ValidationMessages.getString(appBundle, bundle, resourceId,
	                                            locale, loader, params);
	    }

	    /**
	     * Method to get a String after passing it through a series of bundles and
	     * locales
	     *
	     * @param bundle1    name
	     * @param bundle2    name
	     * @param resourceId related to the string
	     * @param locale     package to use
	     * @param loader     as needed
	     * @param params     list
	     * @return resulting string
	     */
	    public static String getString(String bundle1, String bundle2,
	                                   String resourceId, Locale locale,
	                                   ClassLoader loader, Object[] params) {
	        String resource = null;
	        ResourceBundle bundle;

	        if (bundle1 != null) {
	            bundle = ResourceBundle.getBundle(bundle1, locale, loader);
	            if (bundle != null) {
	                try {
	                    resource = bundle.getString(resourceId);
	                }catch (MissingResourceException missingError) {
	                    missingError.printStackTrace();
	                }
	            }
	        }

	        if (resource == null) {
	            bundle = ResourceBundle.getBundle(bundle2, locale, loader);
	            if (bundle != null) {
	                try {
	                    resource = bundle.getString(resourceId);
	                }catch (MissingResourceException missingError) {
	                    missingError.printStackTrace();
	                }
	            }
	        }

	        if (resource == null) {
	            return null;
	        }

	        if (params == null) {
	            return resource;
	        }

	        MessageFormat formatter = new MessageFormat(resource, locale);

	        return formatter.format(params);
	    }

	    /**
	     * Method to get a locale package from the passed faces context
	     *
	     * @param context to use
	     * @return retrieved locale
	     */
	    public static Locale getLocale(FacesContext context) {
	        Locale locale = null;
	        UIViewRoot viewRoot = context.getViewRoot();

	        if (viewRoot != null) {
	            locale = viewRoot.getLocale();
	        }

	        if (locale == null) {
	            locale = Locale.getDefault();
	        }

	        return locale;
	    }

	    /**
	     * Method to get a class loader (either from this thread or the default
	     * system loader)
	     *
	     * @return resulting class loader
	     */
	    public static ClassLoader getClassLoader() {
	        ClassLoader loader = Thread.currentThread().getContextClassLoader();

	        if (loader == null) {
	            loader = ClassLoader.getSystemClassLoader();
	        }

	        return loader;
	    }
}


