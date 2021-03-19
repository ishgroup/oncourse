package ish.oncourse.ui.pages.internal;

import ish.oncourse.ui.base.ISHCommon;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

import java.util.List;

public class SuburbAutocomplete extends ISHCommon {

	private List<String> strings;
	
	@Property
	private String str;
	
	@SuppressWarnings("unchecked")
	@SetupRender
	void beforeRender(){
		strings=(List<String>) request.getAttribute("stringsList");
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
