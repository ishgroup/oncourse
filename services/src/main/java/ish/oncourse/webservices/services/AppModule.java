/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ish.oncourse.webservices.services;

import ish.oncourse.services.BinderFunctions;
import ish.oncourse.services.alias.IWebUrlAliasService;
import ish.oncourse.services.alias.WebUrlAliasService;
import ish.oncourse.services.application.ApplicationServiceImpl;
import ish.oncourse.services.application.IApplicationService;
import ish.oncourse.services.binary.BinaryDataService;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.contact.ContactServiceImpl;
import ish.oncourse.services.contact.IContactService;
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
import ish.oncourse.services.encrypt.EncryptionService;
import ish.oncourse.services.enrol.EnrolmentServiceImpl;
import ish.oncourse.services.enrol.IEnrolmentService;
import ish.oncourse.services.environment.EnvironmentService;
import ish.oncourse.services.environment.IEnvironmentService;
import ish.oncourse.services.filestorage.FileStorageAssetService;
import ish.oncourse.services.filestorage.IFileStorageAssetService;
import ish.oncourse.services.format.FormatService;
import ish.oncourse.services.format.IFormatService;
import ish.oncourse.services.jmx.IJMXInitService;
import ish.oncourse.services.location.IPostCodeDbService;
import ish.oncourse.services.location.PostCodeDbService;
import ish.oncourse.services.mail.IMailService;
import ish.oncourse.services.mail.MailService;
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
import ish.oncourse.services.s3.IS3Service;
import ish.oncourse.services.s3.S3Service;
import ish.oncourse.services.search.ISearchService;
import ish.oncourse.services.search.SearchService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.WebSiteServiceOverride;
import ish.oncourse.services.sites.ISitesService;
import ish.oncourse.services.sites.SitesService;
import ish.oncourse.services.sms.DefaultSMSService;
import ish.oncourse.services.sms.ISMSService;
import ish.oncourse.services.sms.TestModeSMSService;
import ish.oncourse.services.system.CollegeService;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.tag.TagService;
import ish.oncourse.services.templates.IWebTemplateService;
import ish.oncourse.services.templates.WebTemplateService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.services.textile.TextileConverter;
import ish.oncourse.services.threading.ThreadSource;
import ish.oncourse.services.threading.ThreadSourceImpl;
import ish.oncourse.services.tutor.ITutorService;
import ish.oncourse.services.tutor.TutorService;
import ish.oncourse.services.usi.IUSIVerificationService;
import ish.oncourse.services.visitor.IParsedContentVisitor;
import ish.oncourse.services.visitor.ParsedContentVisitor;
import ish.oncourse.services.voucher.IVoucherService;
import ish.oncourse.services.voucher.VoucherService;
import ish.oncourse.util.*;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.jobs.PaymentInExpireJob;
import ish.oncourse.webservices.jobs.SMSJob;
import ish.oncourse.webservices.jobs.UpdateAmountOwingJob;
import ish.oncourse.webservices.reference.services.ReferenceStubBuilder;
import ish.oncourse.webservices.replication.builders.ITransactionStubBuilder;
import ish.oncourse.webservices.replication.builders.IWillowStubBuilder;
import ish.oncourse.webservices.replication.builders.TransactionStubBuilderImpl;
import ish.oncourse.webservices.replication.builders.WillowStubBuilderImpl;
import ish.oncourse.webservices.replication.services.*;
import ish.oncourse.webservices.replication.updaters.IWillowUpdater;
import ish.oncourse.webservices.replication.updaters.WillowUpdaterImpl;
import ish.oncourse.webservices.usi.USIService;
import ish.oncourse.webservices.usi.USIVerificationService;
import ish.oncourse.webservices.usi.crypto.CryptoKeys;
import ish.oncourse.webservices.usi.tapestry.CryptoKeysBuilder;
import ish.oncourse.webservices.usi.tapestry.USIServiceBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.IOCSymbols;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.services.ApplicationDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.services.LibraryMapping;
import org.apache.tapestry5.services.RequestExceptionHandler;

