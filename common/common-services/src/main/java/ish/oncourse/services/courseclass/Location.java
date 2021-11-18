/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.courseclass;

import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;

/**
 * User: akoiro
 * Date: 3/08/2016
 */
public class Location {
	private Double latitude;
	private Double longitude;
	private String address = null;

	private static final String MAP_URL_PATTERN = "https://maps.googleapis.com/maps/api/streetview?size=200x300&location=%s&key=AIzaSyD7S4AhXWmNeLMpa9OpJMz4DbGIEfFj2Ms";
	private static final String DEFAULT_STREET_VIEW_URL = "http://www.mindingthecampus.com/originals/campus.jpg";

	public Location(Double latitude, Double longitude, String state, String street, String suburb) {
		this.latitude = latitude;
		this.longitude = longitude;
		
		if (StringUtils.trimToNull(street) != null) {
			StringBuilder builder = new StringBuilder();
			if (StringUtils.trimToNull(state) != null) {
				builder.append(state);
				builder.append(" ");
			}
			builder.append(street);
			if (StringUtils.trimToNull(suburb) != null) {
				builder.append(" ");
				builder.append(suburb);
			}
			this.address = builder.toString();
		}
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getAddress() {
		return address;
	}

	public boolean hasCoordinates() {
		return latitude != null && longitude != null;
	}

	public boolean hasAddress() {
		return  StringUtils.trimToNull(address) != null;
	}


	public static String getStreetViewUrl(Location location) {
		if (location != null) {
			if (location.hasAddress()) {
				try {
					return  String.format(MAP_URL_PATTERN, java.net.URLEncoder.encode(location.getAddress(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					return DEFAULT_STREET_VIEW_URL;
				}
			}

			if (location.hasCoordinates()) {
				return  String.format(MAP_URL_PATTERN, String.format("%f,%f", location.getLatitude(), location.getLongitude()));
			}
		}
		return DEFAULT_STREET_VIEW_URL;
	}
}
