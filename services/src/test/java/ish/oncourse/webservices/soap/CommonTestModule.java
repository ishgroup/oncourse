/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.soap;

import ish.oncourse.services.BinderFunctions;
import ish.oncourse.services.alias.IWebUrlAliasService;
import ish.oncourse.services.alias.WebUrlAliasService;
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
import ish.oncourse.services.discount.DiscountService;
import ish.oncourse.services.discount.IDiscountService;
import ish.oncourse.services.enrol.EnrolmentServiceImpl;
import ish.oncourse.services.enrol.IEnrolmentService;
import ish.oncourse.services.environment.EnvironmentService;
import ish.oncourse.services.environment.IEnvironmentService;
import ish.oncourse.services.filestorage.FileStorageAssetService;
import ish.oncourse.services.filestorage.IFileStorageAssetService;
import ish.oncourse.services.format.FormatService;
import ish.oncourse.services.format.IFormatService;
import ish.oncourse.services.html.IPlainTextExtractor;
import ish.oncourse.services.html.JerichoPlainTextExtractor;
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
import ish.oncourse.services.paymentexpress.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.preference.PreferenceControllerFactory;
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
import ish.oncourse.services.site.WebSiteServiceOverride;
import ish.oncourse.services.sites.ISitesService;
import ish.oncourse.services.sites.SitesService;
import ish.oncourse.services.system.CollegeService;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.tag.TagService;
import ish.oncourse.services.IRichtextConverter;
import ish.oncourse.services.RichtextConverter;
import ish.oncourse.services.tutor.ITutorService;
import ish.oncourse.services.tutor.TutorService;
import ish.oncourse.util.ComponentPageResponseRenderer;
import ish.oncourse.util.IComponentPageResponseRenderer;
import ish.oncourse.util.IPageRenderer;
import ish.oncourse.util.PageRenderer;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.replication.builders.ITransactionStubBuilder;
import ish.oncourse.webservices.replication.builders.IWillowStubBuilder;
import ish.oncourse.webservices.replication.builders.TransactionStubBuilderImpl;
import ish.oncourse.webservices.replication.builders.WillowStubBuilderImpl;
import ish.oncourse.webservices.replication.services.InternalPaymentService;
import ish.oncourse.webservices.replication.services.PaymentServiceImpl;
import ish.oncourse.webservices.replication.services.TransactionGroupProcessorImpl;
import ish.oncourse.webservices.replication.updaters.IWillowUpdater;
import ish.oncourse.webservices.replication.updaters.WillowUpdaterImpl;
import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.ServiceBuilder;
import org.apache.tapestry5.ioc.ServiceResources;

/**
 * User: akoiro
 * Date: 23/8/17
 */
public class CommonTestModule {
	public static void bind(ServiceBinder binder) {

		binder.bind(IComponentPageResponseRenderer.class, ComponentPageResponseRenderer.class);
		binder.bind(ICookiesService.class, CookiesService.class);
		binder.bind(IEnvironmentService.class, EnvironmentService.class);
		binder.bind(IFormatService.class, FormatService.class);
		binder.bind(IPageRenderer.class, PageRenderer.class);
		binder.bind(IPropertyService.class, PropertyService.class);
		binder.bind(IResourceService.class, ResourceService.class);
		binder.bind(ISearchService.class, SearchService.class);
		binder.bind(ITagService.class, TagService.class);
		binder.bind(IRichtextConverter.class, RichtextConverter.class);

		// Data specific serivces
		binder.bind(IBinaryDataService.class, BinaryDataService.class);
		binder.bind(ICollegeService.class, CollegeService.class);
		binder.bind(ICourseClassService.class, CourseClassService.class);
		binder.bind(ICourseService.class, CourseService.class);
		binder.bind(IPostCodeDbService.class, PostCodeDbService.class);

		binder.bind(PreferenceController.class);
		binder.bind(PreferenceControllerFactory.class);

		binder.bind(IRoomService.class, RoomService.class);
		binder.bind(ISitesService.class, SitesService.class);
		binder.bind(ITutorService.class, TutorService.class);
		binder.bind(IWebContentService.class, WebContentService.class);
		binder.bind(IWebMenuService.class, WebMenuService.class);
		binder.bind(IWebNodeService.class, WebNodeService.class);

		binder.bind(IWebSiteService.class, WebSiteServiceOverride.class).withId(WebSiteServiceOverride.class.getSimpleName());

		binder.bind(IWebUrlAliasService.class, WebUrlAliasService.class);
		binder.bind(IWebNodeTypeService.class, WebNodeTypeService.class);
		binder.bind(IDiscountService.class, DiscountService.class);
		binder.bind(IPaymentService.class, PaymentService.class);
		binder.bind(IMessagePersonService.class, MessagePersonService.class);
		binder.bind(IPlainTextExtractor.class, JerichoPlainTextExtractor.class);

		// Reference Data services
		binder.bind(ICountryService.class, CountryService.class).withId("CountryService");
		binder.bind(ILanguageService.class, LanguageService.class).withId("LanguageService");
		binder.bind(IModuleService.class, ModuleService.class).withId("ModuleService");
		binder.bind(IQualificationService.class, QualificationService.class).withId("QualificationService");
		binder.bind(ITrainingPackageService.class, TrainingPackageService.class).withId("TrainingPackageService");

		binder.bind(IPaymentGatewayServiceBuilder.class, PaymentGatewayServiceBuilder.class);
		//this part added for test with manual services invoke
		binder.bind(IFileStorageAssetService.class, FileStorageAssetService.class);
		binder.bind(IWillowUpdater.class, WillowUpdaterImpl.class);
		binder.bind(IWillowStubBuilder.class, WillowStubBuilderImpl.class);
		binder.bind(ITransactionStubBuilder.class, TransactionStubBuilderImpl.class);
		binder.bind(IEnrolmentService.class, EnrolmentServiceImpl.class);
		binder.bind(ITransactionGroupProcessor.class, new ServiceBuilder<ITransactionGroupProcessor>() {
			@Override
			public ITransactionGroupProcessor buildService(ServiceResources res) {
				return new TransactionGroupProcessorImpl(res.getService(ICayenneService.class), res.getService("WebSiteServiceOverride",
						IWebSiteService.class), res.getService(IWillowUpdater.class), res.getService(IFileStorageAssetService.class));
			}
		}).scope(ScopeConstants.PERTHREAD);
		binder.bind(InternalPaymentService.class, PaymentServiceImpl.class);
		binder.bind(INewPaymentGatewayServiceBuilder.class, NewPaymentGatewayServiceBuilder.class);
		binder.bind(javax.cache.CacheManager.class, new BinderFunctions.CacheManagerBuilder()).eagerLoad();
		binder.bind(ICayenneService.class, new BinderFunctions.CayenneServiceBuilder()).eagerLoad();
		binder.bind(INewPaymentGatewayService.class, new BinderFunctions.PaymentGatewayBuilder());
	}
}
