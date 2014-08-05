package com.onetouch.view.util;



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.onetouch.model.domainobject.City;
import com.onetouch.view.comparator.CityComparator;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Application-scope bean used to store static lookup information for
 * AutoComplete (selectInputText) example. Statically referenced by
 * AutoCompleteBean as the dictionary is rather large.
 *
 * @see AutoCompleteBean
 */
//@ManagedBean(name="autoCompleteDictionary",eager=true)
//@ApplicationScoped
@Component("autoCompleteDictionary")
public class AutoCompleteDictionary implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(AutoCompleteDictionary.class);
	 private @Value("${cityxml.directory.location}") String city_xmllocation;
    private List<City> dictionary;
    //private Map<String,CdanceSchool> schoolInterface = new HashMap<String, CdanceSchool>();
    public AutoCompleteDictionary() {
        try {
            log.info("initializing dictionary");
            
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Error initializtin sorting list");
            }
        }
    }
    
    
    public List<City> getDictionary() {
        return dictionary;
    }
    @PostConstruct
    private void init() {
        // Raw list of xml cities.
        List<City> cityList = null;
        //Properties locationproperties = PropertyLoader.loadProperties("location.properties");
        //String xmlLocation = locationproperties.getProperty("cityxml.directory.location")+"city.xml";
        String xmlLocation = city_xmllocation+"city.xml";
            try {
            	
                BufferedInputStream dictionaryStream = new BufferedInputStream(new FileInputStream(xmlLocation));
                XMLDecoder xDecoder = new XMLDecoder(dictionaryStream);
                // get the city list.
                cityList = (List<City>) xDecoder.readObject();
                dictionaryStream.close();
                
                xDecoder.close();
            } catch (ArrayIndexOutOfBoundsException e) {
                log.error("Error getting city list, not city objects", e);
                return;
            } catch (IOException e) {
                log.error("Error getting city list", e);
                return;
            }
        //}

        // Finally load the object from the xml file.
        if (cityList != null) {
            dictionary = new ArrayList<City>(cityList.size());
            City tmpCity;
            for (int i = 0, max = cityList.size(); i < max; i++) {
                tmpCity = (City) cityList.get(i);
                if (tmpCity != null && tmpCity.getCity() != null) {
                    dictionary.add(tmpCity);
                }
            }
            cityList.clear();
            // finally sort the list
            Collections.sort(dictionary, new CityComparator());
        }
                

    }
	
    
}