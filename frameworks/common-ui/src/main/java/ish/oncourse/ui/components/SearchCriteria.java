package ish.oncourse.ui.components;

import ish.oncourse.model.Tag;
import ish.oncourse.utils.TimestampUtilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class SearchCriteria {
	@Inject
	private Request request;

	@Property
	@Parameter
	private boolean hasMapItemList;

	@Property
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

	@SetupRender
	void beforeRender() {
		browseTagPath = new ArrayList<Tag>();
		Tag browseTag = (Tag) request.getAttribute("browseTag");
		while (browseTag != null && browseTag.hasParentTag()) {
			browseTagPath.add(browseTag);
			browseTag = browseTag.getParent();
		}
		Collections.reverse(browseTagPath);
	}

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
