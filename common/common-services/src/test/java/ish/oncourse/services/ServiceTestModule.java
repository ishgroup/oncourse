package ish.oncourse.services;

import ish.oncourse.model.College;
import ish.oncourse.model.WebHostName;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.alias.IWebUrlAliasService;
import ish.oncourse.services.alias.WebUrlAliasService;
import ish.oncourse.services.assetgroup.AssetGroupService;
import ish.oncourse.services.assetgroup.IAssetGroupService;
import ish.oncourse.services.binary.BinaryDataService;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.cache.*;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.services.content.WebContentService;
import ish.oncourse.services.cookies.CookiesImplOverride;
import ish.oncourse.services.cookies.CookiesService;
import ish.oncourse.services.cookies.ICookiesOverride;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.course.CourseService;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.courseclass.CourseClassService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.discount.DiscountService;
import ish.oncourse.services.discount.IDiscountService;
import ish.oncourse.services.environment.EnvironmentService;
import ish.oncourse.services.environment.IEnvironmentService;
import ish.oncourse.services.filestorage.FileStorageAssetService;
import ish.oncourse.services.filestorage.IFileStorageAssetService;
import ish.oncourse.services.format.FormatService;
import ish.oncourse.services.format.IFormatService;
import ish.oncourse.services.html.IPlainTextExtractor;
import ish.oncourse.services.html.JerichoPlainTextExtractor;
import ish.oncourse.services.jndi.ILookupService;
import ish.oncourse.services.jndi.LookupService;
import ish.oncourse.services.location.IPostCodeDbService;
import ish.oncourse.services.location.PostCodeDbService;
import ish.oncourse.services.menu.IWebMenuService;
import ish.oncourse.services.menu.WebMenuService;
import ish.oncourse.services.message.IMessagePersonService;
import ish.oncourse.services.message.MessagePersonService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.services.node.WebNodeService;
import ish.oncourse.services.node.WebNodeTypeService;
import ish.oncourse.services.payment.IPaymentService;
import ish.oncourse.services.payment.PaymentService;
import ish.oncourse.services.paymentexpress.IPaymentGatewayService;
import ish.oncourse.services.paymentexpress.PaymentExpressGatewayService;
import ish.oncourse.services.persistence.CayenneService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.property.IPropertyService;
import ish.oncourse.services.property.PropertyService;
import ish.oncourse.services.reference.*;
import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.services.resource.ResourceService;
import ish.oncourse.services.room.IRoomService;
import ish.oncourse.services.room.RoomService;
import ish.oncourse.services.search.ISearchService;
import ish.oncourse.services.search.SearchService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.services.site.WebSiteVersionService;
import ish.oncourse.services.sites.ISitesService;
import ish.oncourse.services.sites.SitesService;
import ish.oncourse.services.system.CollegeService;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.tag.TagService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.services.textile.TextileConverter;
import ish.oncourse.services.tutor.ITutorService;
import ish.oncourse.services.tutor.TutorService;
import ish.oncourse.test.TestContextUtil;
import ish.oncourse.util.ComponentPageResponseRenderer;
import ish.oncourse.util.IComponentPageResponseRenderer;
import ish.oncourse.util.IPageRenderer;
import ish.oncourse.util.PageRenderer;
import ish.persistence.CommonPreferenceController;
import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.ServiceBuilder;
import org.apache.tapestry5.ioc.ServiceResources;
import org.apache.tapestry5.ioc.annotations.EagerLoad;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;

public class ServiceTestModule {
	
