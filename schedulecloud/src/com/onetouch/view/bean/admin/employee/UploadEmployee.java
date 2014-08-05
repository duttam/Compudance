package com.onetouch.view.bean.admin.employee;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.ServletContext;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.Hours;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.EmployeeRate;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.State;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.domainobject.TenantDocument;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.admin.location.RegionBean;
import com.onetouch.view.bean.util.DateBean;
import com.onetouch.view.util.DateUtil;
import com.onetouch.view.util.EmployeeUtil;
import com.onetouch.view.util.FacesUtils;
import com.onetouch.view.util.ValidationMessages;

@ManagedBean(name="uploadEmployee") 
@ViewScoped
public class UploadEmployee extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    
      
    
    
    @ManagedProperty(value="#{employeeSupport}")
    private EmployeeSupport employeeSupport;
    
    private Tenant tenant;
    
    public UploadEmployee(){}
	
	private UserDetails user;
	//private Employee uploadedEmployee;
	private UploadedFile uploadEmployeeDetailDocument;
	private List<Employee> uploadEmployeeList;
	private List<Employee> employeeList;
	private Map<String,Position> allActivePositions;
	private Set<String> uploadedPositions;
	private List<Employee> filteredEmployeeList;
	@ManagedProperty(value="#{regionBean}")
	private RegionBean regionBean;

    
	@PostConstruct
	public void initEmployeController(){
		UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		if(authenticationToken.isAuthenticated())
		user = (CustomUser)authenticationToken.getPrincipal();
		
		tenant = tenantContext.getTenant();// get company detail from security context
		uploadEmployeeList = new ArrayList<Employee>();
		employeeList = getEmployeeService().getAllUploadedEmployee(tenant,regionBean.getSelectedRegion());
		List<Position> allPositions = getPositionService().getAllPosition(tenant);
		if(allPositions!=null){
			allActivePositions = new CaseInsensitiveMap();
			for(Position position : allPositions){
				allActivePositions.put(position.getName(),position);
			}
		}
		
	}
	

	
	
	private Map<Object, Object> populateEmailAttributes(Employee employee) {
		Map<Object, Object> attributes = new HashMap<Object, Object>();
		attributes.put("emailsubject", "Schedule-Cloud INVITE for "+tenant.getName());
		attributes.put("companyName", tenantContext.getTenant().getName());
		String contextRoot = FacesUtils.getServletContext().getContextPath().substring(1);
		attributes.put("contextRoot", contextRoot);
		attributes.put("firstname",employee.getFirstname());
		attributes.put("lastname", employee.getLastname());
		attributes.put("invitecode", employee.getInvitecode());
		attributes.put("empid",employee.getId());
		attributes.put("tenantId",tenant.getId());
		return attributes;
	}

	
	
	@SuppressWarnings("deprecation")
	public void uploadEmployeeDetailFile(FileUploadEvent event){
		uploadedPositions = new HashSet<String>();
		uploadEmployeeDetailDocument = event.getFile();
        if(uploadEmployeeDetailDocument!=null && uploadEmployeeDetailDocument.getFileName()!=null){
        		String documentname = uploadEmployeeDetailDocument.getFileName();
        		String documentType = uploadEmployeeDetailDocument.getContentType();
        		byte[] documentBytes = uploadEmployeeDetailDocument.getContents();
        		InputStream inputStream;
				try {
					inputStream = uploadEmployeeDetailDocument.getInputstream();
					
					//Workbook wb = WorkbookFactory.create(inputStream);
					
					if(documentType.equalsIgnoreCase("application/vnd.ms-excel")){
					
						POIFSFileSystem fsFileSystem = new POIFSFileSystem(inputStream);
		    			/*
		    			 * Create a new instance for HSSFWorkBook Class
		    			 */
		    			HSSFWorkbook workBook = new HSSFWorkbook(fsFileSystem);
						HSSFSheet hssfSheet = workBook.getSheetAt(0);
		    			/**
		    			 * Iterate the rows and cells of the spreadsheet
		    			 * to get all the data .
		    			 */
		    			Iterator rowIterator = hssfSheet.rowIterator();
		    			int row = 0;
		    			while (rowIterator.hasNext())
		    			{
		    				
		    				if(row==0){
		    					HSSFRow hssfRow = (HSSFRow) rowIterator.next();
		    					row++;
		    					//continue;
		    				}
		    				else{
		    					Employee employee = new Employee(tenant);
		    					employee.setRegion(regionBean.getSelectedRegion());
		    					State state = new State();
		    					employee.setState(state);
		    					EmployeeRate employeeRate = new EmployeeRate();
		    					employee.setEmpRate(employeeRate);
		    					employee.setEmployeeStatus("upload");
			    				HSSFRow hssfRow = (HSSFRow) rowIterator.next();
			    				int columns = Math.max(hssfRow.getLastCellNum(), 7);
			    				for(int column = 0;column<columns;column++){
			    					HSSFCell hssfCell = hssfRow.getCell(column, HSSFRow.RETURN_BLANK_AS_NULL);
			    					   if (hssfCell == null) {
			    						   if(column==0){
					    						employee.setFirstname("");
					    					}
					    					if(column==1){
					    						
					    						employee.setLastname("");
					    					}
					    					if(column==2){
					    						
					    						employee.setAddress1("");
					    					}
					    					if(column==3){
					    						
					    						employee.setAddress2("");
					    					}
					    					if(column==4){
					    						
					    						employee.setCity("");
					    					}
					    					if(column==5){
					    						state.setStateName("");
					    					}
					    					if(column==6){
					    						
					    						state.setStateCode("");
					    					}
					    					if(column==7){
					    						
					    						employee.setZipcode("");
					    					}
					    					if(column==8){
					    						employee.setHomephone("");
					    					}
					    					if(column==9){
					    						
					    						employee.setCellphone("");
					    					}
					    					if(column==10){
					    						
					    						employee.setFax("");
					    					}
					    					if(column==11){
					    						
					    						employee.setEmail("");
					    					}
					    					if(column==12){
					    						
					    						employee.setHireDate(null);
					    					}
					    					if(column==13){
					    						employeeRate.setHourlyRate(new BigDecimal(0.0));
					    					}
					    					if(column==14){
					    						employee.setAssignedPositions(new ArrayList<Position>());
					    					}
			    					   } else {
			    						   DataFormatter dataFormatter = new DataFormatter(Locale.US);
				    						
			    						   if(column==0){
					    						String firstname = (String)this.getExcelCellValue(hssfCell);
					    						employee.setFirstname(firstname);
					    					}
					    					if(column==1){
					    						String lastname = (String)this.getExcelCellValue(hssfCell);
					    						employee.setLastname(lastname);
					    					}
					    					
					    					if(column==2){
					    						String address1 = (String)this.getExcelCellValue(hssfCell);
					    						employee.setAddress1(address1);
					    					}
					    					if(column==3){
					    						String address2 = (String)this.getExcelCellValue(hssfCell);
					    						employee.setAddress2(address2);
					    					}
					    					if(column==4){
					    						String city = (String)this.getExcelCellValue(hssfCell);
					    						employee.setCity(city);
					    					}
					    					if(column==5){
					    						String stateName = (String)this.getExcelCellValue(hssfCell);
					    						state.setStateName(stateName);
					    						
					    					}
					    					if(column==6){
					    						String stateCode = (String)this.getExcelCellValue(hssfCell);
					    						state.setStateCode(stateCode);
					    					}
					    					if(column==7){
					    						
					    						String zipcode = dataFormatter.formatCellValue(hssfCell);//this.getExcelCellValue(hssfCell);
					    						if(zipcode.length()==4)
					    							zipcode = "0"+zipcode;
					    						employee.setZipcode(zipcode);
					    					}
					    					if(column==8){
					    						String homephone = dataFormatter.formatCellValue(hssfCell);;//this.getExcelCellValue(hssfCell);
					    						employee.setHomephone(homephone);
					    					}
					    					if(column==9){
					    						String cellphone = dataFormatter.formatCellValue(hssfCell);;//this.getExcelCellValue(hssfCell);
					    						employee.setCellphone(cellphone);
					    					}
					    					
					    					if(column==10){
					    						String fax = dataFormatter.formatCellValue(hssfCell);;//this.getExcelCellValue(hssfCell);
					    						employee.setFax(fax);
					    					}
					    					if(column==11){
					    						String email = (String)this.getExcelCellValue(hssfCell);
					    						org.apache.commons.validator.EmailValidator validator = org.apache.commons.validator.EmailValidator.getInstance();
					    						boolean valid = validator.isValid(email);
					    						if(valid)
					    							employee.setEmail(email);
					    						else
					    							employee.setEmail("test@test.com");
					    					}
					    					if(column==12){
					    						if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(hssfCell))
					    						{
					    						   
	
					    						    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					    						    Date hireDate = hssfCell.getDateCellValue();
					    						    employee.setHireDate(hireDate);
					    						}
					    						/*String dob = dataFormatter.formatCellValue(hssfCell);//this.getExcelCellValue(hssfCell);
					    						
												try {
													SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy");
													Date dateOfBirth = sdf.parse(dob);
													
													employee.setDob(dateOfBirth);
												} catch (ParseException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
					    						*/
					    					}
											if(column==13){
												
												String rateString = dataFormatter.formatCellValue(hssfCell);//this.getExcelCellValue(hssfCell);
												employeeRate.setRateStartDate(new Date());
												Date endDate = EmployeeUtil.getEmployeeRateEndDate();
												employeeRate.setRateEnddate(endDate);// set the date after 100 years
												parseAndSetHourlyRate(employeeRate,rateString);
												
												
											}
											if(column==14){
												String positionString = (String)this.getExcelCellValue(hssfCell);
												employee.setUploadedPositionString(positionString);
												String[] positions = positionString.split(",");
												uploadedPositions.addAll(Arrays.asList(positions));
												
											}
			    					   }
			    				}
			    				
			    				if(state!=null){
			    					String statecode = state.getStateCode();
			    					String statename = state.getStateName();
			    					if(statecode!=null){
			    						statename = getApplicationData().getStateName(statecode);
			    						state.setStateName(statename);
			    					}else if(statename!=null){
			    						statecode = getApplicationData().getStateCode(statename);
			    						state.setStateCode(statecode);
			    					}
			    					
			    				}
			    				uploadEmployeeList.add(employee);
			    				row++;
		    				}
		    				
		    			}
					}else{
						
						XSSFWorkbook workBook = new XSSFWorkbook(inputStream);
		    			XSSFSheet xssfSheet = workBook.getSheetAt(0);
		    			/**
		    			 * Iterate the rows and cells of the spreadsheet
		    			 * to get all the data .
		    			 */
		    			Iterator rowIterator = xssfSheet.rowIterator();
		    			int row = 0;
		    			while (rowIterator.hasNext())
		    			{
		    				
		    				if(row==0){
		    					XSSFRow xssfRow = (XSSFRow) rowIterator.next();
		    					row++;
		    					//continue;
		    				}
		    				else{
		    					Employee employee = new Employee(tenant);
		    					employee.setRegion(regionBean.getSelectedRegion());
		    					State state = new State();
		    					employee.setState(state);
		    					EmployeeRate employeeRate = new EmployeeRate();
		    					employee.setEmpRate(employeeRate);
		    					employee.setEmployeeStatus("upload");
			    				XSSFRow xssfRow = (XSSFRow) rowIterator.next();
			    				int columns = Math.max(xssfRow.getLastCellNum(), 7);
			    				for(int column = 0;column<columns;column++){
			    					XSSFCell xssfCell = xssfRow.getCell(column, HSSFRow.RETURN_BLANK_AS_NULL);
			    					   if (xssfCell == null) {
			    						   if(column==0){
					    						employee.setFirstname("");
					    					}
					    					if(column==1){
					    						
					    						employee.setLastname("");
					    					}
					    					if(column==2){
					    						
					    						employee.setAddress1("");
					    					}
					    					if(column==3){
					    						
					    						employee.setAddress2("");
					    					}
					    					if(column==4){
					    						
					    						employee.setCity("");
					    					}
					    					if(column==5){
					    						state.setStateName("");
					    					}
					    					if(column==6){
					    						
					    						state.setStateCode("");
					    					}
					    					if(column==7){
					    						
					    						employee.setZipcode("");
					    					}
					    					if(column==8){
					    						employee.setHomephone("");
					    					}
					    					if(column==9){
					    						
					    						employee.setCellphone("");
					    					}
					    					if(column==10){
					    						
					    						employee.setFax("");
					    					}
					    					if(column==11){
					    						
					    						employee.setEmail("");
					    					}
					    					if(column==12){
					    						
					    						employee.setHireDate(null);
					    					}
					    					if(column==13){
					    						employeeRate.setHourlyRate(new BigDecimal(0.0));
					    					}
					    					if(column==14){
					    						employee.setAssignedPositions(new ArrayList<Position>());
					    					}
			    					   } else {
			    						   DataFormatter dataFormatter = new DataFormatter(Locale.US);
				    						
			    						   if(column==0){
					    						String firstname = (String)this.getExcelCellValue(xssfCell);
					    						employee.setFirstname(firstname);
					    					}
					    					if(column==1){
					    						String lastname = (String)this.getExcelCellValue(xssfCell);
					    						employee.setLastname(lastname);
					    					}
					    					
					    					if(column==2){
					    						String address1 = (String)this.getExcelCellValue(xssfCell);
					    						employee.setAddress1(address1);
					    					}
					    					if(column==3){
					    						String address2 = (String)this.getExcelCellValue(xssfCell);
					    						employee.setAddress2(address2);
					    					}
					    					if(column==4){
					    						String city = (String)this.getExcelCellValue(xssfCell);
					    						employee.setCity(city);
					    					}
					    					if(column==5){
					    						String stateName = (String)this.getExcelCellValue(xssfCell);
					    						state.setStateName(stateName);
					    						
					    					}
					    					if(column==6){
					    						String stateCode = (String)this.getExcelCellValue(xssfCell);
					    						state.setStateCode(stateCode);
					    					}
					    					if(column==7){
					    						
					    						String zipcode = dataFormatter.formatCellValue(xssfCell);//this.getExcelCellValue(hssfCell);
					    						if(zipcode.length()==4)
					    							zipcode = "0"+zipcode;
					    						employee.setZipcode(zipcode);
					    					}
					    					if(column==8){
					    						String homephone = dataFormatter.formatCellValue(xssfCell);;//this.getExcelCellValue(hssfCell);
					    						employee.setHomephone(homephone);
					    					}
					    					if(column==9){
					    						String cellphone = dataFormatter.formatCellValue(xssfCell);;//this.getExcelCellValue(hssfCell);
					    						employee.setCellphone(cellphone);
					    					}
					    					
					    					if(column==10){
					    						String fax = dataFormatter.formatCellValue(xssfCell);;//this.getExcelCellValue(hssfCell);
					    						employee.setFax(fax);
					    					}
					    					if(column==11){
					    						String email = (String)this.getExcelCellValue(xssfCell);
					    						org.apache.commons.validator.EmailValidator validator = org.apache.commons.validator.EmailValidator.getInstance();
					    						boolean valid = validator.isValid(email);
					    						if(valid)
					    							employee.setEmail(email);
					    						else
					    							employee.setEmail("test@test.com");
					    					}
					    					if(column==12){
					    						if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(xssfCell))
					    						{
					    						   
	
					    						    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					    						    Date hireDate = xssfCell.getDateCellValue();
					    						    employee.setHireDate(hireDate);
					    						}
					    						/*String dob = dataFormatter.formatCellValue(hssfCell);//this.getExcelCellValue(hssfCell);
					    						
												try {
													SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy");
													Date dateOfBirth = sdf.parse(dob);
													
													employee.setDob(dateOfBirth);
												} catch (ParseException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
					    						*/
					    					}
											if(column==13){
												
												String rateString = dataFormatter.formatCellValue(xssfCell);//this.getExcelCellValue(hssfCell);
												employeeRate.setRateStartDate(new Date());
												Date endDate = EmployeeUtil.getEmployeeRateEndDate();
												employeeRate.setRateEnddate(endDate);// set the date after 100 years
												parseAndSetHourlyRate(employeeRate,rateString);
												
												
											}
											if(column==14){
												String positionString = (String)this.getExcelCellValue(xssfCell);
												employee.setUploadedPositionString(positionString);
												String[] positions = positionString.split(",");
												uploadedPositions.addAll(Arrays.asList(positions));
												
											}
			    					   }
			    				}
			    				
			    				if(state!=null){
			    					String statecode = state.getStateCode();
			    					String statename = state.getStateName();
			    					if(statecode!=null){
			    						statename = getApplicationData().getStateName(statecode);
			    						state.setStateName(statename);
			    					}else if(statename!=null){
			    						statecode = getApplicationData().getStateCode(statename);
			    						state.setStateCode(statecode);
			    					}
			    					
			    				}
			    				uploadEmployeeList.add(employee);
			    				row++;
		    				}
		    				
		    			}
		    			
						
					}
	    			
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
        		
        }
        	
    }
	
	private void parseAndSetHourlyRate(EmployeeRate employeeRate,
			String rateString) {
		if(rateString.contains("$")){
			Locale locale = new Locale("en", "US");
			NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
			
			try {
				Number rate = fmt.parse(rateString);
				employeeRate.setHourlyRate(new BigDecimal(rate.doubleValue()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			String rate = StringUtils.trim(rateString);
			employeeRate.setHourlyRate(new BigDecimal(rate));
		}
		
		
	}

	private Object getExcelCellValue(HSSFCell hssfCell){
		int type = hssfCell.getCellType();
		Object value = null;
        if (type == HSSFCell.CELL_TYPE_STRING) {
        	value = hssfCell.getRichStringCellValue().toString();
            //System.out.println("[" + hssfCell.getRowIndex() + ", "+ hssfCell.getColumnIndex() + "] = STRING; Value = "+ hssfCell.getRichStringCellValue().toString());
        } else if (type == HSSFCell.CELL_TYPE_NUMERIC) {
        	value = hssfCell.getNumericCellValue();
            //System.out.println("[" + hssfCell.getRowIndex() + ", "+ hssfCell.getColumnIndex() + "] = NUMERIC; Value = "+ hssfCell.getNumericCellValue());
        } else if (type == HSSFCell.CELL_TYPE_BOOLEAN) {
        	value = hssfCell.getBooleanCellValue();
            //System.out.println("[" + hssfCell.getRowIndex() + ", "+ hssfCell.getColumnIndex() + "] = BOOLEAN; Value = "+ hssfCell.getBooleanCellValue());
        } else if (type == HSSFCell.CELL_TYPE_BLANK) {
        	value = "";
            //System.out.println("[" + hssfCell.getRowIndex() + ", "+ hssfCell.getColumnIndex() + "] = BLANK CELL");
        } else if (HSSFDateUtil.isCellDateFormatted(hssfCell)) {
            //System.out.println("The cell contains a date value: " + hssfCell.getDateCellValue());
        }
        
        return value;
	}
	private Object getExcelCellValue(XSSFCell xssfCell){
		int type = xssfCell.getCellType();
		Object value = null;
        if (type == XSSFCell.CELL_TYPE_STRING) {
        	value = xssfCell.getRichStringCellValue().toString();
            //System.out.println("[" + hssfCell.getRowIndex() + ", "+ hssfCell.getColumnIndex() + "] = STRING; Value = "+ hssfCell.getRichStringCellValue().toString());
        } else if (type == XSSFCell.CELL_TYPE_NUMERIC) {
        	value = xssfCell.getNumericCellValue();
            //System.out.println("[" + hssfCell.getRowIndex() + ", "+ hssfCell.getColumnIndex() + "] = NUMERIC; Value = "+ hssfCell.getNumericCellValue());
        } else if (type == XSSFCell.CELL_TYPE_BOOLEAN) {
        	value = xssfCell.getBooleanCellValue();
            //System.out.println("[" + hssfCell.getRowIndex() + ", "+ hssfCell.getColumnIndex() + "] = BOOLEAN; Value = "+ hssfCell.getBooleanCellValue());
        } else if (type == XSSFCell.CELL_TYPE_BLANK) {
        	value = "";
            //System.out.println("[" + hssfCell.getRowIndex() + ", "+ hssfCell.getColumnIndex() + "] = BLANK CELL");
        } else {
            //System.out.println("The cell contains a date value: " + hssfCell.getDateCellValue());
        }
        
        return value;
	}
	private List<Position> getAllAssignedPosition(Employee employee){
		String positionNames = (employee.getUploadedPositionString()==null)?"":employee.getUploadedPositionString();
		String[] positions = positionNames.split(",");
		List<Position> assignedPositions = new ArrayList<Position>();
		for(String posname : positions){
			if(!posname.equalsIgnoreCase("")){
				Position position = allActivePositions.get(posname);
				if(position!=null)
					assignedPositions.add(position);
			}
		}

		return assignedPositions;
	}
	public String sendInviteAllEmployee(){
		
		for(Employee employee : employeeList){
			String invitecode = this.getInviteCode();
			employee.setInvitecode(invitecode);
			employee.setTenant(tenant);
			employee.setRegion(regionBean.getSelectedRegion());
			employee.setEmployeeStatus("invitesent");
			employee.setAssignedPositions(getAllAssignedPosition(employee));
			Map<Object,Object> attributes = null;
			List<String> emailReceipients = new ArrayList<String>();
			try{
				String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/images");
				FileInputStream inputStream = new FileInputStream(path+"/inviteimage.jpg");
	            byte[] img_content = IOUtils.toByteArray(inputStream);
	            employee.setImageType("image/jpeg");
	            employee.setImageName(tenant.getCode()+"_"+"inviteimage.jpg");
	            employee.setImageBytes(img_content);
				EmployeeRate inviteEmpRate = employee.getEmpRate();
				inviteEmpRate.setRateStartDate(new Date());
				Date empRateEndDate = EmployeeUtil.getEmployeeRateEndDate();
				inviteEmpRate.setRateEnddate(empRateEndDate);// set the date after 100 years
				// save data
				String uploadStatus = employee.getEmployeeUploadStatus();
				if(uploadStatus!=null && uploadStatus.equalsIgnoreCase("upload")){
					getEmployeeService().inviteEmployee(employee);
					attributes = populateEmailAttributes(employee);
					emailReceipients.add(employee.getEmail());
					getInviteEmployeeSender().inviteEmployee(tenant, regionBean.getSelectedRegion(), attributes, employee.getEmail(),tenant.getEmailSender(),"invite employee");
					//getSender().inviteEmployee(inviteMessage, attributes, emailReceipients,tenant.getEmailSender());
				}
				//FacesUtils.addInfoMessage("sendInvBtn", " save sucessful");
				
				
			}catch (DataAccessException dae) {
				dae.printStackTrace();
			}catch (MailException me){
				me.printStackTrace();
				//getSender().inviteEmployee(inviteMessage, attributes, emailReceipients,tenant.getEmailSender());
			}catch(Exception exception){
				FacesUtils.addErrorMessage("Error!");
			}
		}
		FacesUtils.addInfoMessage("Information Saved,Invitation will be sent shortly");
		return "uploadEmployeeHome";
	}
	public void onDeleteUploadEmployee(ActionEvent actionEvent){
		Integer rowIndexId = Integer.parseInt(FacesUtils.getRequestParameter("currentRowIndex"));
		Employee employee = employeeList.remove(rowIndexId.intValue());
		getEmployeeService().deleteUploadedEmployee(employee, tenant);
		
		FacesUtils.addInfoMessage("Employee Deleted Successfully");
	}
	public String saveEmployeeExcelData(){
		
		getEmployeeService().saveUploadedEmployees(uploadEmployeeList,uploadedPositions);
		/*for(Employee employee : employeeList){
			List<String> receipients = new ArrayList<String>();
			Map<Object,Object> attributes = populateEmailAttributes(employee);
			receipients.add(employee.getEmail());
			getSender().inviteEmployee(inviteMessage, attributes, receipients);
		}*/
		return "uploadEmployeeHome?faces-redirect=true";
	}
	
	public String inviteUploadedEmployee(Employee uploadedEmployee){
		String invitecode = this.getInviteCode();
		uploadedEmployee.setInvitecode(invitecode);
		uploadedEmployee.setTenant(tenant);
		uploadedEmployee.setRegion(regionBean.getSelectedRegion());
		uploadedEmployee.setEmployeeStatus("invitesent");
		uploadedEmployee.setAssignedPositions(getAllAssignedPosition(uploadedEmployee));
		List<String> receipients = new ArrayList<String>();
		Map<Object,Object> attributes = null;
		
		try{
			String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/images");
			FileInputStream inputStream = new FileInputStream(path+"/inviteimage.jpg");
            byte[] img_content = IOUtils.toByteArray(inputStream);
            uploadedEmployee.setImageType("image/jpeg");
            uploadedEmployee.setImageName(tenant.getCode()+"_"+"inviteimage.jpg");
            uploadedEmployee.setImageBytes(img_content);
			EmployeeRate inviteEmpRate = uploadedEmployee.getEmpRate();
			inviteEmpRate.setRateStartDate(new Date());
			/*Date empRateEndDate = EmployeeUtil.getEmployeeRateEndDate();
			inviteEmpRate.setRateEnddate(empRateEndDate)*/;// set the date after 100 years
			uploadedEmployee = getEmployeeService().inviteEmployee(uploadedEmployee);
			attributes = populateEmailAttributes(uploadedEmployee);
			receipients.add(uploadedEmployee.getEmail());
			getInviteEmployeeSender().inviteEmployee(tenant, regionBean.getSelectedRegion(), attributes, uploadedEmployee.getEmail(),tenant.getEmailSender(),"invite employee");
			//getSender().inviteEmployee(inviteMessage, attributes, receipients,tenant.getEmailSender());
			//FacesUtils.addInfoMessage("sendInvBtn", " save sucessful");
			uploadedEmployee = new Employee(tenant,"invitesent");
			//FacesUtils.addInfoMessage("Employee Saved Successfully");
			FacesUtils.addInfoMessage("Information Saved,Invitation will be sent shortly");
		}catch (DataAccessException dae) {
			dae.printStackTrace();
		}catch (MailException me){
			me.printStackTrace();
			getSender().inviteEmployee(inviteMessage, attributes, receipients,tenant.getCompanyEmail());
		}catch(Exception exception){
			FacesUtils.addErrorMessage("Error!");
		}
		return "uploadEmployeeHome";
	}
	public String getInviteCode(){
		UUID uuid = UUID.randomUUID();
		String invitecode = uuid.toString().split("-")[0];
		return invitecode;
	}
	public void clearUploadedEmployeeData(ActionEvent actionEvent){
		uploadEmployeeList.clear();
	}
	public EmployeeSupport getEmployeeSupport() {
		return employeeSupport;
	}

	public void setEmployeeSupport(EmployeeSupport employeeSupport) {
		this.employeeSupport = employeeSupport;
	}
	public List<Employee> getUploadEmployeeList() {
		return uploadEmployeeList;
	}


	public void setUploadEmployeeList(List<Employee> uploadEmployeeList) {
		this.uploadEmployeeList = uploadEmployeeList;
	}


	public UploadedFile getUploadEmployeeDetailDocument() {
		return uploadEmployeeDetailDocument;
	}


	public void setUploadEmployeeDetailDocument(
			UploadedFile uploadEmployeeDetailDocument) {
		this.uploadEmployeeDetailDocument = uploadEmployeeDetailDocument;
	}

	public List<Employee> getEmployeeList() {
		return employeeList;
	}

	public void setEmployeeList(List<Employee> employeeList) {
		this.employeeList = employeeList;
	}

	public RegionBean getRegionBean() {
		return regionBean;
	}

	public void setRegionBean(RegionBean regionBean) {
		this.regionBean = regionBean;
	}




	public List<Employee> getFilteredEmployeeList() {
		return filteredEmployeeList;
	}




	public void setFilteredEmployeeList(List<Employee> filteredEmployeeList) {
		this.filteredEmployeeList = filteredEmployeeList;
	}

	
	
}
