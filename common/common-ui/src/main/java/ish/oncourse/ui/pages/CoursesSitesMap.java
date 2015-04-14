package ish.oncourse.ui.pages;

import ish.oncourse.model.Site;
import ish.oncourse.services.sites.ISitesService;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoursesSitesMap {
	@Inject
	private Request request;

	@Inject
	private ISitesService sitesService;

	@Property
	private List<Site> mapSites;

	@Property
	private Map<Long, Float> focusesForMapSites;

	@SetupRender
	public void beforeRender() {
		mapSites = new ArrayList<>();
		focusesForMapSites = new HashMap<>();

		String sitesParamStr = request.getParameter("sites");
		List<Long> sitesIds = new ArrayList<>();

		if (sitesParamStr != null) {
			String[] splittedSites = sitesParamStr.split(",");
			for (String siteParam : splittedSites) {
				int focusStartIndex = siteParam.indexOf("(");
				int focusEndIndex = siteParam.indexOf(")");
				Float focus = null;
				if (focusStartIndex != -1 && focusEndIndex != -1 && focusEndIndex > focusStartIndex) {
					String focusStr = siteParam.substring(focusStartIndex + 1, focusEndIndex);
					if (focusStr.matches("\\d+([.]\\d+)?")) {
						focus = Float.valueOf(focusStr);
					}
				}
				if (focus != null) {
					siteParam = siteParam.substring(0, focusStartIndex);
				}
				if (siteParam.matches("\\d+")) {
					Long siteId = Long.valueOf(siteParam);
					sitesIds.add(siteId);
					if (focus != null) {
						focusesForMapSites.put(siteId, focus);
					}
				}

			}
		}
		mapSites = sitesService.loadByIds(sitesIds);
		if (focusesForMapSites.isEmpty()) {
			focusesForMapSites = null;
		}

	}

}