	public static void bind(ServiceBinder binder) {

		// Tapestry and environment specific services
		if (TestContextUtil.isQueryCacheEnabled()) {
			binder.bind(ICacheService.class, OSCacheService.class);
		} else {
			binder.bind(ICacheService.class, NoopCacheService.class);
		}
		binder.bind(IAssetGroupService.class, AssetGroupService.class);
		binder.bind(IComponentPageResponseRenderer.class, ComponentPageResponseRenderer.class);
		binder.bind(ICookiesService.class, CookiesService.class);
		binder.bind(ICookiesOverride.class, CookiesImplOverride.class);
		binder.bind(IEnvironmentService.class, EnvironmentService.class);
		binder.bind(IFormatService.class, FormatService.class);
		binder.bind(IPageRenderer.class, PageRenderer.class);
		binder.bind(IPropertyService.class, PropertyService.class);
		binder.bind(IResourceService.class, ResourceService.class);
		binder.bind(ISearchService.class, SearchService.class);
		binder.bind(ITagService.class, TagService.class);
		binder.bind(ITextileConverter.class, TextileConverter.class);

		// Data specific serivces
		binder.bind(IBinaryDataService.class, BinaryDataService.class);
		binder.bind(ICollegeService.class, CollegeService.class);
		binder.bind(ICourseClassService.class, CourseClassService.class);
		binder.bind(ICourseService.class, CourseService.class);
		binder.bind(IPostCodeDbService.class, PostCodeDbService.class);
		
		binder.bind(CommonPreferenceController.class, PreferenceController.class).scope(ScopeConstants.PERTHREAD);
		
		binder.bind(IRoomService.class, RoomService.class);
		binder.bind(ISitesService.class, SitesService.class);
		binder.bind(ITutorService.class, TutorService.class);
		binder.bind(IWebContentService.class, WebContentService.class);
		binder.bind(IWebMenuService.class, WebMenuService.class);
		binder.bind(IWebNodeService.class, WebNodeService.class);
		
		binder.bind(IWebSiteVersionService.class, WebSiteVersionService.class);
		binder.bind(IWebSiteService.class, new ServiceBuilder<IWebSiteService>() {

			@Override
			public IWebSiteService buildService(final ServiceResources res) {
				
				IWebSiteService webSiteService = new IWebSiteService() {

					@Override
					public WebSite getCurrentWebSite() {
						return getCurrentCollege().getWebSites().get(0);
					}

					@Override
					public College getCurrentCollege() {
						ICollegeService collegeService = res.getService(ICollegeService.class);
						return collegeService.findBySecurityCode("345ttn44$%9");
					}

					@Override
					public WebHostName getCurrentDomain() {
						return getCurrentWebSite().getToWebHostName();
					}
				};

				return webSiteService;
			}
			
		});
		
		binder.bind(IWebUrlAliasService.class, WebUrlAliasService.class);
		binder.bind(IWebNodeTypeService.class, WebNodeTypeService.class);
		binder.bind(IDiscountService.class, DiscountService.class);
		binder.bind(ILookupService.class, LookupService.class);
		binder.bind(IPaymentService.class, PaymentService.class);
        binder.bind(IMessagePersonService.class, MessagePersonService.class);
        binder.bind(IPlainTextExtractor.class, JerichoPlainTextExtractor.class);

		// Reference Data services
		binder.bind(ICountryService.class, CountryService.class).withId("CountryService");
		binder.bind(ILanguageService.class, LanguageService.class).withId("LanguageService");
		binder.bind(IModuleService.class, ModuleService.class).withId("ModuleService");
		binder.bind(IQualificationService.class, QualificationService.class).withId("QualificationService");
		binder.bind(ITrainingPackageService.class, TrainingPackageService.class).withId("TrainingPackageService");
		binder.bind(IPaymentGatewayService.class, PaymentExpressGatewayService.class).withId("PaymentGatewayService");

        binder.bind(IFileStorageAssetService.class, FileStorageAssetService.class);
        binder.bind(IRequestCacheService.class, RequestCacheService.class);
	}
	
	@EagerLoad
	public static ICayenneService buildCayenneService(ICacheService cacheService, RegistryShutdownHub hub, IWebSiteService webSiteService) {
		CayenneService cayenneService = new CayenneService(cacheService, webSiteService);
		hub.addRegistryShutdownListener(cayenneService);
		return cayenneService;
	}
}
