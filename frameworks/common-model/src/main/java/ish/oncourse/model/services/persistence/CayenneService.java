package ish.oncourse.model.services.persistence;

import ish.math.MoneyType;
import ish.oncourse.model.access.ISHDataContext;
import ish.oncourse.model.access.ISHDataContextFactory;
import ish.oncourse.model.services.cache.ICacheService;
import ish.oncourse.model.services.lifecycle.QueueableLifecycleListener;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.conf.Configuration;
import org.apache.cayenne.conf.DefaultConfiguration;
import org.apache.cayenne.reflect.LifecycleCallbackRegistry;


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

		LifecycleCallbackRegistry registry = domain.getEntityResolver().getCallbackRegistry();
		registry.addDefaultListener(new QueueableLifecycleListener());

		for(DataNode dataNode: domain.getDataNodes()){
			dataNode.getAdapter().getExtendedTypes().registerType(new MoneyType());
		}

		DataContext sharedDataContext = domain.createDataContext();

		// only use global query cache with a shared context
		sharedDataContext.setQueryCache(cacheService.cayenneCache());
		this.sharedContext = sharedDataContext;
	}

	public ObjectContext newContext() {
		return domain.createDataContext();
	}

	public ObjectContext newNonReplicatingContext() {
		ObjectContext dc = newContext();
		if (dc instanceof ISHDataContext) {
			((ISHDataContext) dc).setRecordQueueingEnabled(false);
		} else {
			throw new IllegalStateException(ISHDataContextFactory.class.getName() + " not installed as DataContext factory");
		}
		return dc;
	}

	public ObjectContext sharedContext() {
		return sharedContext;
	}
}
