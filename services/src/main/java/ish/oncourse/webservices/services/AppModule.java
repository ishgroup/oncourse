/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ish.oncourse.webservices.services;

import ish.oncourse.model.services.ModelModule;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.WebSiteServiceOverride;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.exception.PaymentNotFoundException;
import ish.oncourse.webservices.jobs.PaymentInExpireJob;
import ish.oncourse.webservices.jobs.SMSJob;
import ish.oncourse.webservices.quartz.QuartzInitializer;
import ish.oncourse.webservices.reference.services.ReferenceStubBuilder;
import ish.oncourse.webservices.replication.builders.ITransactionStubBuilder;
import ish.oncourse.webservices.replication.builders.IWillowStubBuilder;
import ish.oncourse.webservices.replication.builders.TransactionStubBuilderImpl;
import ish.oncourse.webservices.replication.builders.WillowStubBuilderImpl;
import ish.oncourse.webservices.replication.services.IReplicationService;
import ish.oncourse.webservices.replication.services.IWillowQueueService;
import ish.oncourse.webservices.replication.services.PaymentServiceImpl;
import ish.oncourse.webservices.replication.services.ReplicationServiceImpl;
import ish.oncourse.webservices.replication.services.TransactionGroupProcessorImpl;
import ish.oncourse.webservices.replication.services.WillowQueueService;
import ish.oncourse.webservices.replication.updaters.IWillowUpdater;
import ish.oncourse.webservices.replication.updaters.WillowUpdaterImpl;
import ish.oncourse.webservices.soap.v4.PaymentPortType;
import ish.oncourse.webservices.soap.v4.ReferencePortType;
import ish.oncourse.webservices.soap.v4.ReferencePortTypeImpl;

import java.io.IOException;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.ServiceBuilder;
import org.apache.tapestry5.ioc.ServiceResources;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.apache.tapestry5.services.ComponentSource;
import org.apache.tapestry5.services.ExceptionReporter;
import org.apache.tapestry5.services.RequestExceptionHandler;
import org.apache.tapestry5.services.ResponseRenderer;

/**
 * @author marek
 */
@SuppressWarnings("all")
@SubModule({ ModelModule.class, ServiceModule.class })
public class AppModule {

	public static void bind(ServiceBinder binder) {

		binder.bind(ReferenceStubBuilder.class);
		binder.bind(IWillowStubBuilder.class, WillowStubBuilderImpl.class);
		binder.bind(IWillowUpdater.class, WillowUpdaterImpl.class);
		binder.bind(IWillowQueueService.class, WillowQueueService.class);
		binder.bind(IReplicationService.class, ReplicationServiceImpl.class);

		binder.bind(ITransactionGroupProcessor.class, new ServiceBuilder<ITransactionGroupProcessor>() {
			@Override
			public ITransactionGroupProcessor buildService(ServiceResources res) {
				return new TransactionGroupProcessorImpl(res.getService(ICayenneService.class), res.getService("WebSiteServiceOverride",
						IWebSiteService.class), res.getService(IWillowUpdater.class));
			}
		}).scope(ScopeConstants.PERTHREAD);

		binder.bind(QuartzInitializer.class, new ServiceBuilder<QuartzInitializer>() {
			@Override
			public QuartzInitializer buildService(ServiceResources res) {
				QuartzInitializer quartzInit = new QuartzInitializer(res);
				RegistryShutdownHub hub = res.getService(RegistryShutdownHub.class);
				hub.addRegistryShutdownListener(quartzInit);
				return quartzInit;
			}
		}).eagerLoad();

		binder.bind(ReferencePortType.class, ReferencePortTypeImpl.class);
		binder.bind(PaymentPortType.class, PaymentServiceImpl.class);
		binder.bind(ITransactionStubBuilder.class, TransactionStubBuilderImpl.class);
		binder.bind(IWebSiteService.class, WebSiteServiceOverride.class).withId("WebSiteServiceOverride");

		binder.bind(SMSJob.class);
		binder.bind(PaymentInExpireJob.class);
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local IWebSiteService webSiteService) {
		configuration.add(IWebSiteService.class, webSiteService);
	}

	public RequestExceptionHandler buildAppRequestExceptionHandler(final org.slf4j.Logger logger, final ResponseRenderer renderer,
			final ComponentSource componentSource) {
		return new RequestExceptionHandler() {
			public void handleRequestException(Throwable exception) throws IOException {
				logger.error("Unexpected runtime exception: " + exception.getMessage(), exception);
				Throwable cause = exception.getCause();
				if (cause != null) {
					// Trying to get possible PaymentNotFoundException, which is
					// enclosed by TapestryException and RenderQueueException
					cause = cause.getCause();
				}
				String exceptionPageName = null;
				if (cause != null && cause instanceof PaymentNotFoundException) {
					exceptionPageName = "PaymentNotFound";
					exception = cause;
				} else {
					exceptionPageName = "ErrorPage";
				}
				ExceptionReporter exceptionReporter = (ExceptionReporter) componentSource.getPage(exceptionPageName);
				exceptionReporter.reportException(exception);
				renderer.renderPageMarkupResponse(exceptionPageName);
			}
		};
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local RequestExceptionHandler handler) {
		configuration.add(RequestExceptionHandler.class, handler);
	}
}
