package ish.oncourse.webservices.soap.v15;

import ish.oncourse.webservices.soap.CommonTestModule;
import org.apache.tapestry5.ioc.ServiceBinder;

/**
 * Own services module real-services setup.
 * @author vdavidovich
 *
 */
public class PaymentServiceTestModule {
	public static void bind(ServiceBinder binder) {
		CommonTestModule.bind(binder);
	}
}
