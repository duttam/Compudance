package com.onetouch.model.domainobject;
import java.io.Serializable;
import java.util.List;
import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;
public class DressCodeModel extends ListDataModel<DressCode> implements SelectableDataModel<DressCode>, Serializable {  

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DressCodeModel() {
    }

    public DressCodeModel(List<DressCode> data) {
        super(data);
    }
    
    @Override
    public DressCode getRowData(String rowKey) {
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data
        
        List<DressCode> dressCodes = (List<DressCode>) getWrappedData();
        
        for(DressCode dressCode : dressCodes) {
            if(dressCode.getId().intValue()== Integer.parseInt(rowKey))
                return dressCode;
        }
        
        return null;
    }

    @Override
    public Object getRowKey(DressCode dressCode ) {
        return dressCode.getId();
    }
}