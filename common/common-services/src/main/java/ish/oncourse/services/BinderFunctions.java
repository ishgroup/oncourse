/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services;

import ish.oncourse.services.application.ApplicationServiceImpl;
import ish.oncourse.services.application.IApplicationService;
import ish.oncourse.services.binary.BinaryDataService;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.contact.ContactServiceImpl;
import ish.oncourse.services.contact.IContactService;
import ish.oncourse.services.content.cache.ContentCacheService;
import ish.oncourse.services.content.cache.IContentCacheService;
import ish.oncourse.services.course.CourseService;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.courseclass.CourseClassService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.discount.DiscountService;
import ish.oncourse.services.discount.IDiscountService;
import ish.oncourse.services.encrypt.EncryptionService;
import ish.oncourse.services.enrol.EnrolmentServiceImpl;
import ish.oncourse.services.enrol.IEnrolmentService;
import ish.oncourse.services.environment.EnvironmentService;
import ish.oncourse.services.environment.IEnvironmentService;
import ish.oncourse.services.filestorage.FileStorageAssetService;
import ish.oncourse.services.filestorage.IFileStorageAssetService;
import ish.oncourse.services.html.ICacheMetaProvider;
import ish.oncourse.services.jmx.IJMXInitService;
import ish.oncourse.services.jmx.JMXInitService;
import ish.oncourse.services.location.IPostCodeDbService;
import ish.oncourse.services.location.PostCodeDbService;
import ish.oncourse.services.mail.IMailService;
import ish.oncourse.services.mail.MailService;
import ish.oncourse.services.message.IMessagePersonService;
import ish.oncourse.services.message.MessagePersonService;
import ish.oncourse.services.payment.IPaymentService;
import ish.oncourse.services.payment.PaymentService;
import ish.oncourse.services.paymentexpress.*;
import ish.oncourse.services.persistence.CayenneService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.preference.PreferenceControllerFactory;
import ish.oncourse.services.reference.*;
import ish.oncourse.services.room.IRoomService;
import ish.oncourse.services.room.RoomService;
import ish.oncourse.services.s3.IS3Service;
import ish.oncourse.services.s3.S3Service;
import ish.oncourse.services.search.ISearchService;
import ish.oncourse.services.search.SearchService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.sites.ISitesService;
import ish.oncourse.services.sites.SitesService;
import ish.oncourse.services.sms.DefaultSMSService;
import ish.oncourse.services.sms.ISMSService;
import ish.oncourse.services.sms.TestModeSMSService;
import ish.oncourse.services.system.CollegeService;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.tag.TagService;
import ish.oncourse.services.tutor.ITutorService;
import ish.oncourse.services.tutor.TutorService;
import ish.oncourse.services.visitor.ParsedContentVisitor;
import ish.oncourse.services.voucher.IVoucherService;
import ish.oncourse.services.voucher.VoucherService;
import ish.oncourse.util.IPageRenderer;
import org.apache.cayenne.configuration.CayenneRuntime;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.ServiceBuilder;
import org.apache.tapestry5.ioc.ServiceResources;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.apache.tapestry5.services.ApplicationGlobals;

import javax.cache.CacheManager;
import javax.sql.DataSource;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.tapestry5.ioc.ScopeConstants.PERTHREAD;

/**
 * User: akoiro
 * Date: 29/11/17
 */
public class BinderFunctions {

	public static void bindPaymentGatewayServices(ServiceBinder binder) {
		binder.bind(IPaymentGatewayServiceBuilder.class, ish.oncourse.services.paymentexpress.PaymentGatewayServiceBuilder.class);
		binder.bind(IPaymentGatewayService.class, new PaymentGatewayServiceBuilder()).scope(PERTHREAD);

		binder.bind(INewPaymentGatewayServiceBuilder.class, NewPaymentGatewayServiceBuilder.class);
		binder.bind(INewPaymentGatewayService.class, new PaymentGatewayBuilder()).scope(PERTHREAD);
	}

	public static void bindReferenceServices(ServiceBinder binder) {
		// Reference Data services
		binder.bind(V5ReferenceService.class);
		binder.bind(V6ReferenceService.class);
		binder.bind(ICountryService.class, CountryService.class).withId("CountryService");
		binder.bind(ILanguageService.class, LanguageService.class).withId("LanguageService");
		binder.bind(IModuleService.class, ModuleService.class).withId("ModuleService");
		binder.bind(IQualificationService.class, QualificationService.class).withId("QualificationService");
		binder.bind(ITrainingPackageService.class, TrainingPackageService.class).withId("TrainingPackageService");
		binder.bind(IPostcodeService.class, PostcodeService.class).withId("PostcodeService");
	}

	public static void bindEntityServices(ServiceBinder binder) {
		binder.bind(ITagService.class, TagService.class);
		binder.bind(IBinaryDataService.class, BinaryDataService.class);
		binder.bind(ICollegeService.class, CollegeService.class);
		binder.bind(ICourseClassService.class, CourseClassService.class);
		binder.bind(ICourseService.class, CourseService.class);
		binder.bind(IPostCodeDbService.class, PostCodeDbService.class);
		binder.bind(IContactService.class, ContactServiceImpl.class);
		binder.bind(IRoomService.class, RoomService.class);
		binder.bind(ISitesService.class, SitesService.class);
		binder.bind(ITutorService.class, TutorService.class);
		binder.bind(IEnrolmentService.class, EnrolmentServiceImpl.class);
		binder.bind(IDiscountService.class, DiscountService.class);
		binder.bind(IVoucherService.class, VoucherService.class);
		binder.bind(IApplicationService.class, ApplicationServiceImpl.class);
		binder.bind(IMessagePersonService.class, MessagePersonService.class);
		binder.bind(IPaymentService.class, PaymentService.class);
	}

