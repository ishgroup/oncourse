package ish.oncourse.portal.services;

import com.google.inject.Injector;
import io.bootique.jdbc.DataSourceFactory;
import ish.oncourse.model.services.ModelModule;
import ish.oncourse.portal.PortalModule;
import ish.oncourse.portal.access.AccessController;
import ish.oncourse.portal.access.AuthenticationService;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.access.validate.AccessLinksValidatorFactory;
import ish.oncourse.portal.services.application.IPortalApplicationService;
import ish.oncourse.portal.services.application.PortalApplicationServiceImpl;
import ish.oncourse.portal.services.discussion.DiscussionServiceImpl;
import ish.oncourse.portal.services.discussion.IDiscussionService;
import ish.oncourse.portal.services.pageload.PortalPageRenderer;
import ish.oncourse.portal.services.site.PortalSiteService;
import ish.oncourse.services.DisableJavaScriptStack;
import ish.oncourse.services.ServiceModule;
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
import ish.oncourse.services.html.IPlainTextExtractor;
import ish.oncourse.services.html.JerichoPlainTextExtractor;
import ish.oncourse.services.jmx.IJMXInitService;
import ish.oncourse.services.location.IPostCodeDbService;
import ish.oncourse.services.location.PostCodeDbService;
import ish.oncourse.services.mail.IMailService;
import ish.oncourse.services.mail.MailService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.node.WebNodeService;
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
import ish.oncourse.services.usi.IUSIVerificationService;
import ish.oncourse.services.voucher.IVoucherService;
import ish.oncourse.services.voucher.VoucherService;
import ish.oncourse.textile.services.TextileModule;
import ish.oncourse.util.IPageRenderer;
import ish.oncourse.util.UIRequestExceptionHandler;
import ish.oncourse.webservices.usi.USIService;
import ish.oncourse.webservices.usi.crypto.CryptoKeys;
import ish.oncourse.webservices.usi.tapestry.CryptoKeysBuilder;
import ish.oncourse.webservices.usi.tapestry.USIServiceBuilder;
import net.sf.ehcache.CacheManager;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.tapestry5.Asset;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.internal.services.TapestrySessionFactory;
import org.apache.tapestry5.ioc.*;
import org.apache.tapestry5.ioc.annotations.*;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.apache.tapestry5.ioc.services.ServiceOverride;
import org.apache.tapestry5.services.*;
import org.apache.tapestry5.services.javascript.JavaScriptAggregationStrategy;
import org.apache.tapestry5.services.javascript.JavaScriptStack;
import org.apache.tapestry5.services.javascript.JavaScriptStackSource;
import org.apache.tapestry5.services.javascript.StylesheetLink;
import org.apache.tapestry5.services.linktransform.PageRenderLinkTransformer;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

import static ish.oncourse.services.ServiceModule.APP_TEST_MODE;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.tapestry5.ioc.ScopeConstants.PERTHREAD;

@ImportModule({ModelModule.class, TextileModule.class})
public class AppModule {

	private static final String EXCEPTION_REDIRECT_PAGE = "login";

	private static final String HMAC_PASSPHRASE = "T88LkO4uVSAH72BSU85FzhI6e3O31N6J";

	public static void bind(ServiceBinder binder) {
		boolean isInTestMode = Boolean.getBoolean(APP_TEST_MODE);

		bindReferenceServices(binder);
		bindEntityServices(binder);
		bindPaymentGatewayServices(binder);
		bindEnvServices(binder);

		binder.bind(IWebSiteService.class, PortalSiteService.class);
		binder.bind(IWebSiteVersionService.class, WebSiteVersionService.class);
		binder.bind(IWebContentService.class, WebContentService.class);
		binder.bind(IWebNodeService.class, WebNodeService.class);

		binder.bind(IAuthenticationService.class, AuthenticationService.class);
		binder.bind(IDiscussionService.class, DiscussionServiceImpl.class);
		binder.bind(AccessController.class).withId("AccessController");

		binder.bind(IPageRenderer.class, PortalPageRenderer.class);
		binder.bind(IPortalService.class, PortalService.class).scope(ScopeConstants.PERTHREAD);
		binder.bind(IPortalApplicationService.class, PortalApplicationServiceImpl.class);

		binder.bind(ExpiredSessionController.class).withId("ExpiredSessionController");
		binder.bind(TapestrySessionFactory.class, ISHTapestrySessionFactoryImpl.class).withId("ISHTapestrySessionFactoryImpl");
		binder.bind(AccessLinksValidatorFactory.class, AccessLinksValidatorFactory.class);

		binder.bind(CacheManager.class, new ServiceModule.CacheManagerBuilder());

		bindUSIServices(binder);
	}

