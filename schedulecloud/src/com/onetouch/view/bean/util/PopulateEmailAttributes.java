package com.onetouch.view.bean.util;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.util.FacesUtils;

public class PopulateEmailAttributes {
	
	public static Map<Object, Object> populateEmailAttributes(String websiteUrl, Tenant tenant, Event event, EventPosition eventPosition, Employee employee,Position position) {
		Map<Object, Object> attributes = new HashMap<Object, Object>();
		attributes.put("emailsubject", " Scheduled for "+tenant.getName()+", "+event.getLocation().getName()+", "+event.getName());
		//attributes.put("smtphost", tenant.getEmailServer());
		String url = websiteUrl;
		String contextRoot = FacesUtils.getServletContext().getContextPath().substring(1);
		String serverUrl = url+contextRoot;
		attributes.put("serverUrl", serverUrl);
		
		
		attributes.put("companyId",tenant.getId());
		attributes.put("employeeId",employee.getId());
		attributes.put("eventPosId",eventPosition.getId());
		
		attributes.put("firstname",employee.getFirstname());
		attributes.put("lastname", employee.getLastname());
		attributes.put("eventName", event.getName());
		attributes.put("eventNotes", event.getNotes());
		attributes.put("employeePosition",position.getName());
		attributes.put("dressCode",event.getDressCode());
		attributes.put("locName",event.getLocation().getName());
		attributes.put("locAddress1",event.getLocation().getAddress1());
		String address2 = event.getLocation().getAddress2();
		attributes.put("locAddress2",event.getLocation().getAddress2());
		attributes.put("locCity",event.getLocation().getCity());
		attributes.put("locState",event.getLocation().getState().getStateName());
		attributes.put("locZipcode",event.getLocation().getZipcode());
		SimpleDateFormat dateformat = new SimpleDateFormat("EEEEEEEEE, MMMMMMMM d, yyyy");
		SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm a");
		attributes.put("eventDate",dateformat.format(event.getStartDate()));
		attributes.put("shiftStartTime",timeformat.format(eventPosition.getStartTime()));
		attributes.put("shiftEndTime",timeformat.format(eventPosition.getEndTime()));
		attributes.put("positionNotes",eventPosition.getNotes());
		return attributes;
	}
	
	public static Map<Object, Object> populateUpdatedEmailAttributes(String websiteUrl, Tenant tenant, Event event, EventPosition eventPosition, Employee employee,Position position) {
		Map<Object, Object> attributes = new HashMap<Object, Object>();
		attributes.put("emailsubject", " Change for "+tenant.getName()+", "+event.getLocation().getName()+", "+event.getName());
		//attributes.put("smtphost", tenant.getEmailServer());
		String url = websiteUrl;
		String contextRoot = FacesUtils.getServletContext().getContextPath().substring(1);
		String serverUrl = url+contextRoot;
		attributes.put("serverUrl", serverUrl);
		
		
		attributes.put("companyId",tenant.getId());
		attributes.put("employeeId",employee.getId());
		attributes.put("eventPosId",eventPosition.getId());
		
		attributes.put("firstname",employee.getFirstname());
		attributes.put("lastname", employee.getLastname());
		attributes.put("eventName", event.getName());
		attributes.put("eventNotes", event.getNotes());
		attributes.put("employeePosition",position.getName());
		attributes.put("dressCode",event.getDressCode());
		attributes.put("locName",event.getLocation().getName());
		attributes.put("locAddress1",event.getLocation().getAddress1());
		attributes.put("locAddress2",event.getLocation().getAddress2());
		attributes.put("locCity",event.getLocation().getCity());
		attributes.put("locState",event.getLocation().getState().getStateName());
		attributes.put("locZipcode",event.getLocation().getZipcode());
		SimpleDateFormat dateformat = new SimpleDateFormat("EEEEEEEEE, MMMMMMMM d, yyyy");
		SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm a");
		attributes.put("eventDate",dateformat.format(event.getStartDate()));
		//attributes.put("shiftStartTime",timeformat.format(eventPosition.getStartTime()));
		//attributes.put("shiftEndTime",timeformat.format(eventPosition.getEndTime()));
		attributes.put("shiftStartTime","<span style=\"color:red\">"+timeformat.format(eventPosition.getStartTime())+"</span>");
		attributes.put("shiftEndTime","<span style=\"color:red\">"+timeformat.format(eventPosition.getEndTime())+"</span>");
		attributes.put("positionNotes",eventPosition.getNotes());
		return attributes;
	}
	
	public static Map<Object, Object> populateUpdatedLocationEmailAttributes(String websiteUrl, Tenant tenant, Event event, EventPosition eventPosition, Employee employee,Position position) {
		Map<Object, Object> attributes = new HashMap<Object, Object>();
		attributes.put("emailsubject", " Change for "+tenant.getName()+", "+event.getLocation().getName()+", "+event.getName());
		//attributes.put("smtphost", tenant.getEmailServer());
		String url = websiteUrl;
		String contextRoot = FacesUtils.getServletContext().getContextPath().substring(1);
		String serverUrl = url+contextRoot;
		attributes.put("serverUrl", serverUrl);
		
		
		attributes.put("companyId",tenant.getId());
		attributes.put("employeeId",employee.getId());
		attributes.put("eventPosId",eventPosition.getId());
		
		attributes.put("firstname",employee.getFirstname());
		attributes.put("lastname", employee.getLastname());
		attributes.put("eventName", event.getName());
		attributes.put("eventNote", event.getNotes());
		attributes.put("employeePosition",position.getName());
		attributes.put("dressCode","<span style=\"color:red\">"+event.getDressCode()+"</span>");
		attributes.put("locName","<span style=\"color:red\">"+event.getLocation().getName());
		attributes.put("locAddress1",event.getLocation().getAddress1());
		attributes.put("locAddress2",event.getLocation().getAddress2());
		attributes.put("locCity",event.getLocation().getCity());
		attributes.put("locState",event.getLocation().getState().getStateName());
		attributes.put("locZipcode",event.getLocation().getZipcode()+"</span>");
		SimpleDateFormat dateformat = new SimpleDateFormat("EEEEEEEEE, MMMMMMMM d, yyyy");
		SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm a");
		attributes.put("eventDate",dateformat.format(event.getStartDate()));
		attributes.put("shiftStartTime",timeformat.format(eventPosition.getStartTime()));
		attributes.put("shiftEndTime",timeformat.format(eventPosition.getEndTime()));
		//attributes.put("shiftStartTime","<span style=\"color:red\">"+timeformat.format(eventPosition.getStartTime())+"</span>");
		//attributes.put("shiftEndTime","<span style=\"color:red\">"+timeformat.format(eventPosition.getEndTime())+"</span>");
		
		return attributes;
	}

