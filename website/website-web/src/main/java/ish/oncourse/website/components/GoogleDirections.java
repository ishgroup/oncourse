package ish.oncourse.website.components;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class GoogleDirections {
	
	@Property
	@Parameter
	private Double latitude;
	
	@Property
	@Parameter
	private Double longitude;

	@Property
	@Parameter
	private Boolean hideDirections;
	
	@Property
	@Parameter
	private Boolean showNearMarker;

	@Property
	@Parameter
	private Boolean showMapItems;
	
	@Property
	@Parameter
	private Boolean showLocationMap;
	
	@Parameter
	private Boolean collapseLocationMap;
	
	@Property
	@Parameter
	private Boolean showDirectionsMap;
	
	@Property
	@Parameter
	private Boolean near;

	public boolean isHasLocation() {
		return latitude != null && longitude != null;
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

	public String getDirMapId() {
		return showDirectionsMap ? "dirmap" : "dirmapDelayed";
	}
	
	public boolean isCollapseLocationMap(){
		return Boolean.TRUE.equals(collapseLocationMap);
	}

	public String getLocationClass() {
		return "blockwrap"
				+ ((isCollapseLocationMap()) ? " collapsedLocationMap" : "");
	}

	public String getMapClass() {
		return showLocationMap ? "map" : "mapDelayed";
	}
}
