

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="helloWorld")
@ViewScoped
public class HelloWorldBean2 implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String string;
	private String thirdString;

	@PostConstruct
	public void init(){
		string = "this is a sinple demo test";
		thirdString = "this is a second String";
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	public String getThirdString() {
		return thirdString;
	}

	public void setThirdString(String thirdString) {
		this.thirdString = thirdString;
	}

	

	
}
