package ish.oncourse.ui.components;

import ish.oncourse.model.Room;
import ish.oncourse.model.Site;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

public class SiteDetailsComponent {
	@Parameter
	@Property
	private Room room;

	@Parameter
	@Property
	private Site site;

	@Parameter
	@Property
	private boolean collapseLocationMap;

	public boolean isHasName() {
		if(room==null){
			return false;
		}
		String name = room.getName();
		return isStringNotEmpty(name)
				&& !"*default*".equals(name.trim().toLowerCase());
	}

	@SetupRender
	public void beforeRender() {
		if (room != null) {
			site = room.getSite();
		}
	}

	public boolean isHasAddress() {
		String street = site.getStreet();
		String suberb = site.getSuburb();
		return isStringNotEmpty(street) && isStringNotEmpty(suberb);
	}

	public boolean isHasPostCode() {
		String postcode = site.getPostcode();
		return isStringNotEmpty(postcode);
	}

	public boolean isHasDrivingDirections() {
		return isStringNotEmpty(site.getDrivingDirections());
	}

	public boolean isHasPublicTransportDirections() {
		return isStringNotEmpty(site.getPublicTransportDirections());
	}

	public boolean isHasSpecialInstructions() {
		return isStringNotEmpty(site.getSpecialInstructions());
	}

	public List<Site> getMapSites() {
		List<Site> sites = new ArrayList<Site>(1);
		sites.add(site);
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
