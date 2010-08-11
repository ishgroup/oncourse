package ish.oncourse.website.components;

import ish.oncourse.model.Site;
import ish.oncourse.website.util.GoogleMapItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

public class GoogleMapItemList {

	@Parameter
	private Collection<Site> sites;

	private List<GoogleMapItem> mapItemList;

	@Property
	private Double mapPositionLatitude;

	@Property
	private Double mapPositionLongitude;

	@SetupRender
	public void beforeRender() {
		mapItemList = getMapItemList();
		setupMapPosition();
	}

	public boolean isHasMapItemList() {
		return !mapItemList.isEmpty();
	}

	private List<GoogleMapItem> getMapItemList() {
		List<GoogleMapItem> mapItems = new ArrayList<GoogleMapItem>();
		for (Site site : sites) {
			float bestFocusMatch = 1f;// TODO
			// CourseClassInMemoryFilter.focusMatchForSite(
			// context(), site );
			mapItems.add(GoogleMapItem.getMapItemForSite(site, bestFocusMatch));
		}

		return mapItems;
	}

	public void setupMapPosition() {

		double avLat = 0;
		double avLong = 0;
		int count = 0;
		Double firstLat = null;
		Double firstLong = null;
		for (GoogleMapItem item : mapItemList) {
			if (item.getLatitude() == null || item.getLongitude() == null) {
				continue;
			}
			Double lat = new Double(item.getLatitude().toString());
			Double lon = new Double(item.getLongitude().toString());
			if (firstLat == null) {
				firstLat = lat;
			}
			if (firstLong == null) {
				firstLong = lon;
			}

			// UGLY HACK to ignore ridiculous site lat longs
			if (Math.abs(firstLat - lat) > 10) {
				continue;
			}
			if (Math.abs(firstLong - lon) > 10) {
				continue;
			}
			count++;
			avLat = avLat + lat;
			avLong = avLong + lon;
		}
		if (count > 0) {
			mapPositionLatitude = avLat / count;
			mapPositionLongitude = avLong / count;
		} else {
			// TODO set global position
		}
	}
	
	public boolean isHasGlobalPosition(){
		//TODO 
		return false;
	}
	
	public boolean isSearchingNear(){
		//TODO 
		return false;
	}
}
