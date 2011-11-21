package ish.oncourse.admin.services;

import ish.oncourse.admin.services.ntis.TrainingComponentServiceBuilder;
import ish.oncourse.model.services.ModelModule;
import ish.oncourse.services.ServiceModule;

import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.SubModule;

import au.gov.training.services.trainingcomponent.ITrainingComponentService;

@SubModule({ ModelModule.class, ServiceModule.class })
public class AdminTestModule {
	public static void bind(ServiceBinder binder) {
		binder.bind(ITrainingComponentService.class, new TrainingComponentServiceBuilder());
	}
}
