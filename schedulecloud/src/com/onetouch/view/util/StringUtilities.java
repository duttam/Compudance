/*
 * StringUtilites.java
 *
 * Created on January 29, 2007, 1:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.onetouch.view.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Date;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import org.apache.commons.lang3.StringUtils;
/**
 *
 * @author liup
 */
public class StringUtilities {
    
    public static DecimalFormat priceFormat = new DecimalFormat("##,##0.00");
    public static DecimalFormat dec5Format = new DecimalFormat("##,##0.0000#");
    public static SimpleDateFormat dtFormat = new SimpleDateFormat("MM/dd/yyyy");
    /** Creates a new instance of StringUtilites */
    public StringUtilities() {
    }
    /**
     * Prepend a number of the given characters to the given String so the
     * resulting String is the given length.
     */
    public static String prependToLength(String pIn, char pChar, int pLength) {
        String localInput = (pIn==null) ? "" : pIn;
        int padLength = pLength - localInput.length();
        if (padLength < 1) {
            return localInput;
        }
        StringBuffer padBuf = new StringBuffer();
        for (int i = 0; i < padLength; i++) {
            padBuf.append(pChar);
        }
        padBuf.append(localInput);
        return padBuf.toString();
    }

    public static String nonNullString(String str) {
        return (str == null) ? "" : str.trim();
    }
    
    public static String nonNullTrim(String str) {
        return (str == null) ? str : str.trim();
    }
    
    public static String join(Collection s, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        Iterator iter = s.iterator();
        while (iter.hasNext()) {
            buffer.append(iter.next());
            if (iter.hasNext()) {
                buffer.append(delimiter);
            }
        }
        return buffer.toString();
    }

    public static String sqljoin(Collection s, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        Iterator iter = s.iterator();
        while (iter.hasNext()) {
            buffer.append("'"+iter.next()+"'");
            if (iter.hasNext()) {
                buffer.append(delimiter);
            }
        }
        return buffer.toString();
    }
    
    public static String formatDouble(double value) {
        return priceFormat.format(value);        
    }

    public static String dec5Double(double value) {
        return dec5Format.format(value);
    }

    public static String formatDate(Date dt) {
        if (dt == null) {
            return null;
        } else {
            return dtFormat.format(dt);
        }
    }
    
    public static String quoteit(String source) {
        if (source == null) {
            return null;
        }
        if (source.indexOf("'")<0)
            return source;
        else
            return StringUtils.replace(source,"'","''");
    }
    

}
