package ish.oncourse.ui.components;

import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.model.SearchParam;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

import java.util.Map;

public class SearchTermsClarification extends ISHCommon {

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
	
	public String getKm() {
		return paramsInError.get(SearchParam.km);
	}

    public String getAfter() {
        return paramsInError.get(SearchParam.after);
    }

    public String getBefore() {
        return paramsInError.get(SearchParam.before);
    }
}
