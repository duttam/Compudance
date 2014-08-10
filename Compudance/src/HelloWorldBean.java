

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="helloWorld")
@ViewScoped
public class HelloWorldBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String string;
	private String secondString;

	@PostConstruct
	public void init(){
		string = "this is a sinple demo test";
		secondString = "this is a second String";
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	public String getSecondString() {
		return secondString;
	}

	public void setSecondString(String secondString) {
		this.secondString = secondString;
	}

	
}
