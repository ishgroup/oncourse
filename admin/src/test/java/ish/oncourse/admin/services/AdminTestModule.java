package ish.oncourse.admin.services;

import au.gov.training.services.organisation.IOrganisationService;
import au.gov.training.services.trainingcomponent.ITrainingComponentService;
import ish.oncourse.admin.services.ntis.OrganisationServiceBuilder;
import ish.oncourse.admin.services.ntis.TrainingComponentServiceBuilder;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.cache.IRequestCacheService;
import ish.oncourse.services.cache.RequestCacheService;
import ish.oncourse.services.site.IWebSiteVersionService;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.ImportModule;

@ImportModule({ServiceModule.class})
public class AdminTestModule {
	public static void bind(ServiceBinder binder) {
		binder.bind(ITrainingComponentService.class, new TrainingComponentServiceBuilder());
		binder.bind(IOrganisationService.class, new OrganisationServiceBuilder());
		binder.bind(IWebSiteVersionService.class, WebSiteVersionServiceOverride.class).withId("WebSiteVersionServiceAdmin");
		binder.bind(IRequestCacheService.class, RequestCacheService.class);
	}
}
