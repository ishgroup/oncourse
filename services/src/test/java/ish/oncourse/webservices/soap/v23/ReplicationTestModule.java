package ish.oncourse.webservices.soap.v23;

import ish.oncourse.model.College;
import ish.oncourse.services.BinderFunctions;
import ish.oncourse.services.filestorage.IFileStorageAssetService;
import ish.oncourse.services.html.NoCacheMetaProvider;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.WebSiteServiceOverride;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.services.usi.IUSIVerificationService;
import ish.oncourse.test.tapestry.TestModule;
import ish.oncourse.util.PageRenderer;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.reference.services.ReferenceStubBuilder;
import ish.oncourse.webservices.replication.builders.ITransactionStubBuilder;
import ish.oncourse.webservices.replication.builders.IWillowStubBuilder;
import ish.oncourse.webservices.replication.builders.TransactionStubBuilderImpl;
import ish.oncourse.webservices.replication.builders.WillowStubBuilderImpl;
import ish.oncourse.webservices.replication.updaters.IWillowUpdater;
import ish.oncourse.webservices.replication.updaters.WillowUpdaterImpl;
import ish.oncourse.webservices.soap.v7.ReferencePortType;
import ish.oncourse.webservices.soap.v7.ReferencePortTypeImpl;
import ish.oncourse.webservices.usi.USIVerificationService;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Local;

import javax.sql.DataSource;

public class ReplicationTestModule {

	public static void bind(ServiceBinder binder) {

		binder.bind(ServerRuntime.class, resources -> TestModule.serverRuntime.get());

		binder.bind(DataSource.class, resources -> TestModule.dataSource.get());


		BinderFunctions.bindEntityServices(binder);
		BinderFunctions.bindEnvServices(binder, "services", true);
		BinderFunctions.bindPaymentGatewayServices(binder);
		BinderFunctions.bindTapestryServices(binder, NoCacheMetaProvider.class, PageRenderer.class);
		BinderFunctions.bindReferenceServices(binder);

		binder.bind(ReferenceStubBuilder.class);
		binder.bind(IWillowStubBuilder.class, WillowStubBuilderImpl.class);
		binder.bind(IWillowUpdater.class, WillowUpdaterImpl.class);
		binder.bind(IWillowQueueService.class, WillowQueueService.class);
		binder.bind(ITransactionStubBuilder.class, TransactionStubBuilderImpl.class);
		binder.bind(IReplicationService.class, ReplicationServiceImpl.class);

		binder.bind(ITransactionGroupProcessor.class, res -> new TransactionGroupProcessorImpl(res.getService(ICayenneService.class), res.getService("WebSiteServiceOverride",
				IWebSiteService.class), res.getService(IWillowUpdater.class), res.getService(IFileStorageAssetService.class))).scope(ScopeConstants.PERTHREAD);

		binder.bind(ReferencePortType.class, ReferencePortTypeImpl.class);

		binder.bind(InternalPaymentService.class, PaymentServiceImpl.class);

		binder.bind(IWebSiteService.class, res -> {
			WebSiteServiceOverride service = new WebSiteServiceOverride() {
				@Override
				public College getCurrentCollege() {
					ICollegeService collegeService = res.getService(ICollegeService.class);
					return collegeService.findBySecurityCode("345ttn44$%9");
				}

			};

			return service;
		}).withId("WebSiteServiceOverride");

		binder.bind(IUSIVerificationService.class, USIVerificationService.class);
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local IWebSiteService webSiteService) {
		configuration.add(IWebSiteService.class, webSiteService);
	}

}
