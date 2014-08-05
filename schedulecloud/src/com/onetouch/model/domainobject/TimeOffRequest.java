package com.onetouch.model.domainobject;

import java.io.Serializable;
import java.util.Date;

import com.onetouch.view.util.DateUtil;

public class TimeOffRequest implements Serializable{

	/**
	 * 
	 */
    
	private static final long serialVersionUID = 1L;
        private Integer id;
        private Integer employeeId;
        private Integer companyId;
        private String empName;
        private Date createDate;
        private Date beginDate;
        private Date endDate;
        private String requestType;
        private int numDays;
        private String reason;
        private Event sickEvent;
        public TimeOffRequest(){}
	
        public TimeOffRequest(String requestType) {
		
        	this.requestType = requestType;
        }

		
        
	public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

	public Integer getEmployeeId() {
		return employeeId;
	}

        public void setEmployeeId(Integer id) {
		this.employeeId = id;
	}
	
	public String getEmpName() {
		return this.empName;
	}
	public void setEmpName(String name) {
		this.empName = name;
	}

        public Date getCreateDate() {
            return this.createDate;
        }

        public void setCreateDate(Date dt) {
            this.createDate = dt;
        }

        public Date getBeginDate() {
            return this.beginDate;
        }

        public void setBeginDate(Date dt) {
            this.beginDate = dt;
        }

        public void setEndDate(Date dt) {
            this.endDate = dt;
        }

        public Date getEndDate() {
            return this.endDate;
        }

        public String getRequestType() {
            return this.requestType;
        }

        public void setRequestType(String type) {
            this.requestType = type;
        }

	public Integer getCompanyId() {
		return this.companyId;
	}

        public void setCompanyId(Integer id) {
		this.companyId = id;
	}

        public int getNumDays() {
            return DateUtil.dateRange(beginDate,endDate)+1;
        }

        public void setNumDays(int num) {
            this.numDays = num;
        }

		public String getReason() {
			return reason;
		}

		public void setReason(String reason) {
			this.reason = reason;
		}

		public Event getSickEvent() {
			return sickEvent;
		}

		public void setSickEvent(Event sickEvent) {
			this.sickEvent = sickEvent;
		}

		
        
}
