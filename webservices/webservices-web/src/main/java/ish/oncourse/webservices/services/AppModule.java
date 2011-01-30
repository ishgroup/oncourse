/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ish.oncourse.webservices.services;

import ish.oncourse.model.services.ModelModule;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.webservices.services.builders.IStubBuilder;
import ish.oncourse.webservices.services.builders.StubBuilderComposite;
import ish.oncourse.webservices.services.reference.IReferenceServiceComposite;
import ish.oncourse.webservices.services.reference.ReferenceServiceComposite;

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
		binder.bind(IReferenceServiceComposite.class, ReferenceServiceComposite.class);
		binder.bind(IStubBuilder.class, StubBuilderComposite.class);
	}
	
}
