package ish.oncourse.webservices.soap.v4;

import static org.mockito.Mockito.mock;
import ish.oncourse.model.College;
import ish.oncourse.services.cache.CacheGroup;
import ish.oncourse.services.cache.CachedObjectProvider;
import ish.oncourse.services.cache.ICacheService;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.services.ICollegeRequestService;

import java.io.IOException;

import org.apache.cayenne.cache.QueryCache;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.RequestHandler;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.Session;

public class ReplicationTestModule {

	public RequestFilter buildLogFilterOverride(org.slf4j.Logger log, RequestGlobals requestGlobals) {
		return new RequestFilter() {
			public boolean service(Request request, Response response, RequestHandler handler) throws IOException {
				return handler.service(request, response);
			}
		};
	}

	public void contributeRequestHandler(OrderedConfiguration<RequestFilter> configuration, @Local RequestFilter logFilter) {
		configuration.override("LogFilter", logFilter);
	}

	public ICollegeRequestService buildCollegeRequestServiceOverride(ICollegeService collegeService) {
		final College college = collegeService.findBySecurityCode("345ttn44$%9");

		ICollegeRequestService service = new ICollegeRequestService() {
			
			private Session session;
			
			@Override
			public College getRequestingCollege() {
				return college;
			}

			@Override
			public Session getCollegeSession(boolean create) {
				if (create) {
					session = mock(Session.class);
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
	
	public ICacheService buildCacheServiceServiceOverride() {
		return new ICacheService() {

			@Override
			public QueryCache cayenneCache() {
				return null;
			}

			@Override
			public <T> T get(String key, CachedObjectProvider<T> objectProvider, CacheGroup... cacheGroups) {
				return null;
			}
		};
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local ICacheService cacheService) {
		configuration.add(ICacheService.class, cacheService);
	}
}
