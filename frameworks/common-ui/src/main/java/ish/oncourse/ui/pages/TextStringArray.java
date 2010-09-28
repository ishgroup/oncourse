package ish.oncourse.ui.pages;

import java.util.Collections;
import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class TextStringArray {

	@Inject 
	private Request request;
	
	private List<String> strings;
	
	@Property
	private String str;
	
	@SuppressWarnings("unchecked")
	@SetupRender
	void beforeRender(){
		strings=(List<String>) request.getAttribute("stringsList");
		if(strings==null){
			strings = Collections.emptyList();
		}
	}

	/**
	 * @return the strings
	 */
	public List<String> getStrings() {
		return strings;
	}

	/**
	 * @param strings the strings to set
	 */
	public void setStrings(List<String> strings) {
		this.strings = strings;
	}
	
}
