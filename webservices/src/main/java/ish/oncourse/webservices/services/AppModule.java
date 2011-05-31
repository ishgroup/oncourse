/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ish.oncourse.webservices.services;

import ish.oncourse.model.services.ModelModule;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.paymentexpress.IPaymentGatewayService;
import ish.oncourse.services.paymentexpress.TestPaymentGatewayService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.ui.services.UIModule;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.exception.PaymentNotFoundException;
import ish.oncourse.webservices.reference.services.ReferenceService;
import ish.oncourse.webservices.reference.services.ReferenceStubBuilder;
import ish.oncourse.webservices.replication.builders.IWillowStubBuilder;
import ish.oncourse.webservices.replication.builders.WillowStubBuilderImpl;
import ish.oncourse.webservices.replication.services.IWillowQueueService;
import ish.oncourse.webservices.replication.services.TransactionGroupProcessorImpl;
import ish.oncourse.webservices.replication.services.WillowQueueService;
import ish.oncourse.webservices.replication.updaters.IWillowUpdater;
import ish.oncourse.webservices.replication.updaters.WillowUpdaterImpl;
import ish.oncourse.webservices.services.site.WebSiteServiceOverride;
import ish.oncourse.webservices.soap.v4.ReferencePortType;
import ish.oncourse.webservices.soap.v4.ReferencePortTypeImpl;
import ish.oncourse.webservices.soap.v4.ReplicationPortType;
import ish.oncourse.webservices.soap.v4.ReplicationPortTypeImpl;
import ish.oncourse.webservices.soap.v4.auth.AuthenticationPortType;
import ish.oncourse.webservices.soap.v4.auth.AuthenticationPortTypeImpl;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.services.ComponentSource;
import org.apache.tapestry5.services.ExceptionReporter;
import org.apache.tapestry5.services.RequestExceptionHandler;
import org.apache.tapestry5.services.ResponseRenderer;

/**
 * 
 * @author marek
 */
@SubModule({ ModelModule.class, ServiceModule.class, UIModule.class })
public class AppModule {

	private static final Logger LOGGER = Logger.getLogger(AppModule.class);

	public static void bind(ServiceBinder binder) {
		LOGGER.info("Registering Willow WebServices");
		binder.bind(ReferenceService.class);
		binder.bind(ReferenceStubBuilder.class);
		binder.bind(IWillowStubBuilder.class, WillowStubBuilderImpl.class);
		binder.bind(IWillowUpdater.class, WillowUpdaterImpl.class);
		binder.bind(IWillowQueueService.class, WillowQueueService.class);
		binder.bind(ITransactionGroupProcessor.class, TransactionGroupProcessorImpl.class);
		binder.bind(ReplicationPortType.class, ReplicationPortTypeImpl.class);
		binder.bind(ReferencePortType.class, ReferencePortTypeImpl.class);
		binder.bind(AuthenticationPortType.class, AuthenticationPortTypeImpl.class).withId("authDefault");
		binder.bind(IWebSiteService.class, WebSiteServiceOverride.class).withId("WebSiteServiceOverride");
		binder.bind(IPaymentGatewayService.class, TestPaymentGatewayService.class).scope(ScopeConstants.PERTHREAD);
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local IWebSiteService webSiteService) {
		configuration.add(IWebSiteService.class, webSiteService);
	}

	public RequestExceptionHandler buildAppRequestExceptionHandler(final Logger logger, final ResponseRenderer renderer,
			final ComponentSource componentSource) {
		return new RequestExceptionHandler() {
			public void handleRequestException(Throwable exception) throws IOException {
				logger.error("Unexpected runtime exception: " + exception.getMessage(), exception);
				String exceptionPageName = (exception instanceof PaymentNotFoundException) ? "PaymentNotFound" : "ErrorPage";
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
