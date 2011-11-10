package ish.oncourse.admin.services;

import ish.oncourse.admin.services.billing.BillingDataServiceImpl;
import ish.oncourse.admin.services.billing.IBillingDataService;
import ish.oncourse.admin.services.ntis.INTISUpdater;
import ish.oncourse.admin.services.ntis.NTISUpdaterImpl;
import ish.oncourse.admin.services.ntis.TrainingComponentServiceBuilder;
import ish.oncourse.model.services.ModelModule;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;

import org.apache.tapestry5.MetaDataConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.ServiceBuilder;
import org.apache.tapestry5.ioc.ServiceResources;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.SubModule;

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
		binder.bind(PreferenceController.class, new ServiceBuilder<PreferenceController>() {

			@Override
			public PreferenceController buildService(ServiceResources resources) {
				return new PreferenceController(resources.getService(ICayenneService.class), 
						resources.getService("WebSiteServiceAdmin", IWebSiteService.class));
			}
			
		});
	}

	public void contributeMetaDataLocator(MappedConfiguration<String, String> configuration) {
		configuration.add(MetaDataConstants.SECURE_PAGE, "true");
	}
	
	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local IWebSiteService webSiteService) {
		configuration.add(IWebSiteService.class, webSiteService);
	}
	
}
