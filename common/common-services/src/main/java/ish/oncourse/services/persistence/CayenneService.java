package ish.oncourse.services.persistence;

import ish.math.MoneyType;
import ish.oncourse.listeners.IshVersionListener;
import ish.oncourse.services.lifecycle.*;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.cayenne.DataChannel;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.lifecycle.changeset.ChangeSetFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.services.RegistryShutdownListener;


public class CayenneService implements ICayenneService, RegistryShutdownListener {

	private ServerRuntime cayenneRuntime;
	private ObjectContext sharedContext;
	
	private static final Logger logger = LogManager.getLogger();
	
	public CayenneService(IWebSiteService webSiteService) {
		
		logger.info("Starting CayenneService....");
		
		try {
			this.cayenneRuntime = new ServerRuntime("cayenne-oncourse.xml", new ISHModule());
			this.cayenneRuntime.getDataDomain().addFilter(new ChangeSetFilter());
		} catch (Exception e) {
			throw new RuntimeException("Error loading Cayenne stack", e);
		}
		
		this.sharedContext = cayenneRuntime.newContext();
		
		QueueableLifecycleListener listener = new QueueableLifecycleListener(this);
		cayenneRuntime.getDataDomain().addFilter(listener);
		cayenneRuntime.getChannel().getEntityResolver().getCallbackRegistry().addDefaultListener(listener);
		cayenneRuntime.getChannel().getEntityResolver().getCallbackRegistry().addListener(new IshVersionListener());
		cayenneRuntime.getChannel().getEntityResolver().getCallbackRegistry().addListener(new BinaryInfoRelationListener(webSiteService));
		cayenneRuntime.getChannel().getEntityResolver().getCallbackRegistry().addListener(new TaggableListener());
		cayenneRuntime.getChannel().getEntityResolver().getCallbackRegistry().addListener(new QueuedTransactionListener(this));
		cayenneRuntime.getChannel().getEntityResolver().getCallbackRegistry().addListener(new InvoiceListener());

		for(DataNode dataNode: cayenneRuntime.getDataDomain().getDataNodes()){
			dataNode.getAdapter().getExtendedTypes().registerType(new MoneyType());
		}
		
		logger.info("CayenneService starting SUCCESS.");
		
	}
	
	/**
	 * @see ICayenneService#newContext()
	 */
	public ObjectContext newContext() {
		return cayenneRuntime.newContext();
	}


	public ObjectContext newContext(DataChannel parentChannel) {
		return cayenneRuntime.newContext(parentChannel);
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
			logger.info("Shutting down CayenneService...");
			cayenneRuntime.shutdown();
			cayenneRuntime = null;
			logger.info("CayenneService shutting down SUCCESS.");			
		}
		
	}
}
