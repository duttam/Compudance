package com.onetouch.model.domainobject;

public enum ScheduleType {
	NOTIFIED("Notify"),
	ACCEPTED("Accept"),
	REJECTED("Reject"),
	EXPIRED("Expired");
	private String label;
	private ScheduleType(String label){
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

}
