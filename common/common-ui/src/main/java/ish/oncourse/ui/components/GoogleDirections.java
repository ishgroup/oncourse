package ish.oncourse.ui.components;

import ish.oncourse.model.Site;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GoogleDirections {
	@Inject
	private Request request;

	@Parameter
	private List<Site> sites;

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

	@Parameter
	@Property
	private boolean showLocationMap;

	@Property
	private String directionsFrom;

	public static final String NO_MATCH_IMAGE = "/s/img/marker-no-match.png";
	public static final String PARTIAL_IMAGE = "/s/img/marker-match-partial.png";
	public static final String PERFECT_IMAGE = "/s/img/marker-match-exact.png";

	@SetupRender
	public void beforeRender() {
		sitesArray = new ArrayList<>();
		for (Site item : sites) {
			if (item.isHasCoordinates()) {
				List<Object> element = new ArrayList<>();
				// latitude
				element.add(item.getLatitude().doubleValue());
				// longitude
				element.add(item.getLongitude().doubleValue());
				// title
				element.add("\"" + item.getName() + "\"");
				// site's suburb
				element.add("\"" + item.getSuburb() + "\"");
				// site's public id
				element.add(item.getAngelId());
				// url to the site details
				element.add("\"/site/" + item.getAngelId() + "\"");
				// marker's image
				if (focuses == null) {
					element.add("\"/s/img/marker1.png\"");
				} else {
					element.add("\"" + imageSrc(focuses.get(item.getId())) + "\"");
				}
				// marker's shadow
				element.add("\"/s/img/marker-shadow1.png\"");
				// iconWidth
				element.add(20);
				// iconHeight
				element.add(34);
				// shadowWidth
				element.add(36);
				// shadowHeight
				element.add(34);
				// anchorx
				element.add(10);
				// anchory
				element.add(34);
				// infoWindowx
				element.add(10);
				// infoWindowy
				element.add(5);
				sitesArray.add(element);
			}
		}
		setupMapPosition();
	}

	private String imageSrc(float focusMatch) {
		String src = PERFECT_IMAGE;
		if (focusMatch < 0.5d) {
			src = PARTIAL_IMAGE;
		} else if (focusMatch < 1d) {
			src = NO_MATCH_IMAGE;
		}
		return src;
	}

	public void setupMapPosition() {
		/**
		 * if at least one site has coordinate  we should show google map.
		 */

		for (Site item : sites) {
			if (item.getLatitude() == null || item.getLongitude() == null) {
				continue;
			}
			mapPositionLatitude = item.getLatitude().doubleValue();
			mapPositionLongitude = item.getLongitude().doubleValue();
			break;
		}
	}


	public boolean isHasGlobalPosition() {
		String near = request.getParameter("near");
		return near != null;
	}

	public String getSearchingNear() {
		return request.getParameter("near");
	}

	public boolean isHasLocation() {
		return mapPositionLatitude != null && mapPositionLongitude != null;
	}

	public String getAddress() {
		if (sites != null && !sites.isEmpty()) {
			Site site = sites.get(0);
			return site.getStreet() + ", " + site.getSuburb() + " " + site.getPostcode()
					+ ", Australia".replace("&amp;", "&");
		}
		return "";
	}

	public boolean isShowMapItems() {
		return true;
	}

	public boolean isHideDirections() {
		return !showDirections;
	}

	public String getDirMapId() {
		return showDirectionsMap ? "dirmap" : "dirmapDelayed";
	}

	public String getLocationClass() {
		return "blockwrap" + ((collapseLocationMap) ? " collapsedLocationMap" : "");
	}

	public String getMapClass() {
		return showLocationMap ? "map" : "mapDelayed";
	}

	/**
	 * google option "center" is needed only we show one marker. when we show more then one marker google
	 * api 3.exp calculates this value by itself.
	 */
	public boolean needCenter()
	{
		return sites != null && sites.size() == 1;
	}

}
