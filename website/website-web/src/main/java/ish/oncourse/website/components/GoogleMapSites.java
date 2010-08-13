package ish.oncourse.website.components;

import ish.oncourse.model.Site;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

public class GoogleMapSites {

	@Parameter
	private Collection<Site> sites;

	@Property
	private List<List<Object>> sitesArray;

	@Property
	private Double mapPositionLatitude;

	@Property
	private Double mapPositionLongitude;

	@SetupRender
	public void beforeRender() {
		sitesArray = new ArrayList<List<Object>>();
		for (Site item : sites) {
			List<Object> element = new ArrayList<Object>();
			element.add(item.getLatitude().doubleValue());
			element.add(item.getLongitude().doubleValue());
			element.add("\"" + item.getName() + "\"");
			element.add("\"" + item.getSuburb() + "\"");
			element.add(item.getId());
			sitesArray.add(element);
		}
		setupMapPosition();
	}

	public boolean isHasMapItemList() {
		return !sites.isEmpty();
	}

	public void setupMapPosition() {

		double avLat = 0;
		double avLong = 0;
		int count = 0;
		Double firstLat = null;
		Double firstLong = null;
		for (Site item : sites) {
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

	public boolean isHasGlobalPosition() {
		// TODO
		return false;
	}

	public boolean isSearchingNear() {
		// TODO
		return false;
	}

	public boolean isHasLocation() {
		return mapPositionLatitude != null && mapPositionLongitude != null;
	}

	public String getAddress() {
		return "";// TODO return valueForBinding( "street" ) + ", " +
		// valueForBinding( "suburb" ) + " " + valueForBinding(
		// "postcode" ) + ", Australia".replace("&amp;", "&");
	}

	public String getDirectionsFrom() {
		// TODO return myWebHostName().googleDirectionsFrom();
		return "";
	}

	public boolean isShowMapItems() {
		return true;
	}

	public boolean isHideDirections() {
		return true;
	}

	public boolean isShowLocationMap() {
		return false;
	}

	public boolean isShowDirectionsMap() {
		return false;
	}

	public String getDirMapId() {
		return isShowDirectionsMap() ? "dirmap" : "dirmapDelayed";
	}

	public boolean isCollapseLocationMap() {
		return true;
	}

	public String getLocationClass() {
		return "blockwrap"
				+ ((isCollapseLocationMap()) ? " collapsedLocationMap" : "");
	}

	public String getMapClass() {
		return isShowLocationMap() ? "map" : "mapDelayed";
	}

}