import static ish.oncourse.services.ServiceModule.APP_TEST_MODE;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.apache.tapestry5.ioc.ScopeConstants.PERTHREAD;

public class AppModule {

	private static Logger logger = LogManager.getLogger();

	public static void bind(ServiceBinder binder) {
		boolean isInTestMode = "true".equalsIgnoreCase(System.getProperty(APP_TEST_MODE));

		if (isInTestMode) {
			logger.warn("This application is in TEST MODE. SMS and credit card processing is disabled.");
		}

		logger.info("Registering Willow Common Services");

		// Tapestry and environment specific services
		binder.bind(V5ReferenceService.class);
		binder.bind(V6ReferenceService.class);

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
		binder.bind(IParsedContentVisitor.class, ParsedContentVisitor.class);

		// Data specific serivces
		binder.bind(IBinaryDataService.class, BinaryDataService.class);
		binder.bind(ICollegeService.class, CollegeService.class);
		binder.bind(ICourseClassService.class, CourseClassService.class);
		binder.bind(ICourseService.class, CourseService.class);
		binder.bind(IPostCodeDbService.class, PostCodeDbService.class);

		binder.bind(PreferenceController.class);
		binder.bind(PreferenceControllerFactory.class);
		binder.bind(EncryptionService.class);
		binder.bind(IMailService.class, MailService.class);

		binder.bind(ThreadSource.class, ThreadSourceImpl.class);

		binder.bind(IContactService.class, ContactServiceImpl.class);
		binder.bind(IRoomService.class, RoomService.class);
		binder.bind(ISitesService.class, SitesService.class);
		binder.bind(ITutorService.class, TutorService.class);
		binder.bind(IWebContentService.class, WebContentService.class);
		binder.bind(IWebMenuService.class, WebMenuService.class);
		binder.bind(IWebNodeService.class, WebNodeService.class);
		binder.bind(IWebSiteService.class, WebSiteServiceOverride.class);

		binder.bind(IWebUrlAliasService.class, WebUrlAliasService.class);
		binder.bind(IWebNodeTypeService.class, WebNodeTypeService.class);
		binder.bind(IWebTemplateService.class, WebTemplateService.class);
		binder.bind(IDiscountService.class, DiscountService.class);
		binder.bind(IPaymentService.class, PaymentService.class);
		binder.bind(IMessagePersonService.class, MessagePersonService.class);
		binder.bind(IEnrolmentService.class, EnrolmentServiceImpl.class);

		// Reference Data services
		binder.bind(ICountryService.class, CountryService.class).withId("CountryService");
		binder.bind(ILanguageService.class, LanguageService.class).withId("LanguageService");
		binder.bind(IModuleService.class, ModuleService.class).withId("ModuleService");
		binder.bind(IQualificationService.class, QualificationService.class).withId("QualificationService");
		binder.bind(ITrainingPackageService.class, TrainingPackageService.class).withId("TrainingPackageService");
		binder.bind(IPostcodeService.class, PostcodeService.class).withId("PostcodeService");

		if (isInTestMode) {
			binder.bind(ISMSService.class, TestModeSMSService.class);
		} else {
			binder.bind(ISMSService.class, DefaultSMSService.class);
		}

		binder.bind(IFileStorageAssetService.class, FileStorageAssetService.class);
		binder.bind(IS3Service.class, S3Service.class).scope(PERTHREAD);
		binder.bind(IVoucherService.class, VoucherService.class);

		binder.bind(IApplicationService.class, ApplicationServiceImpl.class);

		binder.bind(ReferenceStubBuilder.class);
		binder.bind(IWillowStubBuilder.class, WillowStubBuilderImpl.class);
		binder.bind(IWillowUpdater.class, WillowUpdaterImpl.class);
		binder.bind(IWillowQueueService.class, WillowQueueService.class);
		binder.bind(IReplicationService.class, ReplicationServiceImpl.class);

		binder.bind(ITransactionGroupProcessor.class, TransactionGroupProcessorImpl.class).scope(PERTHREAD);

		binder.bind(InternalPaymentService.class, PaymentServiceImpl.class);
		binder.bind(ITransactionStubBuilder.class, TransactionStubBuilderImpl.class);

		binder.bind(PaymentInExpireJob.class);
		binder.bind(SMSJob.class);
		binder.bind(UpdateAmountOwingJob.class);

		binder.bind(CryptoKeys.class, new CryptoKeysBuilder());
		binder.bind(USIService.class, new USIServiceBuilder()).eagerLoad();
		binder.bind(IUSIVerificationService.class, USIVerificationService.class);

		binder.bind(ICayenneService.class, new BinderFunctions.CayenneServiceBuilder()).eagerLoad();
		binder.bind(IJMXInitService.class, new BinderFunctions.JMXInitServiceBuilder("services")).eagerLoad();
		binder.bind(RequestExceptionHandler.class, ServiceRequestExceptionHandler.class).withId(ServiceRequestExceptionHandler.class.getSimpleName());

		BinderFunctions.bindPaymentGatewayServices(binder);
	}


	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local RequestExceptionHandler handler) {
		configuration.add(RequestExceptionHandler.class, handler);
	}

	/**
	 * Add initial values for ParallelExecutor
	 *
	 * @param configuration
	 */
	public void contributeApplicationDefaults(MappedConfiguration<String, String> configuration) {
		configuration.add(IOCSymbols.THREAD_POOL_CORE_SIZE, "3");
		configuration.add(IOCSymbols.THREAD_POOL_MAX_SIZE, "50");
		configuration.add(IOCSymbols.THREAD_POOL_KEEP_ALIVE, "1 m");
		configuration.add(IOCSymbols.THREAD_POOL_ENABLED, "true");
	}

	public void contributeComponentClassResolver(Configuration<LibraryMapping> configuration) {
		configuration.add(new LibraryMapping("ui", "ish.oncourse.ui"));
	}


	@Contribute(SymbolProvider.class)
	@ApplicationDefaults
	public void contributeApplicationDefaults(MappedConfiguration<String, String> configuration, @Local IEnvironmentService environmentService) {
		// The version of the application, which is incorporated into URLs for
		// context and classpath assets.If not specified the random number is
		// used each time.
		// CommonUtils.VERSION_development we need only when we start our applications from IDEs (eclipse, intellij IDEA ....).
		try {
			String version = environmentService.getCiVersion();
			configuration.add(SymbolConstants.APPLICATION_VERSION, trimToNull(version) == null ? CommonUtils.VERSION_development : version);
		} catch (Exception e) {
			/**
			 * The catch was intruduce to exclude runtime exception for junits:
			 * java.lang.RuntimeException: Exception constructing service 'ServiceOverride': Construction of service 'ServiceOverride' has failed due to recursion: the service depends on itself in some way. Please check org.apache.tapestry5.ioc.internal.services.ServiceOverrideImpl(Map) (at ServiceOverrideImpl.java:31) via org.apache.tapestry5.ioc.services.TapestryIOCModule.bind(ServiceBinder) (at TapestryIOCModule.java:49) for references to another service that is itself dependent on service 'ServiceOverride'.
			 */
			logger.debug("Unexpected exception.", e);
		}
		/**
		 * The configuration property is set to avoid adding
		 * "<meta content="Apache Tapestry Framework (version 5.*.*)" name="generator"> to head.
		 */

		// ensure Tapestry does not advertise itself on our pages...
		configuration.add(SymbolConstants.OMIT_GENERATOR_META, "true");

		// this is overridden in other palces anyways, as we are using locale
		// variant for site template customization
		configuration.add(SymbolConstants.SUPPORTED_LOCALES, "en");
	}



}
