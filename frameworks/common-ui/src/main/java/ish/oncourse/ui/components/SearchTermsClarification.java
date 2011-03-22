package ish.oncourse.ui.components;

import ish.oncourse.services.search.SearchParam;

import java.util.Map;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class SearchTermsClarification {

	@Property
	@Parameter
	private Map<SearchParam, String> paramsInError;
	
	public String getSearchNear(){
		return paramsInError.get(SearchParam.near);
	}
	
	public String getSearchDay(){
		return paramsInError.get(SearchParam.day);
	}
	
	public String getSearchTime(){
		return paramsInError.get(SearchParam.time);
	}
	
	public String getSearchPrice(){
		return paramsInError.get(SearchParam.price);
	}
	
	public String getSearchTag(){
		return paramsInError.get(SearchParam.subject);
	}
}
