package ish.oncourse.portal.services;

import ish.oncourse.mbean.ApplicationData;
import ish.oncourse.mbean.MBeanRegisterUtil;
import ish.oncourse.model.services.ModelModule;
import ish.oncourse.portal.access.AccessController;
import ish.oncourse.portal.access.AuthenticationService;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.services.discussion.DiscussionServiceImpl;
import ish.oncourse.portal.services.discussion.IDiscussionService;
import ish.oncourse.portal.services.mail.IMailService;
import ish.oncourse.portal.services.mail.MailServiceImpl;
import ish.oncourse.portal.services.pageload.*;
import ish.oncourse.portal.services.site.PortalSiteService;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.textile.services.TextileModule;
import ish.oncourse.util.IPageRenderer;
import ish.oncourse.util.UIRequestExceptionHandler;
import org.apache.tapestry5.MetaDataConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.*;
import org.apache.tapestry5.ioc.services.ServiceOverride;
import org.apache.tapestry5.services.*;
import org.apache.tapestry5.services.pageload.ComponentRequestSelectorAnalyzer;
import org.apache.tapestry5.services.pageload.ComponentResourceLocator;

@SubModule({ ModelModule.class, ServiceModule.class, TextileModule.class })
public class AppModule {

	public static void bind(ServiceBinder binder) {
		binder.bind(IUserAgentDetector.class, UserAgentDetectorImpl.class);
		binder.bind(ComponentRequestSelectorAnalyzer.class, PortalComponentRequestSelectorAnalyzer.class).withId(
				"PortalComponentRequestSelectorAnalyzer");

		binder.bind(IAuthenticationService.class, AuthenticationService.class);
		binder.bind(IMailService.class, MailServiceImpl.class);
		binder.bind(IDiscussionService.class, DiscussionServiceImpl.class);
		binder.bind(AccessController.class).withId("AccessController");
		binder.bind(IWebSiteService.class, PortalSiteService.class).withId("WebSiteServiceOverride");
		binder.bind(IPageRenderer.class, PortalPageRenderer.class).withId("PortalPageRenderer");
		MBeanRegisterUtil.registerMbeanService(new ApplicationData("portal"), "ish.oncourse:type=PortalApplicationData");
	}

	@Contribute(ServiceOverride.class)
	public static void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local IWebSiteService webSiteService) {
		configuration.add(IWebSiteService.class, webSiteService);
	}

	@Contribute(ServiceOverride.class)
	public static void overrideSelectorAnalyzer(MappedConfiguration<Class<?>, Object> cfg,
			@InjectService("PortalComponentRequestSelectorAnalyzer") ComponentRequestSelectorAnalyzer analyzer) {
		cfg.add(ComponentRequestSelectorAnalyzer.class, analyzer);
	}
	
	@Contribute(ServiceOverride.class)
	public static void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local IPageRenderer pageRenderer) {
		configuration.add(IPageRenderer.class, pageRenderer);
	}

	@Decorate(serviceInterface = ComponentResourceLocator.class)
	public static Object customComponentResourceLocator(ComponentResourceLocator delegate) {
		return new PortalComponentResourceLocator(delegate);
	}

	public void contributeMasterDispatcher(OrderedConfiguration<Dispatcher> configuration,
			@InjectService("AccessController") Dispatcher accessController) {
		configuration.add("AccessController", accessController, "before:PageRender");
	}
	
	public void contributeMetaDataLocator(MappedConfiguration<String, String> configuration) {
		configuration.add(MetaDataConstants.SECURE_PAGE, "true");
	}
	
	public RequestExceptionHandler buildAppRequestExceptionHandler(ComponentSource componentSource,
                                                                   ResponseRenderer renderer,
                                                                   Request request,
                                                                   Response response) {
		return new UIRequestExceptionHandler(componentSource,renderer,request,response, "errorPage", "login");
	}
	
	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local RequestExceptionHandler handler) {
		configuration.add(RequestExceptionHandler.class, handler);
	}
}
