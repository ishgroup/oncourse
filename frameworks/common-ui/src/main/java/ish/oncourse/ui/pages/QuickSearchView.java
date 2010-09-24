package ish.oncourse.ui.pages;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class QuickSearchView {
	
	@Inject 
	private Request request;
	@Property
	private String searchString;

	@SetupRender
	void beforeRender(){
		searchString=request.getParameter("text");
	}
	 
	public boolean isHasResults()
	{
		return isHasLocationDetailList() || isHasMatchingCourseList() || isHasCourseList() || isHasTagGroupResultsList();
	}

	private boolean isHasLocationDetailList() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isHasMatchingCourseList() {
		// TODO Auto-generated method stub
		return false;
	}
	

	private boolean isHasCourseList() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isHasTagGroupResultsList() {
		// TODO Auto-generated method stub
		return false;
	}
	private boolean isHasSearchingLocationsSearchString(){
		return false;
	}
}
