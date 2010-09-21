package ish.oncourse.ui.components;

import ish.oncourse.model.Tag;
import ish.oncourse.utils.TimestampUtilities;

import java.util.List;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class SearchCriteria {

	@Property
	@Parameter
	private boolean hasMapItemList;
	
	@Property
	@Parameter
	private List<Tag> browseTagPath;

	@Property
	@Parameter
	private String searchString;

	@Property
	@Parameter
	private List<String> searchDays;
	
	@Property
	@Parameter
	private String searchTime;
	
	@Property
	@Parameter
	private String searchPrice;
	
	@Property
	@Parameter
	private String searchNear;

	@Property
	private Tag tag;

	@Property
	private int tagIndex;

	public boolean getShowTagRaquo() {
		return tagIndex < browseTagPath.size() - 1;
	}

	public boolean isHasSearchDays() {
		return searchDays != null && !searchDays.isEmpty()
				&& (browseTagPath != null || searchString != null);
	}

	public boolean isSearchingWeekday() {
		if (searchDays == null) {
			return false;
		}
		for (String day : searchDays) {
			if (TimestampUtilities.DaysOfWorkingWeekNamesLowerCase.contains(day
					.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	public boolean isSearchingWeekend() {
		if (searchDays == null) {
			return false;
		}
		for (String day : searchDays) {
			if (TimestampUtilities.DaysOfWeekendNamesLowerCase.contains(day
					.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

}
