package ish.oncourse.ui.components;

import ish.oncourse.model.Room;
import ish.oncourse.model.Site;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.util.ValidationErrors;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.ArrayList;
import java.util.List;

public class SiteDetailsComponent {

	@Inject
	private ITextileConverter textileConverter;

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
		if (room == null) {
			return false;
		}
		String name = room.getName();
		return isStringNotEmpty(name) && !"*default*".equals(name.trim().toLowerCase());
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
		List<Site> sites = new ArrayList<>(1);
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

	public String getDrivingDirections() {
		return textileConverter.convertCustomTextile(site.getDrivingDirections(), new ValidationErrors());
	}

	public String getPublicTransportDirections() {
		return textileConverter.convertCustomTextile(site.getPublicTransportDirections(), new ValidationErrors());
	}

	public String getSpecialInstructions() {
		return textileConverter.convertCustomTextile(site.getSpecialInstructions(), new ValidationErrors());
	}
}
