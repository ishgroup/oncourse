package ish.oncourse.admin.services;

import au.gov.training.services.organisation.IOrganisationService;
import au.gov.training.services.trainingcomponent.ITrainingComponentService;
import ish.oncourse.admin.services.ntis.INTISUpdater;
import ish.oncourse.admin.services.ntis.NTISUpdaterImpl;
import ish.oncourse.admin.services.ntis.OrganisationServiceBuilder;
import ish.oncourse.admin.services.ntis.TrainingComponentServiceBuilder;
import ish.oncourse.admin.services.storage.S3ServiceBuilder;
import ish.oncourse.services.BinderFunctions;
import ish.oncourse.services.IRichtextConverter;
import ish.oncourse.services.RichtextConverter;
import ish.oncourse.services.cache.IRequestCacheService;
import ish.oncourse.services.cache.RequestCacheService;
import ish.oncourse.services.threading.ThreadSource;
import ish.oncourse.services.threading.ThreadSourceImpl;
import ish.oncourse.util.IPageRenderer;
import ish.oncourse.util.PageRenderer;
import org.apache.tapestry5.MetaDataConstants;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.commons.Configuration;
import org.apache.tapestry5.commons.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;


/**
 * Willow Admin application service module.
 *
 */

public class AppModule {

	private static final String HMAC_PASSPHRASE = "807A760F20C70F8C9E0ACD8D955EA05399E501E5";
	private static final String HEALTHCHECK = "/*/ISHHealthCheck";

	public static void bind(ServiceBinder binder) {
		BinderFunctions.bindReferenceServices(binder);
		BinderFunctions.bindEntityServices(binder);
		BinderFunctions.bindWebSiteServices(binder, WebSiteServiceOverride.class, WebSiteVersionServiceOverride.class);
		BinderFunctions.bindPaymentGatewayServices(binder);
		BinderFunctions.bindEnvServices(binder, "admin", false, new S3ServiceBuilder());

		binder.bind(ITrainingComponentService.class, new TrainingComponentServiceBuilder());
		binder.bind(IOrganisationService.class, new OrganisationServiceBuilder());
		binder.bind(INTISUpdater.class, NTISUpdaterImpl.class);
		binder.bind(IRequestCacheService.class, RequestCacheService.class);
		binder.bind(ThreadSource.class, ThreadSourceImpl.class);
		binder.bind(IRichtextConverter.class, RichtextConverter.class);
		binder.bind(IPageRenderer.class, PageRenderer.class);
	}

	public static void contributeApplicationDefaults(
			MappedConfiguration<String, String> configuration) {
		configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
		configuration.add(SymbolConstants.SECURE_ENABLED, "false");
		configuration.add(SymbolConstants.HMAC_PASSPHRASE, HMAC_PASSPHRASE);
	}

	public void contributeMetaDataLocator(MappedConfiguration<String, String> configuration) {
		configuration.add(MetaDataConstants.SECURE_PAGE, "false");
	}

	public static void contributeIgnoredPathsFilter(Configuration<String> configuration)
	{
		configuration.add(HEALTHCHECK);
	}
}
