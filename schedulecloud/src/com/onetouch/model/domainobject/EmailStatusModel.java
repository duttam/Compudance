package com.onetouch.model.domainobject;
import java.io.Serializable;
import java.util.List;
import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;
public class EmailStatusModel extends ListDataModel<EmailStatus> implements SelectableDataModel<EmailStatus>, Serializable {  

    public EmailStatusModel() {
    }

    public EmailStatusModel(List<EmailStatus> data) {
        super(data);
    }
    
    @Override
    public EmailStatus getRowData(String rowKey) {
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data
        
        List<EmailStatus> emails = (List<EmailStatus>) getWrappedData();
        
        for(EmailStatus emailStatus : emails) {
            if(emailStatus.getId().intValue()== Integer.parseInt(rowKey))
                return emailStatus;
        }
        
        return null;
    }

    @Override
    public Object getRowKey(EmailStatus request) {
        return request.getId();
    }
}