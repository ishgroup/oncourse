package ish.oncourse.webservices.soap.v4;

import ish.oncourse.model.College;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.services.ICollegeRequestService;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.Local;

public class ReplicationTestModule {

	public ICollegeRequestService buildCollegeRequestServiceOverride(final ICollegeService collegeService) {

		ICollegeRequestService service = new ICollegeRequestService() {

			@Override
			public College getRequestingCollege() {
				return collegeService.findBySecurityCode("345ttn44$%9");
			}
		};

		return service;
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local ICollegeRequestService creq) {
		configuration.add(ICollegeRequestService.class, creq);
	}
}
