

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


	@PostConstruct
	public void init(){
		string = "this is a sinple demo test";
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	
}
