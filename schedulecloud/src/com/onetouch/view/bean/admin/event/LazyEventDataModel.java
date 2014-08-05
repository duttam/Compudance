package com.onetouch.view.bean.admin.event;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.service.event.IEventService;
 

public class LazyEventDataModel extends LazyDataModel<Event> implements Serializable{
     
    private List<Event> datasource;
     
    public LazyEventDataModel(List<Event> datasource) {
        this.datasource = datasource;
    }
     
    private IEventService eventService;
    
    public LazyEventDataModel(IEventService eventService) {
        this.eventService = eventService;
    }
 
    @Override
    public List<Event> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,String> filters) {
       // datasource = eventService.findWithNamedQuery(Event.ALL, first, first + pageSize);
       // setRowCount(eventService.countTotalRecord(Event.TOTAL));
        return datasource;
    }
 
    
}