	public static void bindWebSiteServices(ServiceBinder binder, Class<? extends IWebSiteService> webSiteServiceClass) {
		new BindWebSiteServices().webSiteService(webSiteServiceClass).bind(binder);
	}

	public static void bindEnvServices(ServiceBinder binder, String appName, boolean testMode) {
		binder.bind(ICayenneService.class, new CayenneServiceBuilder()).eagerLoad();
		binder.bind(CacheManager.class, new BinderFunctions.CacheManagerBuilder());

		binder.bind(PreferenceController.class).withId(PreferenceController.class.getSimpleName());
		binder.bind(PreferenceControllerFactory.class).withId(PreferenceControllerFactory.class.getSimpleName());

		binder.bind(IEnvironmentService.class, EnvironmentService.class);

		binder.bind(EncryptionService.class).withId(EnvironmentService.class.getSimpleName());

		binder.bind(IMailService.class, MailService.class);

		binder.bind(IFileStorageAssetService.class, FileStorageAssetService.class);
		binder.bind(IS3Service.class, S3Service.class).scope(PERTHREAD);

		binder.bind(IJMXInitService.class, new JMXInitServiceBuilder(appName));

		binder.bind(ISearchService.class, SearchService.class);

		if (testMode) {
			binder.bind(ISMSService.class, TestModeSMSService.class);
		} else {
			binder.bind(ISMSService.class, DefaultSMSService.class);
		}
	}

	public static void tapestryConfiguration(MappedConfiguration<String, String> configuration, String hmacPassphrase) {
		// ensure Tapestry does not advertise itself on our pages...
		configuration.add(SymbolConstants.OMIT_GENERATOR_META, "true");

		// this is overridden in other palces anyways, as we are using locale
		// variant for site template customization
		//configuration.add(SymbolConstants.SUPPORTED_LOCALES, "en");

		configuration.add(SymbolConstants.HMAC_PASSPHRASE, hmacPassphrase);

		configuration.add(SymbolConstants.COMPACT_JSON, "false");
		configuration.add(SymbolConstants.COMPRESS_WHITESPACE, "false");
		configuration.add(SearchService.ALIAS_SUFFIX_PROPERTY, EMPTY);
		configuration.add(ParsedContentVisitor.WEB_CONTENT_CACHE, "false");
	}



	public static void bindTapestryServices(ServiceBinder binder,
											Class<? extends ICacheMetaProvider> cacheMetaProviderClass,
											Class<? extends  IPageRenderer> pageRendererClass) {

		new BindTapestryServices()
				.cacheMetaProviderClass(cacheMetaProviderClass)
				.pageRendererClass(pageRendererClass)
				.bind(binder);
	}


	public static class CayenneServiceBuilder implements ServiceBuilder<ICayenneService> {
		@Override
		public ICayenneService buildService(ServiceResources resources) {
			RegistryShutdownHub hub = resources.getService(RegistryShutdownHub.class);
			IWebSiteService webSiteService = resources.getService(IWebSiteService.class);
			CayenneService cayenneService = new CayenneService(resources.getService(ServerRuntime.class),
					webSiteService);
			hub.addRegistryShutdownListener(cayenneService);
			return cayenneService;
		}
	}

	public static class JMXInitServiceBuilder implements ServiceBuilder<IJMXInitService> {
		private String name;

		public JMXInitServiceBuilder(String name) {
			this.name = name;
		}

		@Override
		public IJMXInitService buildService(ServiceResources resources) {
			ApplicationGlobals applicationGlobals = resources.getService(ApplicationGlobals.class);
			RegistryShutdownHub hub = resources.getService(RegistryShutdownHub.class);
			JMXInitService service = new JMXInitService(applicationGlobals, resources.getService(DataSource.class), name);
			hub.addRegistryShutdownListener(service);
			return service;
		}
	}

	public static class CacheManagerBuilder implements ServiceBuilder<CacheManager> {
		@Override
		public CacheManager buildService(ServiceResources resources) {
			CayenneRuntime cayenneRuntime = resources.getService(ServerRuntime.class);
			CacheManager cacheManager = cayenneRuntime.getInjector().getInstance(CacheManager.class);
			return cacheManager;
		}
	}

	public static class ContentCacheServiceBuilder implements ServiceBuilder<IContentCacheService> {
		@Override
		public IContentCacheService buildService(ServiceResources resources) {
			return new ContentCacheService(resources.getService(CacheManager.class));
		}
	}

	public static class PaymentGatewayServiceBuilder implements ServiceBuilder<IPaymentGatewayService> {
		@Override
		public IPaymentGatewayService buildService(ServiceResources resources) {
			IPaymentGatewayServiceBuilder builder = resources.getService(IPaymentGatewayServiceBuilder.class);
			return builder.buildService();
		}
	}


	public static class PaymentGatewayBuilder implements ServiceBuilder<INewPaymentGatewayService> {
		@Override
		public INewPaymentGatewayService buildService(ServiceResources resources) {
			INewPaymentGatewayServiceBuilder builder = resources.getService(INewPaymentGatewayServiceBuilder.class);
			return builder.buildService();
		}
	}


}
