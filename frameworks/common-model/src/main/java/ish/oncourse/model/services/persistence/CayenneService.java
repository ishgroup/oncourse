package ish.oncourse.model.services.persistence;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.conf.Configuration;
import org.apache.cayenne.conf.DefaultConfiguration;

import ish.oncourse.model.services.cache.ICacheService;

public class CayenneService implements ICayenneService {

	private DataDomain domain;
	private ObjectContext sharedContext;

	public CayenneService(ICacheService cacheService) {
		// using non-static configuration
		Configuration cayenneConfiguration = new DefaultConfiguration();
		try {
			cayenneConfiguration.initialize();
		} catch (Exception e) {
			throw new RuntimeException("Error loading Cayenne stack", e);
		}

		domain = cayenneConfiguration.getDomain();
		DataContext sharedDataContext = domain.createDataContext();

		// only use global query cache with a shared context
		sharedDataContext.setQueryCache(cacheService.cayenneCache());
		this.sharedContext = sharedDataContext;
	}

	public ObjectContext newContext() {
		return domain.createDataContext();
	}

	public ObjectContext sharedContext() {
		return sharedContext;
	}
}
