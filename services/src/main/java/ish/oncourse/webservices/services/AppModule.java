/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ish.oncourse.webservices.services;

import ish.oncourse.model.services.ModelModule;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.filestorage.IFileStorageAssetService;
import ish.oncourse.services.jmx.IJMXInitService;
import ish.oncourse.services.jmx.JMXInitService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.WebSiteServiceOverride;
import ish.oncourse.services.usi.IUSIVerificationService;
import ish.oncourse.util.UIRequestExceptionHandler;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.exception.PaymentNotFoundException;
import ish.oncourse.webservices.jobs.PaymentInExpireJob;
import ish.oncourse.webservices.jobs.SMSJob;
import ish.oncourse.webservices.pages.PaymentNotFound;
import ish.oncourse.webservices.reference.services.ReferenceStubBuilder;
import ish.oncourse.webservices.replication.builders.ITransactionStubBuilder;
import ish.oncourse.webservices.replication.builders.IWillowStubBuilder;
import ish.oncourse.webservices.replication.builders.TransactionStubBuilderImpl;
import ish.oncourse.webservices.replication.builders.WillowStubBuilderImpl;
import ish.oncourse.webservices.replication.services.*;
import ish.oncourse.webservices.replication.updaters.IWillowUpdater;
import ish.oncourse.webservices.replication.updaters.WillowUpdaterImpl;
import ish.oncourse.webservices.soap.v4.ReferencePortType;
import ish.oncourse.webservices.soap.v4.ReferencePortTypeImpl;
import ish.oncourse.webservices.usi.USIVerificationService;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.ioc.*;
import org.apache.tapestry5.ioc.annotations.EagerLoad;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.apache.tapestry5.services.*;

/**
 * @author marek
 */
@SuppressWarnings("all")
@SubModule({ ModelModule.class, ServiceModule.class })
public class AppModule {
	private static final String WEB_SITE_SERVICE_OVERRIDE_NAME = WebSiteServiceOverride.class.getSimpleName();

	public static void bind(ServiceBinder binder) {

		binder.bind(ReferenceStubBuilder.class);
		binder.bind(IWillowStubBuilder.class, WillowStubBuilderImpl.class);
		binder.bind(IWillowUpdater.class, WillowUpdaterImpl.class);
		binder.bind(IWillowQueueService.class, WillowQueueService.class);
		binder.bind(IReplicationService.class, ReplicationServiceImpl.class);

		binder.bind(ITransactionGroupProcessor.class, new ServiceBuilder<ITransactionGroupProcessor>() {
			@Override
			public ITransactionGroupProcessor buildService(ServiceResources res) {
				return new TransactionGroupProcessorImpl(res.getService(ICayenneService.class), res.getService(WEB_SITE_SERVICE_OVERRIDE_NAME,
						IWebSiteService.class), res.getService(IWillowUpdater.class), res.getService(IFileStorageAssetService.class));
			}
		}).scope(ScopeConstants.PERTHREAD);

		binder.bind(ReferencePortType.class, ReferencePortTypeImpl.class);
		binder.bind(InternalPaymentService.class, PaymentServiceImpl.class);
		binder.bind(ITransactionStubBuilder.class, TransactionStubBuilderImpl.class);
		binder.bind(IWebSiteService.class, WebSiteServiceOverride.class).withId(WEB_SITE_SERVICE_OVERRIDE_NAME);
		binder.bind(IUSIVerificationService.class, USIVerificationService.class);

		binder.bind(PaymentInExpireJob.class);
		binder.bind(SMSJob.class);
	}
		
	/**
	 * Add initial values for ParallelExecutor
	 * @param configuration
	 */
	public void contributeApplicationDefaults(MappedConfiguration<String, String> configuration) {
		configuration.add(IOCSymbols.THREAD_POOL_CORE_SIZE, "3");
        configuration.add(IOCSymbols.THREAD_POOL_MAX_SIZE, "50");
        configuration.add(IOCSymbols.THREAD_POOL_KEEP_ALIVE, "1 m");
        configuration.add(IOCSymbols.THREAD_POOL_ENABLED, "true");
	}
	
	@EagerLoad
	public static IJMXInitService buildJMXInitService(ApplicationGlobals applicationGlobals, RegistryShutdownHub hub) {
		JMXInitService jmxService = new JMXInitService(applicationGlobals,"services","ish.oncourse:type=ServicesApplicationData");
		hub.addRegistryShutdownListener(jmxService);
		return jmxService;
	}
	
	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local IWebSiteService webSiteService) {
		configuration.add(IWebSiteService.class, webSiteService);
	}
	
	public RequestExceptionHandler buildAppRequestExceptionHandler(ComponentSource componentSource, ResponseRenderer renderer, Request request, 
		Response response) {
		return new UIRequestExceptionHandler(componentSource, renderer, request, response, UIRequestExceptionHandler.DEFAULT_ERROR_PAGE, 
			StringUtils.EMPTY, false) {

			@Override
			public String getErrorPageName(Throwable exception) {
				Throwable cause = exception.getCause();
				if (cause != null) {
					// Trying to get possible PaymentNotFoundException, which is
					// enclosed by TapestryException and RenderQueueException
					cause = cause.getCause();
				}
				String exceptionPageName = null;
				if (cause != null && cause instanceof PaymentNotFoundException) {
					exceptionPageName = PaymentNotFound.class.getSimpleName();
					exception = cause;
				} else {
					exceptionPageName = UIRequestExceptionHandler.DEFAULT_ERROR_PAGE;
				}
				return exceptionPageName;
			}
		};
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local RequestExceptionHandler handler) {
		configuration.add(RequestExceptionHandler.class, handler);
	}
}
