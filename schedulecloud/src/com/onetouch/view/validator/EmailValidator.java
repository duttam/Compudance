package com.onetouch.view.validator;



import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;

import com.onetouch.view.util.ValidationMessages;
/**
 * Validator for family emailid 
 * verify whether email id is valid
 * verify if the email id already exist in the compudance.dbo.family table
 * 
 * @author duttam
 *
 */
@FacesValidator(value="emailValidator")
public class EmailValidator implements Validator {
    
	
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		String inputEmail = value.toString();

        
		org.apache.commons.validator.EmailValidator validator = org.apache.commons.validator.EmailValidator.getInstance();
		
		if(!validator.isValid(inputEmail)){
			
			FacesMessage message = ValidationMessages.getMessage(
                    ValidationMessages.MESSAGE_RESOURCES, "invalidEmail", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
			
        }
	}
	
	private int hear( BufferedReader in ) throws IOException {
	     String line = null;
	     int res = 0;

	     while ( (line = in.readLine()) != null ) {
	         String pfx = line.substring( 0, 3 );
	         try {
	            res = Integer.parseInt( pfx );
	         }
	         catch (Exception ex) {
	            res = -1;
	         }
	         if ( line.charAt( 3 ) != '-' ) break;
	     }

	     return res;
	     }

	   private void say( BufferedWriter wr, String text )
	      throws IOException {
	     wr.write( text + "\r\n" );
	     wr.flush();

	     return;
	     }
	     private static List<String> getMX( String hostName )
	         throws NamingException {
	     // Perform a DNS lookup for MX records in the domain
	     Hashtable<String, String> env = new Hashtable<String, String>();
	     env.put("java.naming.factory.initial","com.sun.jndi.dns.DnsContextFactory");
	     DirContext ictx = new InitialDirContext( env );
	     Attributes attrs = ictx.getAttributes( hostName, new String[] { "MX" });
	     Attribute attr = attrs.get( "MX" );

	     // if we don't have an MX record, try the machine itself
	     if (( attr == null ) || ( attr.size() == 0 )) {
	       attrs = ictx.getAttributes( hostName, new String[] { "A" });
	       attr = attrs.get( "A" );
	       if( attr == null )
	            throw new NamingException( "No match for name '" + hostName + "'" );
	     }
	         // Huzzah! we have machines to try. Return them as an array list
	     // NOTE: We SHOULD take the preference into account to be absolutely
	     //   correct. This is left as an exercise for anyone who cares.
	     List<String> res = new ArrayList<String>();
	     NamingEnumeration en = attr.getAll();

	     while ( en.hasMore() ) {
	        String mailhost;
	        String x = (String) en.next();
	        String f[] = x.split( " " );
	        //  THE fix *************
	        if (f.length == 1)
	            mailhost = f[0];
	        else if ( f[1].endsWith( "." ) )
	            mailhost = f[1].substring( 0, (f[1].length() - 1));
	        else
	            mailhost = f[1];
	        //  THE fix *************            
	        res.add( mailhost );
	     }
	     return res;
	     }

	   public boolean isAddressValid( String address ) {
	     // Find the separator for the domain name
	     int pos = address.indexOf( '@' );

	     // If the address does not contain an '@', it's not valid
	     if ( pos == -1 ) return false;

	     // Isolate the domain/machine name and get a list of mail exchangers
	     String domain = address.substring( ++pos );
	     List<String> mxList = null;
	     try {
	        mxList = getMX( domain );
	     }
	     catch (NamingException ex) {
	        return false;
	     }

	     // Just because we can send mail to the domain, doesn't mean that the
	     // address is valid, but if we can't, it's a sure sign that it isn't
	     if ( mxList.size() == 0 ) return false;

	     // Now, do the SMTP validation, try each mail exchanger until we get
	     // a positive acceptance. It *MAY* be possible for one MX to allow
	     // a message [store and forwarder for example] and another [like
	     // the actual mail server] to reject it. This is why we REALLY ought
	     // to take the preference into account.
	     for ( int mx = 0 ; mx <mxList.size() ; mx++ ) {
	         boolean valid = false;
	         try {
	             int res;
	             //
	             Socket skt = new Socket( (String) mxList.get( mx ), 25 );
	             BufferedReader rdr = new BufferedReader
	                ( new InputStreamReader( skt.getInputStream() ) );
	             BufferedWriter wtr = new BufferedWriter
	                ( new OutputStreamWriter( skt.getOutputStream() ) );

	             res = hear( rdr );
	             if ( res != 220 ) throw new Exception( "Invalid header" );
	             say( wtr, "EHLO rgagnon.com" );

	             res = hear( rdr );
	             if ( res != 250 ) throw new Exception( "Not ESMTP" );

	             // validate the sender address              
	             say( wtr, "MAIL FROM: <tim@orbaker.com>" );
	             res = hear( rdr );
	             if ( res != 250 ) throw new Exception( "Sender rejected" );

	             say( wtr, "RCPT TO: <" + address + ">" );
	             res = hear( rdr );

	             // be polite
	             say( wtr, "RSET" ); hear( rdr );
	             say( wtr, "QUIT" ); hear( rdr );
	             if ( res != 250 )
	                throw new Exception( "Address is not valid!" );

	             valid = true;
	             rdr.close();
	             wtr.close();
	             skt.close();
	         }
	         catch (Exception ex) {
	           // Do nothing but try next host
	           //ex.printStackTrace();
	         }
	         finally {
	           if ( valid ) return true;
	         }
	     }
	     return false;
	     }
}
