/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ish.oncourse.webservices.services;

import ish.oncourse.model.services.ModelModule;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.filestorage.IFileStorageAssetService;
import ish.oncourse.services.filestorage.TempFileStorageAssetService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.WebSiteServiceOverride;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.exception.PaymentNotFoundException;
import ish.oncourse.webservices.jobs.PaymentInExpireJob;
import ish.oncourse.webservices.jobs.SMSJob;
import ish.oncourse.webservices.reference.services.ReferenceStubBuilder;
import ish.oncourse.webservices.replication.services.*;
import ish.oncourse.webservices.replication.v4.builders.ITransactionStubBuilder;
import ish.oncourse.webservices.replication.v4.builders.IWillowStubBuilder;
import ish.oncourse.webservices.replication.v4.builders.TransactionStubBuilderImpl;
import ish.oncourse.webservices.replication.v4.builders.WillowStubBuilderImpl;
import ish.oncourse.webservices.replication.v4.updaters.IWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.WillowUpdaterImpl;
import ish.oncourse.webservices.soap.v4.ReferencePortType;
import ish.oncourse.webservices.soap.v4.ReferencePortTypeImpl;
import org.apache.tapestry5.ioc.*;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.internal.OperationException;
import org.apache.tapestry5.runtime.ComponentEventException;
import org.apache.tapestry5.services.ComponentSource;
import org.apache.tapestry5.services.ExceptionReporter;
import org.apache.tapestry5.services.RequestExceptionHandler;
import org.apache.tapestry5.services.ResponseRenderer;

import java.io.IOException;

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

		binder.bind(PaymentInExpireJob.class);
		binder.bind(SMSJob.class);

        /**
         * TODO TempFileStorageAssetService should be replaced on FileStorageAssetService after we will stop saving BinaryData to the database.
         */
        binder.bind(IFileStorageAssetService.class, TempFileStorageAssetService.class).eagerLoad();
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local IWebSiteService webSiteService) {
		configuration.add(IWebSiteService.class, webSiteService);
	}

	public RequestExceptionHandler buildAppRequestExceptionHandler(final org.slf4j.Logger logger, final ResponseRenderer renderer,
			final ComponentSource componentSource) {
		return new RequestExceptionHandler() {
			public void handleRequestException(Throwable exception) throws IOException {
				if (exception instanceof OperationException && exception.getCause() instanceof ComponentEventException && 
					exception.getCause().getCause() instanceof RuntimeException && 
					"Forms require that the request method be POST and that the t:formdata query parameter have values.".equals(exception.getMessage())) {
					logger.warn("Unexpected runtime exception: " + exception.getMessage(), exception);
				} else {
					logger.error("Unexpected runtime exception: " + exception.getMessage(), exception);
				}
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
