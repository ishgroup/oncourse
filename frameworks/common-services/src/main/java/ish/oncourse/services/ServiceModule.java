package ish.oncourse.services;

import ish.oncourse.services.alias.IWebUrlAliasService;
import ish.oncourse.services.alias.WebUrlAliasService;
import ish.oncourse.services.assetgroup.AssetGroupService;
import ish.oncourse.services.assetgroup.IAssetGroupService;
import ish.oncourse.services.binary.BinaryDataService;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.services.content.WebContentService;
import ish.oncourse.services.cookies.CookiesService;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.course.CourseService;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.courseclass.CourseClassService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.environment.EnvironmentService;
import ish.oncourse.services.environment.IEnvironmentService;
import ish.oncourse.services.format.FormatService;
import ish.oncourse.services.format.IFormatService;
import ish.oncourse.services.location.IPostCodeDbService;
import ish.oncourse.services.location.PostCodeDbService;
import ish.oncourse.services.menu.IWebMenuService;
import ish.oncourse.services.menu.WebMenuService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.services.node.WebNodeService;
import ish.oncourse.services.node.WebNodeTypeService;
import ish.oncourse.services.preference.IPreferenceService;
import ish.oncourse.services.preference.PreferenceService;
import ish.oncourse.services.property.IPropertyService;
import ish.oncourse.services.property.PropertyService;
import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.services.resource.ResourceService;
import ish.oncourse.services.room.IRoomService;
import ish.oncourse.services.room.RoomService;
import ish.oncourse.services.search.ISearchService;
import ish.oncourse.services.search.SearchService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.WebSiteService;
import ish.oncourse.services.sites.ISitesService;
import ish.oncourse.services.sites.SitesService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.tag.TagService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.services.textile.TextileConverter;
import ish.oncourse.services.tutor.ITutorService;
import ish.oncourse.services.tutor.TutorService;
import ish.oncourse.services.ui.ISelectModelService;
import ish.oncourse.services.ui.SelectModelService;
import ish.oncourse.util.ComponentPageResponseRenderer;
import ish.oncourse.util.IComponentPageResponseRenderer;
import ish.oncourse.util.IPageRenderer;
import ish.oncourse.util.PageRenderer;

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
		binder.bind(IComponentPageResponseRenderer.class,
				ComponentPageResponseRenderer.class);
		binder.bind(IWebSiteService.class, WebSiteService.class);
		binder.bind(IWebNodeService.class, WebNodeService.class);
		binder.bind(IFormatService.class, FormatService.class);
		binder.bind(ITextileConverter.class, TextileConverter.class);
		binder.bind(IBinaryDataService.class, BinaryDataService.class);
		binder.bind(ICourseService.class, CourseService.class);
		binder.bind(ICourseClassService.class, CourseClassService.class);
		binder.bind(ITutorService.class, TutorService.class);
		binder.bind(ISitesService.class, SitesService.class);
		binder.bind(IRoomService.class, RoomService.class);
		binder.bind(ISearchService.class, SearchService.class);
		binder.bind(IPageRenderer.class, PageRenderer.class);
		binder.bind(ITagService.class, TagService.class);
		binder.bind(ICookiesService.class, CookiesService.class);
		binder.bind(IWebMenuService.class, WebMenuService.class);
		binder.bind(IWebContentService.class, WebContentService.class);
		binder.bind(IWebUrlAliasService.class, WebUrlAliasService.class);
		binder.bind(ISelectModelService.class, SelectModelService.class);
		binder.bind(IPreferenceService.class, PreferenceService.class);
		binder.bind(IPostCodeDbService.class, PostCodeDbService.class);
		binder.bind(IWebNodeTypeService.class, WebNodeTypeService.class);
	}

}
