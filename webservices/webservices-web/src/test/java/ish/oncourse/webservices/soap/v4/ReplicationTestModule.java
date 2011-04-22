package ish.oncourse.webservices.soap.v4;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import ish.oncourse.model.College;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.services.ICollegeRequestService;
import ish.oncourse.webservices.soap.v4.auth.SessionToken;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.services.Session;

public class ReplicationTestModule {

	public ICollegeRequestService buildCollegeRequestServiceOverride(final ICollegeService collegeService) {

		ICollegeRequestService service = new ICollegeRequestService() {

			private Session session;

			@Override
			public College getRequestingCollege() {
				return collegeService.findBySecurityCode("345ttn44$%9");
			}

			@Override
			public Session getCollegeSession(boolean create) {
				if (create) {
					session = mock(Session.class);

					SessionToken token = new SessionToken() {
						@Override
						public Long getCommunicationKey() {
							final College college = collegeService.findBySecurityCode("345ttn44$%9");
							return college.getCommunicationKey();
						}

						@Override
						public College getCollege() {
							return collegeService.findBySecurityCode("345ttn44$%9");
						}
					};

					when(session.getAttribute(eq(SessionToken.SESSION_TOKEN_KEY))).thenReturn(token);
				}
				return session;
			}
		};

		return service;
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
			@Local ICollegeRequestService collegeRequestService) {
		configuration.add(ICollegeRequestService.class, collegeRequestService);
	}
}
