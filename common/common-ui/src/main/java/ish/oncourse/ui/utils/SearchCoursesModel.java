package ish.oncourse.ui.utils;

import ish.oncourse.model.SearchParam;

import java.io.Serializable;
import java.util.Map;

public class SearchCoursesModel implements Serializable{
	private static final long serialVersionUID = 746880776157047697L;
	private Double[] locationPoints;
	private String postcode;
	private Map<SearchParam, Object> searchParams;
	
	public SearchCoursesModel(Double[] locationPoints, String postcode, Map<SearchParam, Object> searchParams) {
		this.locationPoints = locationPoints;
		this.postcode = postcode;
		this.searchParams = searchParams;
	}

	/**
	 * @return the locationPoints
	 */
	public Double[] getLocationPoints() {
		return locationPoints;
	}

	/**
	 * @return the postcode
	 */
	public String getPostcode() {
		return postcode;
	}

	/**
	 * @return the searchParams
	 */
	public Map<SearchParam, Object> getSearchParams() {
		return searchParams;
	}
}