	private static void bindUSIServices(ServiceBinder binder) {
		binder.bind(CryptoKeys.class, new CryptoKeysBuilder()).eagerLoad();
		binder.bind(USIService.class, new USIServiceBuilder()).eagerLoad();
		binder.bind(IUSIVerificationService.class, PortalUSIService.class);
	}


	private static void bindEnvServices(ServiceBinder binder) {
		binder.bind(ICayenneService.class, new CayenneServiceBuilder()).eagerLoad();
		binder.bind(PreferenceController.class).withId(PreferenceController.class.getSimpleName());
		binder.bind(PreferenceControllerFactory.class).withId(PreferenceControllerFactory.class.getSimpleName());

		binder.bind(IEnvironmentService.class, EnvironmentService.class);

		binder.bind(ITextileConverter.class, TextileConverter.class);
		binder.bind(IPlainTextExtractor.class, JerichoPlainTextExtractor.class);

		binder.bind(EncryptionService.class).withId(EnvironmentService.class.getSimpleName());

		binder.bind(IMailService.class, MailService.class);

		binder.bind(ICookiesService.class, CookiesService.class);
		binder.bind(ICookiesOverride.class, CookiesImplOverride.class);

		binder.bind(IS3Service.class, S3Service.class);

		binder.bind(ISearchService.class, SearchService.class);

		binder.bind(IJMXInitService.class, new JMXInitServiceBuilder());
	}

	private static void bindPaymentGatewayServices(ServiceBinder binder) {
		binder.bind(IPaymentGatewayServiceBuilder.class, PaymentGatewayServiceBuilder.class);
		binder.bind(IPaymentGatewayService.class, new ServiceModule.PaymentGatewayServiceBuilder()).scope(PERTHREAD);

		binder.bind(INewPaymentGatewayServiceBuilder.class, NewPaymentGatewayServiceBuilder.class);
		binder.bind(INewPaymentGatewayService.class, new ServiceModule.PaymentGatewayBuilder()).scope(PERTHREAD);
	}

	private static void bindReferenceServices(ServiceBinder binder) {
		// Reference Data services
		binder.bind(ICountryService.class, CountryService.class).withId("CountryService");
		binder.bind(ILanguageService.class, LanguageService.class).withId("LanguageService");
		binder.bind(IModuleService.class, ModuleService.class).withId("ModuleService");
		binder.bind(IQualificationService.class, QualificationService.class).withId("QualificationService");
		binder.bind(ITrainingPackageService.class, TrainingPackageService.class).withId("TrainingPackageService");
		binder.bind(IPostcodeService.class, PostcodeService.class).withId("PostcodeService");
	}

	private static void bindEntityServices(ServiceBinder binder) {
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
	}

	@Decorate(serviceInterface = JavaScriptStackSource.class)
	public JavaScriptStackSource decorateJavaScriptStackSource(JavaScriptStackSource original) {
		return new EmptyJavaScriptStackSource();
	}

