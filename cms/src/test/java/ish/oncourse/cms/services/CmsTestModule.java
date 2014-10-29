/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cms.services;

import ish.oncourse.cms.services.access.AuthenticationService;
import ish.oncourse.cms.services.access.IAuthenticationService;
import ish.oncourse.cms.services.site.CMSWebSiteVersionService;
import ish.oncourse.model.College;
import ish.oncourse.model.WebHostName;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.services.ModelModule;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.services.system.ICollegeService;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.SubModule;

@SubModule({ ModelModule.class, ServiceModule.class })
public class CmsTestModule {

	public static void bind(ServiceBinder binder) {
		binder.bind(IAuthenticationService.class, AuthenticationService.class);

		binder.bind(IWebSiteService.class, WebSiteServiceOverride.class).withId("testWebSiteService");
		binder.bind(IWebSiteVersionService.class, CMSWebSiteVersionService.class);
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local IWebSiteService webSiteService) {
		configuration.add(IWebSiteService.class, webSiteService);
	}

	public static class WebSiteServiceOverride implements IWebSiteService {

		@Inject
		private ICollegeService collegeService;

		@Override
		public WebSite getCurrentWebSite() {
			return getCurrentCollege().getWebSites().get(0);
		}

		@Override
		public College getCurrentCollege() {
			return collegeService.findBySecurityCode("345ttn44$%9");
		}

		@Override
		public WebHostName getCurrentDomain() {
			return getCurrentWebSite().getToWebHostName();
		}
	}
}
