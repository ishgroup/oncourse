package ish.oncourse.portal.services;

import ish.oncourse.configuration.ISHHealthCheckServlet;
import ish.oncourse.portal.access.*;
import ish.oncourse.portal.access.validate.AccessLinksValidatorFactory;
import ish.oncourse.portal.services.application.IPortalApplicationService;
import ish.oncourse.portal.services.application.PortalApplicationServiceImpl;
import ish.oncourse.portal.services.discussion.DiscussionServiceImpl;
import ish.oncourse.portal.services.discussion.IDiscussionService;
import ish.oncourse.portal.services.pageload.PortalPageRenderer;
import ish.oncourse.portal.services.site.PortalSiteService;
import ish.oncourse.services.BinderFunctions;
import ish.oncourse.services.DisableJavaScriptStack;
import ish.oncourse.services.search.SearchService;
import ish.oncourse.services.usi.IUSIVerificationService;
import ish.oncourse.textile.services.TextileModule;
import ish.oncourse.util.ComponentPageResponseRenderer;
import ish.oncourse.util.IComponentPageResponseRenderer;
import ish.oncourse.util.IPageRenderer;
import ish.oncourse.util.UIRequestExceptionHandler;
import org.apache.tapestry5.Asset;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.internal.services.TapestrySessionFactory;
import org.apache.tapestry5.ioc.*;
import org.apache.tapestry5.ioc.annotations.*;
import org.apache.tapestry5.ioc.services.ServiceOverride;
import org.apache.tapestry5.services.*;
import org.apache.tapestry5.services.javascript.JavaScriptAggregationStrategy;
import org.apache.tapestry5.services.javascript.JavaScriptStack;
import org.apache.tapestry5.services.javascript.JavaScriptStackSource;
import org.apache.tapestry5.services.javascript.StylesheetLink;
import org.apache.tapestry5.services.linktransform.PageRenderLinkTransformer;

import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@ImportModule({TextileModule.class})
public class AppModule {

	private static final String EXCEPTION_REDIRECT_PAGE = "login";

	private static final String HMAC_PASSPHRASE = "T88LkO4uVSAH72BSU85FzhI6e3O31N6J";

	public static void bind(ServiceBinder binder) {
		BinderFunctions.bindReferenceServices(binder);
		BinderFunctions.bindEntityServices(binder);
		BinderFunctions.bindWebSiteServices(binder, PortalSiteService.class);
		BinderFunctions.bindPaymentGatewayServices(binder);
		BinderFunctions.bindEnvServices(binder, "portal", false);
		ish.oncourse.webservices.usi.tapestry.BinderFunctions.bindUSIServices(binder);


		binder.bind(IComponentPageResponseRenderer.class, ComponentPageResponseRenderer.class);

		binder.bind(ISessionManager.class, ZKSessionManager.class).scope(ScopeConstants.DEFAULT);
		binder.bind(IAuthenticationService.class, AuthenticationService.class);
		binder.bind(IDiscussionService.class, DiscussionServiceImpl.class);
		binder.bind(AccessController.class).withId("AccessController");

		binder.bind(IPageRenderer.class, PortalPageRenderer.class);
		binder.bind(IPortalService.class, PortalService.class).scope(ScopeConstants.PERTHREAD);
		binder.bind(IPortalApplicationService.class, PortalApplicationServiceImpl.class);

		binder.bind(TapestrySessionFactory.class, ISHTapestrySessionFactoryImpl.class).withId("ISHTapestrySessionFactoryImpl");
		binder.bind(AccessLinksValidatorFactory.class, AccessLinksValidatorFactory.class);
		binder.bind(IUSIVerificationService.class, PortalUSIService.class);

	}


	public static void contributeIgnoredPathsFilter(Configuration<String> configuration) {
		configuration.add(ISHHealthCheckServlet.ISH_HEALTH_CHECK_PATTERN);
	}

	@Contribute(ServiceOverride.class)
	public static void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local TapestrySessionFactory sessionFactory) {
		configuration.add(TapestrySessionFactory.class, sessionFactory);
	}

	public void contributeMasterDispatcher(OrderedConfiguration<Dispatcher> configuration,
										   @InjectService("AccessController") Dispatcher accessController) {
		configuration.add("AccessController", accessController, "before:PageRender");
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

	@Contribute(PageRenderLinkTransformer.class)
	@Primary
	public static void provideURLRewriting(OrderedConfiguration<PageRenderLinkTransformer> configuration) {
		configuration.addInstance("PageLinkRule", PageLinkTransformer.class);
	}


	@Contribute(JavaScriptStackSource.class)
	public static void deactiveJavaScript(MappedConfiguration<String, JavaScriptStack> configuration) {
		configuration.overrideInstance(InternalConstants.CORE_STACK_NAME, DisableJavaScriptStack.class);
	}

	@Decorate(serviceInterface = JavaScriptStackSource.class)
	public JavaScriptStackSource decorateJavaScriptStackSource(JavaScriptStackSource original) {
		return new EmptyJavaScriptStackSource();
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
}
