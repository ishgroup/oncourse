package ish.oncourse.portal.services;

import ish.oncourse.model.services.ModelModule;
import ish.oncourse.portal.access.AccessController;
import ish.oncourse.portal.access.AuthenticationService;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.services.discussion.DiscussionServiceImpl;
import ish.oncourse.portal.services.discussion.IDiscussionService;
import ish.oncourse.portal.services.pageload.PortalPageRenderer;
import ish.oncourse.portal.services.site.PortalSiteService;
import ish.oncourse.services.DisableJavaScriptStack;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.jmx.IJMXInitService;
import ish.oncourse.services.jmx.JMXInitService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.services.site.WebSiteVersionService;
import ish.oncourse.services.usi.IUSIVerificationService;
import ish.oncourse.textile.services.TextileModule;
import ish.oncourse.util.IPageRenderer;
import ish.oncourse.util.UIRequestExceptionHandler;
import org.apache.tapestry5.MetaDataConstants;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.*;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.apache.tapestry5.ioc.services.ServiceOverride;
import org.apache.tapestry5.services.*;
import org.apache.tapestry5.services.javascript.JavaScriptStack;
import org.apache.tapestry5.services.javascript.JavaScriptStackSource;
import org.apache.tapestry5.services.linktransform.PageRenderLinkTransformer;

@SubModule({ModelModule.class, ServiceModule.class, TextileModule.class})
public class AppModule {

    private static final String EXCEPTION_REDIRECT_PAGE = "login";

    private static final String HMAC_PASSPHRASE = "T88LkO4uVSAH72BSU85FzhI6e3O31N6J";

    public static void bind(ServiceBinder binder) {

        binder.bind(IAuthenticationService.class, AuthenticationService.class);
        binder.bind(IDiscussionService.class, DiscussionServiceImpl.class);
        binder.bind(AccessController.class).withId("AccessController");
        binder.bind(IWebSiteService.class, PortalSiteService.class).withId("WebSiteServiceOverride");
        binder.bind(IPageRenderer.class, PortalPageRenderer.class).withId("PortalPageRenderer");
        //we set PERTHREAD scope for the service to be sure that we don't sure any data between requests
        binder.bind(IPortalService.class, PortalService.class).scope(ScopeConstants.PERTHREAD);
        binder.bind(IWebSiteVersionService.class, WebSiteVersionService.class);
        binder.bind(ExpiredSessionController.class).withId("ExpiredSessionController");
        binder.bind(IUSIVerificationService.class, TestUSISevice.class);
    }

    @EagerLoad
    public static IJMXInitService buildJMXInitService(ApplicationGlobals applicationGlobals, RegistryShutdownHub hub) {
        JMXInitService jmxService = new JMXInitService(applicationGlobals, "portal", "ish.oncourse:type=PortalApplicationData");
        hub.addRegistryShutdownListener(jmxService);
        return jmxService;
    }

    @Contribute(ServiceOverride.class)
    public static void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local IWebSiteService webSiteService) {
        configuration.add(IWebSiteService.class, webSiteService);
    }

    @Contribute(ServiceOverride.class)
    public static void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local IPageRenderer pageRenderer) {
        configuration.add(IPageRenderer.class, pageRenderer);
    }


    public void contributeMasterDispatcher(OrderedConfiguration<Dispatcher> configuration,
                                           @InjectService("AccessController") Dispatcher accessController,
                                           @InjectService("ExpiredSessionController") Dispatcher expiredSessionController) {
        configuration.add("AccessController", accessController, "before:PageRender");
        configuration.add("ExpiredSessionController", expiredSessionController, "before:ComponentEvent");
    }

    public void contributeMetaDataLocator(MappedConfiguration<String, String> configuration) {
        configuration.add(MetaDataConstants.SECURE_PAGE, "true");
    }

    public void contributeApplicationDefaults(MappedConfiguration<String, String> configuration) {
        configuration.add(SymbolConstants.HMAC_PASSPHRASE, HMAC_PASSPHRASE);
    }

    public RequestExceptionHandler buildAppRequestExceptionHandler(ComponentSource componentSource, ResponseRenderer renderer, Request request,
                                                                   Response response) {
        return new UIRequestExceptionHandler(componentSource, renderer, request, response, UIRequestExceptionHandler.DEFAULT_ERROR_PAGE,
                EXCEPTION_REDIRECT_PAGE, true);
    }

    public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local RequestExceptionHandler handler) {
        configuration.add(RequestExceptionHandler.class, handler);
    }

    @Contribute(MarkupRenderer.class)
    public static void deactiveDefaultCSS(OrderedConfiguration<MarkupRendererFilter> configuration) {
        configuration.override("InjectDefaultStylesheet", null);
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

}