	@Contribute(ServiceOverride.class)
	public static void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local TapestrySessionFactory sessionFactory) {
		configuration.add(TapestrySessionFactory.class, sessionFactory);
	}

	public void contributeMasterDispatcher(OrderedConfiguration<Dispatcher> configuration,
										   @InjectService("AccessController") Dispatcher accessController,
										   @InjectService("ExpiredSessionController") Dispatcher expiredSessionController) {
		configuration.add("AccessController", accessController, "before:PageRender");
		configuration.add("ExpiredSessionController", expiredSessionController, "before:ComponentEvent");
	}

	public void contributeApplicationDefaults(MappedConfiguration<String, String> configuration) {
		configuration.add(SymbolConstants.HMAC_PASSPHRASE, HMAC_PASSPHRASE);
		configuration.add(SearchService.ALIAS_SUFFIX_PROPERTY, EMPTY);
	}

	public RequestExceptionHandler buildAppRequestExceptionHandler(ComponentSource componentSource, ResponseRenderer renderer, Request request,
																   Response response) {
		return new UIRequestExceptionHandler(componentSource, renderer, request, response, UIRequestExceptionHandler.DEFAULT_ERROR_PAGE,
				EXCEPTION_REDIRECT_PAGE, true);
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local RequestExceptionHandler handler) {
		configuration.add(RequestExceptionHandler.class, handler);
	}

	@Contribute(JavaScriptStackSource.class)
	public static void deactiveJavaScript(MappedConfiguration<String, JavaScriptStack> configuration) {
		configuration.overrideInstance(InternalConstants.CORE_STACK_NAME, DisableJavaScriptStack.class);
	}

	@Contribute(PageRenderLinkTransformer.class)
	@Primary
	public static void provideURLRewriting(OrderedConfiguration<PageRenderLinkTransformer> configuration) {
		configuration.addInstance("PageLinkRule", PageLinkTransformer.class);
	}


	private static class EmptyJavaScriptStack implements JavaScriptStack {
		private EmptyJavaScriptStack() {
		}

		@Override
		public List<String> getModules() {
			return Collections.emptyList();
		}

		@Override
		public JavaScriptAggregationStrategy getJavaScriptAggregationStrategy() {
			return JavaScriptAggregationStrategy.DO_NOTHING;
		}

		@Override
		public List<String> getStacks() {
			return Collections.emptyList();
		}

		@Override
		public List<Asset> getJavaScriptLibraries() {
			return Collections.emptyList();
		}

		@Override
		public List<StylesheetLink> getStylesheets() {
			return Collections.emptyList();
		}

		@Override
		public String getInitialization() {
			return null;
		}
	}

	private static class EmptyJavaScriptStackSource implements JavaScriptStackSource {
		private EmptyJavaScriptStack stack = new EmptyJavaScriptStack();

		@Override
		public JavaScriptStack getStack(String name) {
			return stack;
		}

		@Override
		public JavaScriptStack findStack(String name) {
			return stack;
		}

		@Override
		public List<String> getStackNames() {
			return Collections.emptyList();
		}

		@Override
		public JavaScriptStack findStackForJavaScriptLibrary(Resource resource) {
			return stack;
		}
	}


	public static class CayenneServiceBuilder implements ServiceBuilder<ICayenneService> {
		@Override
		public ICayenneService buildService(ServiceResources resources) {
			RegistryShutdownHub hub = resources.getService(RegistryShutdownHub.class);
			IWebSiteService webSiteService = resources.getService(IWebSiteService.class);
			Injector injector = resources.getService(Injector.class);

			CayenneService cayenneService = new CayenneService(injector.getInstance(ServerRuntime.class),
					webSiteService);
			hub.addRegistryShutdownListener(cayenneService);
			return cayenneService;
		}
	}

	public static class JMXInitServiceBuilder implements ServiceBuilder<IJMXInitService> {
		@Override
		public IJMXInitService buildService(ServiceResources resources) {
			ApplicationGlobals applicationGlobals = resources.getService(ApplicationGlobals.class);
			DataSourceFactory dataSourceFactory = resources.getService(Injector.class).getInstance(DataSourceFactory.class);
			DataSource dataSource = dataSourceFactory.forName(PortalModule.DATA_SOURCE_NAME);
			RegistryShutdownHub hub = resources.getService(RegistryShutdownHub.class);

			JMXInitService service = new JMXInitService(applicationGlobals, dataSource);
			hub.addRegistryShutdownListener(service);
			return service;
		}
	}

}
