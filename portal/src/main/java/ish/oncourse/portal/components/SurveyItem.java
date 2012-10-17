package ish.oncourse.portal.components;

import ish.oncourse.model.Survey;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class SurveyItem {
	
	@Parameter(required = true, cache = false)
	@Property
	private Survey survey;
	
	@Inject
	private Request request;
	
	public String getContextPath() {
		return request.getContextPath();
	}

}
