package ish.oncourse.model.services;

import ish.oncourse.model.services.cache.ICacheService;
import ish.oncourse.model.services.cache.OSCacheService;
import ish.oncourse.model.services.persistence.CayenneService;
import ish.oncourse.model.services.persistence.ICayenneService;

import org.apache.tapestry5.ioc.ServiceBinder;

/**
 * A Tapestry IoC module definition for all common services.
 */
public class ModelModule {

	public static void bind(ServiceBinder binder) {
		binder.bind(ICacheService.class, OSCacheService.class);
		binder.bind(ICayenneService.class, CayenneService.class);
	}

}
