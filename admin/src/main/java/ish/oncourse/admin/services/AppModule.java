package ish.oncourse.admin.services;

import ish.oncourse.admin.services.billing.BillingDataServiceImpl;
import ish.oncourse.admin.services.billing.IBillingDataService;
import ish.oncourse.admin.services.ntis.INTISUpdater;
import ish.oncourse.admin.services.ntis.NTISUpdaterImpl;
import ish.oncourse.admin.services.ntis.TrainingComponentServiceBuilder;
import ish.oncourse.model.services.ModelModule;
import ish.oncourse.services.ServiceModule;

import org.apache.tapestry5.MetaDataConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
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
	}

	public void contributeMetaDataLocator(MappedConfiguration<String, String> configuration) {
		configuration.add(MetaDataConstants.SECURE_PAGE, "true");
	}
}
