package ish.oncourse.ui.components;

import ish.oncourse.model.Tag;
import ish.oncourse.services.tag.ITagService;
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
	
	@Inject
	private ITagService tagService;

	@Property
	@Parameter
	private boolean hasMapItemList;

	@Property
	private List<Tag> browseTagPath;

	@Property
	private String searchString;

	@Property
	private List<String> searchDays;

	@Property
	private String searchTime;

	@Property
	private String searchPrice;

	@Property
	@Parameter
	private String searchNear;

	@Property
	private Tag tag;

	@Property
	private int tagIndex;

	@Property
	private String[] tagNames;

	@Property
	private String tagName;

	@SetupRender
	void beforeRender() {
		browseTagPath = new ArrayList<>();

		Tag browseTag = tagService.getBrowseTag();
		
		while (browseTag != null && browseTag.hasParentTag()) {
			browseTagPath.add(browseTag);
			browseTag = browseTag.getParent();
		}
		Collections.reverse(browseTagPath);
		searchString = request.getParameter("s");
		String day = request.getParameter("day");
		if(day!=null && !"".equals(day)){
			searchDays=new ArrayList<>(1);
			searchDays.add(day);
		}
		searchTime = request.getParameter("time");
		searchPrice = request.getParameter("price");
		String near = request.getParameter("near");
		//TODO if near is geohash, get the human place view
		searchNear = near;

		tagNames = request.getParameters("tag");
	}

	public boolean getAddTagComma() {
		return tagIndex > 0;
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
			if("weekday".equalsIgnoreCase(day)){
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
			if("weekend".equalsIgnoreCase(day)){
				return true;
			}
		}
		return false;
	}

}
