package ish.oncourse.ui.pages;

import java.util.ArrayList;
import java.util.List;

import ish.oncourse.model.Site;
import ish.oncourse.services.sites.ISitesService;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class SiteDetails {

	@Inject
	private Request request;
	
	@Inject 
	private ISitesService sitesService;
	
	@Property
	private Site site;
	
	@SetupRender
	public void beforeRender() {
		String angelId = (String) request.getAttribute("siteId");
		site = sitesService.getSite(Site.ANGEL_ID_PROPERTY, Long.valueOf(angelId));
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
