package com.onetouch.view.bean.admin.schedule;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.ValidatorException;
import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;

import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.expression.impl.PreviousExpressionResolver;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.util.Constants;

import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTable;
import com.lowagie.text.pdf.PdfWriter;

import com.onetouch.model.domainobject.Availability;
import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.EmployeeRate;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.FormatType;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Schedule;

import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.admin.event.EventPositionPDFExporter;
import com.onetouch.view.bean.admin.event.EventSupport;
import com.onetouch.view.bean.admin.location.RegionBean;
import com.onetouch.view.bean.admin.report.EventPositionToExcel;

import com.onetouch.view.bean.util.DateBean;
import com.onetouch.view.bean.util.PopulateEmailAttributes;
import com.onetouch.view.bean.util.SupportBean;
import com.onetouch.view.comparator.EmployeeNameComparator;
import com.onetouch.view.context.TenantContextUtil;
import com.onetouch.view.util.DateUtil;
import com.onetouch.view.util.EmployeeUtil;
import com.onetouch.view.util.FacesUtils;
import com.onetouch.view.util.PageXofY;

@ManagedBean(name="scheduleEmployeeSort")
@ViewScoped
public class ScheduleEmployeeSort extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<String> sortOptions; 
	private String selectedSortOption;

	@ManagedProperty(value="#{scheduleEventPosition}")
	private ScheduleEventPosition scheduleEventPosition;
	
	@PostConstruct
	public void init(){
		
		List<Employee> sortEmployees = scheduleEventPosition.getAllEmployees();
		if(sortEmployees!=null){
			if(tenantContext.getTenant().isSortByRankAndHiredate()){
				sortEmployees = EmployeeUtil.sortEmployeeByDefaultOrder(sortEmployees);
				selectedSortOption = "default";
			}
			else{
				sortEmployees = EmployeeUtil.sortEmployeeByName(sortEmployees);
				selectedSortOption = "name";
			}
			
			scheduleEventPosition.setAllEmployees(sortEmployees);
		}
	}

	public void sortEmployees(){
		List<Employee> sortEmployees = scheduleEventPosition.getAllEmployees();
		if(selectedSortOption.equalsIgnoreCase("default"))
			sortEmployees = EmployeeUtil.sortEmployeeByDefaultOrder(sortEmployees);
		else if(selectedSortOption.equalsIgnoreCase("rating"))
			sortEmployees = EmployeeUtil.sortEmployeeByRating(sortEmployees);
		else if(selectedSortOption.equalsIgnoreCase("hiredate"))
			sortEmployees = EmployeeUtil.sortEmployeeByHiredate(sortEmployees);
		else if(selectedSortOption.equalsIgnoreCase("name"))
			sortEmployees = EmployeeUtil.sortEmployeeByName(sortEmployees);
		else
			sortEmployees = EmployeeUtil.sortEmployeeByDefaultOrder(sortEmployees);
		
		scheduleEventPosition.setAllEmployees(sortEmployees);
	}
	public List<Employee> sortEmployee(List<Employee> sortEmployees) {
		if(selectedSortOption.equalsIgnoreCase("default"))
			return EmployeeUtil.sortEmployeeByDefaultOrder(sortEmployees);
		else if(selectedSortOption.equalsIgnoreCase("rating"))
			return EmployeeUtil.sortEmployeeByRating(sortEmployees);
		else if(selectedSortOption.equalsIgnoreCase("hiredate"))
			return EmployeeUtil.sortEmployeeByHiredate(sortEmployees);
		else if(selectedSortOption.equalsIgnoreCase("name"))
			return EmployeeUtil.sortEmployeeByName(sortEmployees);
		else
			return EmployeeUtil.sortEmployeeByDefaultOrder(sortEmployees);
	}
	
	public void changeSortOptions(ValueChangeEvent event) {
	    System.out.print(event.getOldValue());
	    System.out.print(event.getNewValue());
	}
	public List<String> getSortOptions() {
		return sortOptions;
	}

	public void setSortOptions(List<String> sortOptions) {
		this.sortOptions = sortOptions;
	}

	public String getSelectedSortOption() {
		return selectedSortOption;
	}

	public void setSelectedSortOption(String selectedSortOption) {
		this.selectedSortOption = selectedSortOption;
	}

	public ScheduleEventPosition getScheduleEventPosition() {
		return scheduleEventPosition;
	}

	public void setScheduleEventPosition(ScheduleEventPosition scheduleEventPosition) {
		this.scheduleEventPosition = scheduleEventPosition;
	}

	
	
	
}
