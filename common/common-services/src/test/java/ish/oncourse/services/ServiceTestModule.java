package ish.oncourse.services;

import ish.oncourse.test.tapestry.TestModule;
import org.apache.tapestry5.ioc.ServiceBinder;

public class ServiceTestModule {
	public static void bind(ServiceBinder binder) {
		TestModule.bind(binder);
	}
}
