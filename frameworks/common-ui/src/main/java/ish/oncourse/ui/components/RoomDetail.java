package ish.oncourse.ui.components;

import ish.oncourse.model.Room;
import ish.oncourse.model.Site;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class RoomDetail {

	@Parameter(required = true)
	@Property
	private Room room;

	public boolean isHasName() {
		String name = room.getName();
		return isStringNotEmpty(name)
				&& !"*default*".equals(name.trim().toLowerCase());
	}

	public boolean isHasAddress() {
		String street = room.getSite().getStreet();
		String suberb = room.getSite().getSuburb();
		return isStringNotEmpty(street) && isStringNotEmpty(suberb);
	}

	public boolean isHasPostCode() {
		String postcode = room.getSite().getPostcode();
		return isStringNotEmpty(postcode);
	}

	public boolean isHasDrivingDirections() {
		return isStringNotEmpty(room.getSite().getDrivingDirections());
	}

	public boolean isHasPublicTransportDirections() {
		return isStringNotEmpty(room.getSite().getPublicTransportDirections());
	}

	public boolean isHasSpecialInstructions() {
		return isStringNotEmpty(room.getSite().getSpecialInstructions());
	}

	public List<Site> getMapSites() {
		List<Site> sites = new ArrayList<Site>(1);
		sites.add(room.getSite());
		return sites;
	}

	/**
	 * @param str
	 * @return
	 */
	private boolean isStringNotEmpty(String str) {
		return str != null && !"".equals(str);
	}
}
