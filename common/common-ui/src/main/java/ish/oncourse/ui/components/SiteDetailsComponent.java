package ish.oncourse.ui.components;

import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.model.Room;
import ish.oncourse.model.Site;
import ish.oncourse.services.site.SiteDetails;
import ish.oncourse.services.IRichtextConverter;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.ArrayList;
import java.util.List;

public class SiteDetailsComponent extends ISHCommon {

	@Inject
	private IRichtextConverter textileConverter;

	@Parameter
	@Property
	private Room room;

	@Parameter
	@Property
	private Site site;

	@Parameter
	@Property
	private boolean collapseLocationMap;

	private SiteDetails siteDetails;

	@SetupRender
	public void beforeRender() {
		if (room != null) {
			siteDetails = SiteDetails.valueOf(room, textileConverter);
			site = siteDetails.getSite();
		}
		else if (site != null) {
			siteDetails = SiteDetails.valueOf(site, textileConverter);
		} else {
			siteDetails = SiteDetails.valueOf();
		}
	}

	public boolean isHasName() {
		return siteDetails.getRoomName() != null;
	}


	public boolean isHasAddress() {
		return siteDetails.hasAddress();
	}

	public boolean isHasPostCode() {
		return siteDetails.getPostcode() != null;
	}

	public boolean isHasDrivingDirections() {
		return siteDetails.getDrivingDirections() != null;
	}

	public boolean isHasPublicTransportDirections() {
		return siteDetails.getPublicTransportDirections() != null;
	}

	public boolean isHasSpecialInstructions() {
		return siteDetails.getSpecialInstructions() != null;
	}

	public List<Site> getMapSites() {
		List<Site> sites = new ArrayList<>(1);
		sites.add(site);
		return sites;
	}

	public String getDrivingDirections() {
		return siteDetails.getDrivingDirections();
	}

	public String getPublicTransportDirections() {
		return siteDetails.getPublicTransportDirections();
	}

	public String getSpecialInstructions() {
		return siteDetails.getSpecialInstructions();
	}
}
