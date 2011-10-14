package ish.oncourse.webservices.soap.v4;

import ish.oncourse.model.College;
import ish.oncourse.model.services.ModelModule;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.paymentexpress.IPaymentGatewayService;
import ish.oncourse.services.paymentexpress.TestPaymentGatewayService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.WebSiteServiceOverride;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.reference.services.ReferenceService;
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

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.ServiceBuilder;
import org.apache.tapestry5.ioc.ServiceResources;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.SubModule;

@SubModule({ ModelModule.class, ServiceModule.class })
public class ReplicationTestModule {

	public static void bind(ServiceBinder binder) {

		binder.bind(ReferenceService.class);
		binder.bind(ReferenceStubBuilder.class);
		binder.bind(IWillowStubBuilder.class, WillowStubBuilderImpl.class);
		binder.bind(IWillowUpdater.class, WillowUpdaterImpl.class);
		binder.bind(IWillowQueueService.class, WillowQueueService.class);
		binder.bind(ITransactionStubBuilder.class, TransactionStubBuilderImpl.class);
		binder.bind(IReplicationService.class, ReplicationServiceImpl.class);

		binder.bind(ITransactionGroupProcessor.class, new ServiceBuilder<ITransactionGroupProcessor>() {
			@Override
			public ITransactionGroupProcessor buildService(ServiceResources res) {
				return new TransactionGroupProcessorImpl(res.getService(ICayenneService.class), res.getService("WebSiteServiceOverride",
						IWebSiteService.class), res.getService(IWillowUpdater.class), false);
			}
		}).withId("NotAtomic");

		binder.bind(ITransactionGroupProcessor.class, new ServiceBuilder<ITransactionGroupProcessor>() {
			@Override
			public ITransactionGroupProcessor buildService(ServiceResources res) {
				return new TransactionGroupProcessorImpl(res.getService(ICayenneService.class), res.getService("WebSiteServiceOverride",
						IWebSiteService.class), res.getService(IWillowUpdater.class), true);
			}
		}).withId("Atomic");

		binder.bind(ReferencePortType.class, ReferencePortTypeImpl.class);
		binder.bind(PaymentPortType.class, PaymentServiceImpl.class);

		binder.bind(IWebSiteService.class, new ServiceBuilder<IWebSiteService>() {

			@Override
			public IWebSiteService buildService(final ServiceResources res) {
				WebSiteServiceOverride service = new WebSiteServiceOverride() {

					@Override
					public College getCurrentCollege() {
						ICollegeService collegeService = res.getService(ICollegeService.class);
						return collegeService.findBySecurityCode("345ttn44$%9");
					}

				};

				return service;
			}

		}).withId("WebSiteServiceOverride");

		binder.bind(IPaymentGatewayService.class, TestPaymentGatewayService.class).withId("PaymentGatewayServiceOverride");
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local IWebSiteService webSiteService) {
		configuration.add(IWebSiteService.class, webSiteService);
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local IPaymentGatewayService gatewayService) {
		configuration.add(IPaymentGatewayService.class, gatewayService);
	}
}