	public static Map<Object, Object> populateOfferEmailAttributes(
			String websiteUrl, Tenant tenant, Event event,
			EventPosition eventPosition, Employee employee,
			Position position) {
		Map<Object, Object> attributes = new HashMap<Object, Object>();
		attributes.put("emailsubject", " Urgent Staff Request from "+tenant.getName());
		attributes.put("priority", "HIGH");
		attributes.put("offerNotes", eventPosition.getOfferNotes());
		String url = websiteUrl;
		String contextRoot = FacesUtils.getServletContext().getContextPath().substring(1);
		String serverUrl = url+contextRoot;
		attributes.put("serverUrl", serverUrl);
		
		
		attributes.put("companyId",tenant.getId());
		attributes.put("employeeId",employee.getId());
		attributes.put("eventPosId",eventPosition.getId());
		
		attributes.put("firstname",employee.getFirstname());
		attributes.put("lastname", employee.getLastname());
		attributes.put("eventName", event.getName());
		attributes.put("eventNote", event.getNotes());
		attributes.put("employeePosition",position.getName());
		attributes.put("dressCode",event.getDressCode());
		attributes.put("locName",event.getLocation().getName());
		attributes.put("locAddress1",event.getLocation().getAddress1());
		String address2 = event.getLocation().getAddress2();
		attributes.put("locAddress2",event.getLocation().getAddress2());
		attributes.put("locCity",event.getLocation().getCity());
		attributes.put("locState",event.getLocation().getState().getStateName());
		attributes.put("locZipcode",event.getLocation().getZipcode());
		SimpleDateFormat dateformat = new SimpleDateFormat("EEEEEEEEE, MMMMMMMM d, yyyy");
		SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm a");
		attributes.put("eventDate",dateformat.format(event.getStartDate()));
		attributes.put("shiftStartTime",timeformat.format(eventPosition.getStartTime()));
		attributes.put("shiftEndTime",timeformat.format(eventPosition.getEndTime()));
		attributes.put("positionNotes",eventPosition.getNotes());
		return attributes;
	}

	public static Map<Object, Object> populateMoveEmailAttributes(
			String websiteUrl, Tenant tenant, Event transferEvent,
			Event rosterEvent, EventPosition transferEventPosition,
			EventPosition rosterEventPosition, Employee transferEmployee,
			Position position) {
		Map<Object, Object> attributes = new HashMap<Object, Object>();
		SimpleDateFormat dateformat = new SimpleDateFormat("EEEEEEEEE, MMMMMMMM d, yyyy");
		SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm a");
		attributes.put("emailsubject", " Schedule changed ");
		
		attributes.put("oldEventName", rosterEvent.getName());
		
		attributes.put("oldEmployeePosition",rosterEventPosition.getPosition().getName());
		attributes.put("oldEventDate",dateformat.format(rosterEvent.getStartDate()));
		attributes.put("oldShiftStartTime",timeformat.format(rosterEventPosition.getStartTime()));
		attributes.put("oldSiftEndTime",timeformat.format(rosterEventPosition.getEndTime()));
		
		
		
		String url = websiteUrl;
		String contextRoot = FacesUtils.getServletContext().getContextPath().substring(1);
		String serverUrl = url+contextRoot;
		attributes.put("serverUrl", serverUrl);
		
		
		attributes.put("companyId",tenant.getId());
		attributes.put("employeeId",transferEmployee.getId());
		attributes.put("eventPosId",transferEventPosition.getId());
		
		attributes.put("firstname",transferEmployee.getFirstname());
		attributes.put("lastname", transferEmployee.getLastname());
		attributes.put("eventName", transferEvent.getName());
		attributes.put("eventNotes", transferEvent.getNotes());
		attributes.put("employeePosition",position.getName());
		attributes.put("dressCode",transferEvent.getDressCode());
		attributes.put("locName",transferEvent.getLocation().getName());
		attributes.put("locAddress1",transferEvent.getLocation().getAddress1());
		String address2 = transferEvent.getLocation().getAddress2();
		attributes.put("locAddress2",transferEvent.getLocation().getAddress2());
		attributes.put("locCity",transferEvent.getLocation().getCity());
		attributes.put("locState",transferEvent.getLocation().getState().getStateName());
		attributes.put("locZipcode",transferEvent.getLocation().getZipcode());
		
		attributes.put("eventDate",dateformat.format(transferEvent.getStartDate()));
		attributes.put("shiftStartTime",timeformat.format(transferEventPosition.getStartTime()));
		attributes.put("shiftEndTime",timeformat.format(transferEventPosition.getEndTime()));
		attributes.put("positionNotes",transferEventPosition.getNotes());
		return attributes;
	}
}
