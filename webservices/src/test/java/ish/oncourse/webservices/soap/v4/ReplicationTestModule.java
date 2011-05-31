package ish.oncourse.webservices.soap.v4;

import ish.oncourse.model.College;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.services.site.WebSiteServiceOverride;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.Local;

public class ReplicationTestModule {

	public IWebSiteService buildWebSiteServiceTestOverride(final ICollegeService collegeService) {

		WebSiteServiceOverride service = new WebSiteServiceOverride() {

			@Override
			public College getCurrentCollege() {
				return collegeService.findBySecurityCode("345ttn44$%9");
			}
			
		};

		return service;
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local IWebSiteService webSiteService) {
		configuration.override(IWebSiteService.class, webSiteService);
	}
}
