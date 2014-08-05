package com.onetouch.view.bean.util;



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.onetouch.model.domainobject.City;

import com.onetouch.view.comparator.CityComparator;
import com.onetouch.view.util.AutoCompleteDictionary;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Stores the values picked from the AutoCompleteDictionary (different scope to
 * avoid memory hole). 
 *
 * @see AutoCompleteDictionary
 */

//@Component("autoCompleteBean") 
//@Scope("session")
@ManagedBean(name="autoCompleteBean")
@SessionScoped
public class AutoCompleteBean
    implements 
        Serializable{

    private static final long serialVersionUID = 1L;

    private static Log log = LogFactory.getLog(AutoCompleteBean.class);

    private City currentCity;

    private List<String> matchesList = new ArrayList<String>();
    
    @ManagedProperty(value="#{autoCompleteDictionary}") 
    private AutoCompleteDictionary autoCompleteDictionary ;
    
    
    public List<String> getList() {
        return matchesList;
    }
    
    public List<String> complete( String searchWord ) {

        int maxMatches = 10;//((InputText) event.getComponent()).getRows();
        List<String> matchList = new ArrayList<String>(maxMatches);
        
        try {

            int insert = Collections.binarySearch( autoCompleteDictionary.getDictionary(), new City(searchWord),new CityComparator() );

            // less then zero if we have a partial match
            if (insert < 0) {
                insert = Math.abs(insert) - 1;
            }

            for (int i = 0; i < maxMatches; i++) {
                // quit the match list creation if the index is larger then
                // max entries in the dictionary if we have added maxMatches.
                if ((insert + i) >= autoCompleteDictionary.getDictionary().size() ||
                    i >= maxMatches) {
                    break;
                }
                matchList.add(autoCompleteDictionary.getDictionary().get(insert + i).getCity());
            }
        } catch (Throwable e) {
        	e.printStackTrace();
            //log.error("Erorr finding autocomplete matches", e);
        }
        // assign new matchList
        if ( this.matchesList != null ) {
            this.matchesList.clear();
            this.matchesList = null;
        }
        
        this.matchesList = matchList;
        
        return matchList;
    }

    public City getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(City currentCity) {
        this.currentCity = currentCity;
    }

    public List<String> getMatchesList() {
        return matchesList;
    }

    public void setMatchesList(List<String> matchesList) {
        this.matchesList = matchesList;
    }

    public AutoCompleteDictionary getAutoCompleteDictionary() {
        return autoCompleteDictionary;
    }

    public void setAutoCompleteDictionary(
                    AutoCompleteDictionary autoCompleteDictionary) {
        this.autoCompleteDictionary = autoCompleteDictionary;
    }

    public void handleSelect(SelectEvent event) {
        /*if(currentCity!=null){
                currentCity.setState("");
                currentCity.setZip("");
        }*/
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Selected:" + event.getObject().toString(), null);

        FacesContext.getCurrentInstance().addMessage(null, message);
    }
}