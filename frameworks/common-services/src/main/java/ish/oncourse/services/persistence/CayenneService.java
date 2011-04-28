package ish.oncourse.services.persistence;

import ish.math.MoneyType;
import ish.oncourse.services.cache.ICacheService;
import ish.oncourse.services.lifecycle.QueueableLifecycleListener;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.configuration.server.ServerRuntime;


public class CayenneService implements ICayenneService {

	private ServerRuntime cayenneRuntime;
	private ObjectContext sharedContext;
	
	public CayenneService(ICacheService cacheService) {
			
		try {
			this.cayenneRuntime = new ServerRuntime("cayenne-oncourse.xml", new ISHModule());
		} catch (Exception e) {
			throw new RuntimeException("Error loading Cayenne stack", e);
		}
		
		DataContext context = (DataContext) cayenneRuntime.getContext();
		context.setQueryCache(cacheService.cayenneCache());
		this.sharedContext = context;
		
		cayenneRuntime.getChannel().getEntityResolver().getCallbackRegistry().addDefaultListener(new QueueableLifecycleListener(this));
		
		for(DataNode dataNode: cayenneRuntime.getDataDomain().getDataNodes()){
			dataNode.getAdapter().getExtendedTypes().registerType(new MoneyType());
		}
		
	}

	public ObjectContext newContext() {
		return cayenneRuntime.getContext();
	}

	public ObjectContext newNonReplicatingContext() {
		ObjectContext dc = newContext();
		if (dc instanceof ISHObjectContext) {
			((ISHObjectContext) dc).setRecordQueueingEnabled(false);
		} else {
			throw new IllegalStateException(ISHObjectContextFactory.class.getName() + " not installed as DataContext factory");
		}
		return dc;
	}

	public ObjectContext sharedContext() {
		return sharedContext;
	}
}
