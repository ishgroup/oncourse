package ish.oncourse.services;

import org.apache.tapestry5.ioc.ServiceBinder;

import ish.oncourse.services.assetgroup.AssetGroupService;
import ish.oncourse.services.assetgroup.IAssetGroupService;
import ish.oncourse.services.cache.ICacheService;
import ish.oncourse.services.cache.OSCacheService;
import ish.oncourse.services.environment.EnvironmentService;
import ish.oncourse.services.environment.IEnvironmentService;
import ish.oncourse.services.format.FormatService;
import ish.oncourse.services.format.IFormatService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.node.WebNodeService;
import ish.oncourse.services.persistence.CayenneService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.property.IPropertyService;
import ish.oncourse.services.property.PropertyService;
import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.services.resource.ResourceService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.WebSiteService;

/**
 * A Tapestry IoC module definition for all common services.
 */
public class ServiceModule {

	public static void bind(ServiceBinder binder) {

		binder.bind(IAssetGroupService.class, AssetGroupService.class);
		binder.bind(ICacheService.class, OSCacheService.class);
		binder.bind(ICayenneService.class, CayenneService.class);
		binder.bind(IEnvironmentService.class, EnvironmentService.class);
		binder.bind(IPropertyService.class, PropertyService.class);
		binder.bind(IResourceService.class, ResourceService.class);
		binder.bind(IWebSiteService.class, WebSiteService.class);
		binder.bind(IWebNodeService.class, WebNodeService.class);
		binder.bind(IFormatService.class, FormatService.class);
	}
}
