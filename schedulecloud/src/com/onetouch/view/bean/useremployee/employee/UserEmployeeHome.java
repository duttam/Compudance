package com.onetouch.view.bean.useremployee.employee;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.dao.DataAccessException;

import org.springframework.mail.MailException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.onetouch.model.domainobject.CustomUser;

import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.login.LoginBean;
import com.onetouch.view.bean.menu.MainMenuBean;
import com.onetouch.view.bean.util.SupportBean;
import com.onetouch.view.util.FacesUtils;


@ManagedBean(name="userEmployeeHome") 
@RequestScoped
public class UserEmployeeHome extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Employee employee;
	private CustomUser user;
	private String destination="C:\\OneTouch\\";
	private UploadedFile employeeImage;
	private Tenant tenant;
	@ManagedProperty(value="#{mainMenuBean}")
    private MainMenuBean mainMenuBean;
	@PostConstruct
	public void initUserEmployee(){
		UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		if(authenticationToken.isAuthenticated())
			user = (CustomUser)authenticationToken.getPrincipal();
		tenant = user.getTenant();
		SupportBean supportBean = (SupportBean)FacesUtils.getManagedBean("supportBean");
			supportBean.setTenant(tenant);
			tenantContext.setTenant(tenant);
		employee = getEmployeeService().getEmplyeePositionById(user.getEmp_id(), tenant);
		employee.setUserName(user.getUsername());
		employee.setTenant(tenant);
		if(mainMenuBean!=null)
			mainMenuBean.setActiveMenuIndex(0);
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	
	public String editEmployee(){
		try{
			if(employeeImage!=null && employeeImage.getFileName().length()>0){
				String imageName = employeeImage.getFileName();
				String imageType = employeeImage.getContentType();
				
				employee.setImageType(imageType);
				employee.setImageName(tenant.getCode()+"_"+employee.getId()+"_"+imageName);
				byte[] imageBytes = employeeImage.getContents();
				
				employee.setImageBytes(imageBytes);
			}else{
				String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/images");
				FileInputStream inputStream = new FileInputStream(path+"/inviteimage.jpg");
	            employee.setImageType("image/jpeg");
				employee.setImageName(tenant.getCode()+"_"+"inviteimage.jpg");
				InputStream imageIS = employee.getInputStream();
				byte[] imageContent;
				try {
					imageContent = IOUtils.toByteArray(imageIS);
					employee.setImageBytes(imageContent);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(user.getAuthorities());
			if(authorities.contains(new SimpleGrantedAuthority("ROLE_ACCESS_SECURED")))
				getEmployeeService().editEmployee(false,employee,new ArrayList<Position>(),new ArrayList<Position>(),new ArrayList<Location>(),new ArrayList<Location>(),new ArrayList<Location>(),new ArrayList<Location>());
		
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getFlash().setKeepMessages(true);
			FacesUtils.addInfoMessage("Employee details updated Successfully");
			if(FacesUtils.isMobileRequest())
				return null;
			else
				return "employeeHome?faces-redirect=true";
		}catch (DataAccessException dae) {
			dae.printStackTrace();
		}catch(Exception exception){
			FacesUtils.addErrorMessage("Error!");
		}
		return null;
	}
	
	public void uploadImage() {  
        FacesMessage msg = new FacesMessage("Success! ", employeeImage.getFileName() + " is uploaded.");  
        FacesContext.getCurrentInstance().addMessage(null, msg);
        // Do what you want with the file        
        try {
            copyFile(employeeImage.getFileName(), employeeImage.getInputstream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }  

    public void copyFile(String fileName, InputStream in) {
           try {
             
             
                // write the inputStream to a FileOutputStream
                OutputStream out = new FileOutputStream(new File(destination+tenant.getCode()+"\\"+fileName));
             
                int read = 0;
                byte[] bytes = new byte[1024];
             
                while ((read = in.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }
             
                in.close();
                out.flush();
                out.close();
             
                System.out.println("New file created!");
                } catch (IOException e) {
                System.out.println(e.getMessage());
                }
    }

	public UploadedFile getEmployeeImage() {
		return employeeImage;
	}

	public void setEmployeeImage(UploadedFile employeeImage) {
		this.employeeImage = employeeImage;
	}

	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public MainMenuBean getMainMenuBean() {
		return mainMenuBean;
	}

	public void setMainMenuBean(MainMenuBean mainMenuBean) {
		this.mainMenuBean = mainMenuBean;
	}
    
    
}
