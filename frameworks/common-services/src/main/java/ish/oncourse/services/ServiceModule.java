package ish.oncourse.services;

import ish.oncourse.services.assetgroup.AssetGroupService;
import ish.oncourse.services.assetgroup.IAssetGroupService;
import ish.oncourse.services.binary.BinaryDataService;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.block.IWebBlockService;
import ish.oncourse.services.block.WebBlockService;
import ish.oncourse.services.course.CourseService;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.environment.EnvironmentService;
import ish.oncourse.services.environment.IEnvironmentService;
import ish.oncourse.services.format.FormatService;
import ish.oncourse.services.format.IFormatService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.node.WebNodeService;
import ish.oncourse.services.property.IPropertyService;
import ish.oncourse.services.property.PropertyService;
import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.services.resource.ResourceService;
import ish.oncourse.services.search.ISearchService;
import ish.oncourse.services.search.SearchService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.WebSiteService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.services.textile.TextileConverter;
import ish.oncourse.services.tutor.ITutorService;
import ish.oncourse.services.tutor.TutorService;
import ish.oncourse.util.ComponentPageResponseRenderer;
import ish.oncourse.util.IPageResponseRenderer;

import org.apache.tapestry5.ioc.ServiceBinder;

/**
 * A Tapestry IoC module definition for all common services.
 */
public class ServiceModule {

	public static void bind(ServiceBinder binder) {
		binder.bind(IAssetGroupService.class, AssetGroupService.class);
		binder.bind(IEnvironmentService.class, EnvironmentService.class);
		binder.bind(IPropertyService.class, PropertyService.class);
		binder.bind(IResourceService.class, ResourceService.class);
		binder.bind(IPageResponseRenderer.class, ComponentPageResponseRenderer.class);
		binder.bind(IWebSiteService.class, WebSiteService.class);
		binder.bind(IWebNodeService.class, WebNodeService.class);
		binder.bind(IWebBlockService.class, WebBlockService.class);
		binder.bind(IFormatService.class, FormatService.class);
		binder.bind(ITextileConverter.class, TextileConverter.class);
		binder.bind(IBinaryDataService.class, BinaryDataService.class);
		binder.bind(ICourseService.class, CourseService.class);
		binder.bind(ITutorService.class, TutorService.class);
		binder.bind(ISearchService.class, SearchService.class);
	}

}
