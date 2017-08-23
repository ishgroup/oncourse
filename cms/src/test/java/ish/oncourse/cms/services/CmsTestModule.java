/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cms.services;

import ish.oncourse.cms.services.access.AuthenticationService;
import ish.oncourse.cms.services.access.IAuthenticationService;
import ish.oncourse.cms.services.site.CMSWebSiteVersionService;
import ish.oncourse.model.College;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.cookies.CookiesImplOverride;
import ish.oncourse.services.cookies.CookiesService;
import ish.oncourse.services.cookies.ICookiesOverride;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.courseclass.CourseClassService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.discount.DiscountService;
import ish.oncourse.services.discount.IDiscountService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.services.system.CollegeService;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.services.voucher.IVoucherService;
import ish.oncourse.services.voucher.VoucherService;
import net.sf.ehcache.CacheManager;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;
import java.util.TimeZone;

public class CmsTestModule {

	public static void bind(ServiceBinder binder) {

		binder.bind(CacheManager.class, new ServiceModule.CacheManagerBuilder()).eagerLoad();
		binder.bind(ICayenneService.class, new ServiceModule.CayenneServiceBuilder()).eagerLoad();

		binder.bind(PreferenceController.class);

		binder.bind(ICollegeService.class, CollegeService.class);
		binder.bind(IWebSiteService.class, WebSiteServiceOverride.class);
		binder.bind(IWebSiteVersionService.class, CMSWebSiteVersionService.class);

		binder.bind(IAuthenticationService.class, AuthenticationService.class);

		binder.bind(ICookiesService.class, CookiesService.class);
		binder.bind(ICookiesOverride.class, CookiesImplOverride.class);

		binder.bind(ICourseClassService.class, CourseClassService.class);
		binder.bind(IVoucherService.class, VoucherService.class);
		binder.bind(IDiscountService.class, DiscountService.class);
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
		public TimeZone getTimezone() {
			return null;
		}

		@Override
		public List<WebSite> getSiteTemplates() {
			throw new UnsupportedOperationException();
		}
	}
}
