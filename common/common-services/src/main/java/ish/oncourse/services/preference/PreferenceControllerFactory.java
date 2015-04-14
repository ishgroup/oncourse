package ish.oncourse.services.preference;

import ish.oncourse.model.College;
import ish.oncourse.model.WebHostName;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.HashMap;
import java.util.Map;

public class PreferenceControllerFactory {
	
	@Inject
	private ICayenneService cayenneService;
	
	private Map<Long, PreferenceController> controllerMap = new HashMap<>();
	
	public PreferenceController getPreferenceController(final College college) {
		
		PreferenceController pref = controllerMap.get(college.getId());
		
		if (pref == null) {
			pref = new PreferenceController(cayenneService, new IWebSiteService() {

				@Override
				public WebSite getCurrentWebSite() {
					return (college.getWebSites().size() > 0) ? college.getWebSites().get(0) : null;
				}

				@Override
				public College getCurrentCollege() {
					return college;
				}

				@Override
				public WebHostName getCurrentDomain() {
					return (getCurrentWebSite() != null) ? getCurrentWebSite().getToWebHostName() : null;
				}
			});
			
			controllerMap.put(college.getId(), pref);
		}
		
		return pref;
	}

}
