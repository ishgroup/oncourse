package ish.oncourse.admin.services;

import au.gov.training.services.organisation.IOrganisationService;
import au.gov.training.services.trainingcomponent.ITrainingComponentService;
import ish.oncourse.admin.services.ntis.OrganisationServiceBuilder;
import ish.oncourse.admin.services.ntis.TrainingComponentServiceBuilder;
import ish.oncourse.model.services.ModelModule;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.site.IWebSiteVersionService;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.SubModule;

@SubModule({ ModelModule.class, ServiceModule.class })
public class AdminTestModule {
	public static void bind(ServiceBinder binder) {
		binder.bind(ITrainingComponentService.class, new TrainingComponentServiceBuilder());
		binder.bind(IOrganisationService.class, new OrganisationServiceBuilder());
		binder.bind(IWebSiteVersionService.class, WebSiteVersionServiceOverride.class).withId("WebSiteVersionServiceAdmin");
	}
}
