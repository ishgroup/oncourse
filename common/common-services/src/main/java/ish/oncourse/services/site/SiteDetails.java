/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.site;

import ish.oncourse.model.Room;
import ish.oncourse.model.Site;
import ish.oncourse.services.IReachtextConverter;
import ish.oncourse.util.ValidationErrors;

import static org.apache.commons.lang3.StringUtils.trimToNull;

/**
 * User: akoiro
 * Date: 9/08/2016
 */
public class SiteDetails {
	public static final String DEFAULT_ROOM_NAME = "*default*";

	private Site site;
	private Room room;

	private IReachtextConverter textileConverter;

	private String roomName;
	private String siteName;
	private String street;
	private String suburb;
	private String state;
	private String postcode;

	private String drivingDirections;
	private String publicTransportDirections;
	private String specialInstructions;

	public String getStreet() {
		return street;
	}

	public String getSuburb() {
		return suburb;
	}

	public String getState() {
		return state;
	}

	public String getPostcode() {
		return postcode;
	}

	public Site getSite() {
		return site;
	}

	public Room getRoom() {
		return room;
	}

	public String getRoomName() {
		return roomName;
	}

	public String getSiteName() {
		return siteName;
	}

	public String getDrivingDirections() {
		return drivingDirections;
	}

	public String getPublicTransportDirections() {
		return publicTransportDirections;
	}

	public String getSpecialInstructions() {
		return specialInstructions;
	}

	public boolean hasAddress() {
		return street != null && suburb != null;
	}

	private void init() {
		initName();
		initAddress();
		initDirections();
	}

	private void initName() {
		if (room != null) {
			roomName = trimToNull(room.getName());
			if (roomName != null && DEFAULT_ROOM_NAME.equals(roomName.toLowerCase())) {
				roomName = null;
			}
		}
		if (site != null) {
			siteName = trimToNull(site.getName());
		}
	}

	private void initAddress() {
		if (site != null) {
			street = trimToNull(site.getStreet());
			suburb = trimToNull(site.getSuburb());
			postcode = trimToNull(site.getPostcode());
			state = trimToNull(site.getState());
		}
	}

	private void initDirections() {
		if (site != null) {
			drivingDirections = trimToNull(site.getDrivingDirections());
			if (drivingDirections != null) {
				drivingDirections = textileConverter.convertCustomText(drivingDirections, new ValidationErrors());
			}
			publicTransportDirections = trimToNull(site.getPublicTransportDirections());
			if (publicTransportDirections != null) {
				publicTransportDirections = textileConverter.convertCustomText(publicTransportDirections, new ValidationErrors());
			}
			specialInstructions = trimToNull(site.getSpecialInstructions());
			if (specialInstructions != null) {
				specialInstructions = textileConverter.convertCustomText(specialInstructions, new ValidationErrors());
			}
		}
	}


	public static SiteDetails valueOf(Room room, IReachtextConverter textileConverter) {
		SiteDetails siteDetails = new SiteDetails();
		siteDetails.room = room;
		siteDetails.site = room.getSite();
		siteDetails.textileConverter = textileConverter;
		siteDetails.init();
		return siteDetails;
	}

	public static SiteDetails valueOf(Site site, IReachtextConverter textileConverter) {
		SiteDetails siteDetails = new SiteDetails();
		siteDetails.room = null;
		siteDetails.site = site;
		siteDetails.textileConverter = textileConverter;
		siteDetails.init();
		return siteDetails;
	}

	public static SiteDetails valueOf() {
		SiteDetails siteDetails = new SiteDetails();
		siteDetails.room = null;
		siteDetails.site = null;
		siteDetails.textileConverter = null;
		siteDetails.init();
		return siteDetails;
	}

}
