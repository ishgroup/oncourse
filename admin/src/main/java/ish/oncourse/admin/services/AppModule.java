package ish.oncourse.admin.services;

import ish.oncourse.admin.services.billing.BillingDataServiceImpl;
import ish.oncourse.admin.services.billing.IBillingDataService;
import ish.oncourse.admin.services.ntis.INTISUpdater;
import ish.oncourse.admin.services.ntis.NTISUpdaterImpl;
import ish.oncourse.admin.services.ntis.TrainingComponentServiceBuilder;
import ish.oncourse.admin.services.quartz.QuartzInitializer;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.model.services.ModelModule;

import org.apache.tapestry5.MetaDataConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.ServiceBuilder;
import org.apache.tapestry5.ioc.ServiceResources;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;

import au.gov.training.services.trainingcomponent.ITrainingComponentService;


/**
 * Willow Admin application service module.
 * 
 */
@SubModule({ ModelModule.class, ServiceModule.class })
public class AppModule {

	public static void bind(ServiceBinder binder) {
		binder.bind(IBillingDataService.class, BillingDataServiceImpl.class);
		binder.bind(ITrainingComponentService.class, new TrainingComponentServiceBuilder());
		binder.bind(INTISUpdater.class, NTISUpdaterImpl.class);
		binder.bind(IWebSiteService.class, WebSiteServiceOverride.class).withId("WebSiteServiceAdmin");
		
		binder.bind(QuartzInitializer.class, new ServiceBuilder<QuartzInitializer>() {
			@Override
			public QuartzInitializer buildService(ServiceResources res) {
				QuartzInitializer quartzInit = new QuartzInitializer(res);
				RegistryShutdownHub hub = res.getService(RegistryShutdownHub.class);
				hub.addRegistryShutdownListener(quartzInit);
				return quartzInit;
			}
		}).eagerLoad();
	}

	public void contributeMetaDataLocator(MappedConfiguration<String, String> configuration) {
		configuration.add(MetaDataConstants.SECURE_PAGE, "true");
	}
	
	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local IWebSiteService webSiteService) {
		configuration.add(IWebSiteService.class, webSiteService);
	}
	
}
