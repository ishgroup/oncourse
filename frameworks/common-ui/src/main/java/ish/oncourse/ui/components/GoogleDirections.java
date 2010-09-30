package ish.oncourse.ui.components;

import ish.oncourse.model.Site;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class GoogleDirections {
	@Inject
	private Request request;

	@Parameter
	private Collection<Site> sites;
	
	@Parameter
	private Map<Integer, Float> focuses;
	

	@Property
	private List<List<Object>> sitesArray;

	@Property
	private Double mapPositionLatitude;

	@Property
	private Double mapPositionLongitude;

	@Parameter
	private boolean showDirections;

	@Parameter
	@Property
	private boolean collapseLocationMap;

	@Parameter
	@Property
	private boolean showDirectionsMap;

	public static final String NO_MATCH_IMAGE = "/s/img/marker-no-match.png";
	public static final String PARTIAL_IMAGE = "/s/img/marker-match-partial.png";
	public static final String PERFECT_IMAGE = "/s/img/marker-match-exact.png";
	
	
	@SetupRender
	public void beforeRender() {
		sitesArray = new ArrayList<List<Object>>();
		for (Site item : sites) {
			if (item.isHasCoordinates()) {
				List<Object> element = new ArrayList<Object>();
				//latitude
				element.add(item.getLatitude().doubleValue());
				//longitude
				element.add(item.getLongitude().doubleValue());
				//title
				element.add("\"" + item.getName() + "\"");
				//site's suburb
				element.add("\"" + item.getSuburb() + "\"");
				//site's public id
				element.add(item.getAngelId());
				// marker's image
				if(focuses==null){
					element.add("\"/s/img/marker1.png\"");
				}else{
					element.add("\""+imageSrc(focuses.get(item.getId()))+"\"");
				}
				// marker's shadow
				element.add("\"/s/img/marker-shadow1.png\"");
				//iconWidth
				element.add(20);
				//iconHeight
				element.add(34);
				//shadowWidth
				element.add(36);
				//shadowHeight
				element.add(34);
				//anchorx
				element.add(10);
				//anchory
				element.add(34);
				//infoWindowx
				element.add(10);
				//infoWindowy
				element.add(5);
				sitesArray.add(element);
			}
		}
		setupMapPosition();
	}
	private String imageSrc(float focusMatch)
	{
		String src = PERFECT_IMAGE;
		if ( focusMatch < 0.5d )
		{
			src = PARTIAL_IMAGE;
		}
		else if ( focusMatch < 1d )
		{
			src = NO_MATCH_IMAGE;
		}
		return src;
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
		String near=request.getParameter("near");
		return near!=null;
	}

	public String getSearchingNear() {
		//TODO convert possible geohash to something suitable
		return request.getParameter("near");
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
		return !showDirections;
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

	public String getLocationClass() {
		return "blockwrap"
				+ ((collapseLocationMap) ? " collapsedLocationMap" : "");
	}

	public String getMapClass() {
		return isShowLocationMap() ? "map" : "mapDelayed";
	}

}
