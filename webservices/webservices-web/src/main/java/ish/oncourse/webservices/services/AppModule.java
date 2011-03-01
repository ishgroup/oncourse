/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ish.oncourse.webservices.services;

import ish.oncourse.model.services.ModelModule;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.webservices.services.reference.ReferenceService;
import ish.oncourse.webservices.services.reference.ReferenceStubBuilder;
import ish.oncourse.webservices.services.replication.WillowQueueService;
import ish.oncourse.webservices.services.replication.WillowStubBuilderFactory;
import ish.oncourse.webservices.services.replication.WillowUpdaterFactory;

import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.SubModule;


/**
 *
 * @author marek
 */
@SubModule({ModelModule.class, ServiceModule.class})
public class AppModule {

	private static final Logger LOGGER = Logger.getLogger(AppModule.class);

	public static void bind(ServiceBinder binder) {
		LOGGER.info("Registering Willow WebServices");
		binder.bind(ReferenceService.class);
		binder.bind(ReferenceStubBuilder.class);
		binder.bind(WillowStubBuilderFactory.class);
		binder.bind(WillowQueueService.class);
		binder.bind(WillowUpdaterFactory.class);
	}
	
}
