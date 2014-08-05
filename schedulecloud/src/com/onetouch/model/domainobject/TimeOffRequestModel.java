package com.onetouch.model.domainobject;
import java.io.Serializable;
import java.util.List;
import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;
public class TimeOffRequestModel extends ListDataModel<TimeOffRequest> implements SelectableDataModel<TimeOffRequest>, Serializable {  

    public TimeOffRequestModel() {
    }

    public TimeOffRequestModel(List<TimeOffRequest> data) {
        super(data);
    }
    
    @Override
    public TimeOffRequest getRowData(String rowKey) {
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data
        
        List<TimeOffRequest> requests = (List<TimeOffRequest>) getWrappedData();
        
        for(TimeOffRequest request : requests) {
            if(request.getId().intValue()== Integer.parseInt(rowKey))
                return request;
        }
        
        return null;
    }

    @Override
    public Object getRowKey(TimeOffRequest request) {
        return request.getId();
    }
}