package ish.oncourse.services.persistence;

import ish.math.MoneyType;
import ish.oncourse.services.cache.ICacheService;
import ish.oncourse.services.lifecycle.BinaryInfoRelationListener;
import ish.oncourse.services.lifecycle.QueueableLifecycleListener;
import ish.oncourse.services.lifecycle.TaggableListener;
import ish.oncourse.services.site.IWebSiteService;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.services.RegistryShutdownListener;


public class CayenneService implements ICayenneService, RegistryShutdownListener {

	private ServerRuntime cayenneRuntime;
	private ObjectContext sharedContext;
	
	private static final Logger LOGGER = Logger.getLogger(CayenneService.class);
	
	public CayenneService(ICacheService cacheService, IWebSiteService webSiteService) {
		
		LOGGER.info("Starting CayenneService....");
		
		try {
			this.cayenneRuntime = new ServerRuntime("cayenne-oncourse.xml", new ISHModule());
		} catch (Exception e) {
			throw new RuntimeException("Error loading Cayenne stack", e);
		}
		
		DataContext context = (DataContext) cayenneRuntime.getContext();
		context.setQueryCache(cacheService.cayenneCache());
		this.sharedContext = context;
		
		cayenneRuntime.getChannel().getEntityResolver().getCallbackRegistry().addDefaultListener(new QueueableLifecycleListener(this));
		cayenneRuntime.getChannel().getEntityResolver().getCallbackRegistry().addListener(new BinaryInfoRelationListener(webSiteService));
		cayenneRuntime.getChannel().getEntityResolver().getCallbackRegistry().addListener(new TaggableListener(webSiteService));
		
		for(DataNode dataNode: cayenneRuntime.getDataDomain().getDataNodes()){
			dataNode.getAdapter().getExtendedTypes().registerType(new MoneyType());
		}
		
		LOGGER.info("CayenneService starting SUCCESS.");
		
	}
	
	/**
	 * @see ICayenneService#newContext()
	 */
	public ObjectContext newContext() {
		return cayenneRuntime.getContext();
	}

	
	/**
	 * @see ICayenneService#newNonReplicatingContext()
	 */
	public ObjectContext newNonReplicatingContext() {
		ObjectContext dc = newContext();
		if (dc instanceof ISHObjectContext) {
			((ISHObjectContext) dc).setRecordQueueingEnabled(false);
		} else {
			throw new IllegalStateException(ISHObjectContextFactory.class.getName() + " not installed as DataContext factory");
		}
		return dc;
	}

	/**
	 * @see ICayenneService#sharedContext()
	 */
	public ObjectContext sharedContext() {
		return sharedContext;
	}
	
	
	/**
	 * Tapestry IOC container was shutted down, we need to clean up
	 * cayenne stack to prevent leaking.
	 */
	@Override
	public void registryDidShutdown() {
		
		if (cayenneRuntime != null) {
			LOGGER.info("Shutting down CayenneService...");
			cayenneRuntime.shutdown();
			cayenneRuntime = null;
			LOGGER.info("CayenneService shutting down SUCESS.");			
		}
		
	}
}
