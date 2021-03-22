package ish.oncourse.admin.services;

import au.gov.training.services.organisation.IOrganisationService;
import au.gov.training.services.trainingcomponent.ITrainingComponentService;
import ish.oncourse.admin.services.ntis.INTISUpdater;
import ish.oncourse.admin.services.ntis.NTISUpdaterImpl;
import ish.oncourse.admin.services.ntis.OrganisationServiceBuilder;
import ish.oncourse.admin.services.ntis.TrainingComponentServiceBuilder;
import ish.oncourse.cayenne.WillowCayenneModuleBuilder;
import ish.oncourse.services.BinderFunctions;
import ish.oncourse.services.binary.BinaryDataService;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.cache.IRequestCacheService;
import ish.oncourse.services.cache.RequestCacheService;
import ish.oncourse.services.course.CourseService;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.courseclass.CourseClassService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.search.ISearchService;
import ish.oncourse.services.search.SearchService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.tag.TagService;
import ish.oncourse.services.IRichtextConverter;
import ish.oncourse.services.RichtextConverter;
import ish.oncourse.services.threading.ThreadSource;
import ish.oncourse.services.threading.ThreadSourceImpl;
import ish.oncourse.util.IPageRenderer;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.tapestry5.ioc.ServiceBinder;

import javax.cache.CacheManager;


public class AdminTestModule {
	public static void bind(ServiceBinder binder) {
		BinderFunctions.bindReferenceServices(binder);
		BinderFunctions.bindWebSiteServices(binder, WebSiteServiceOverride.class, WebSiteVersionServiceOverride.class);
		binder.bind(ServerRuntime.class, resources -> ServerRuntime.builder()
				.addConfig("cayenne-oncourse.xml")
				.addModule(new WillowCayenneModuleBuilder().build())
				.build()).eagerLoad();

		binder.bind(ICayenneService.class, new BinderFunctions.CayenneServiceBuilder()).eagerLoad();
		binder.bind(CacheManager.class, new BinderFunctions.CacheManagerBuilder());
		binder.bind(ISearchService.class, SearchService.class);

		binder.bind(ITagService.class, TagService.class);
		binder.bind(IBinaryDataService.class, BinaryDataService.class);
		binder.bind(ICourseClassService.class, CourseClassService.class);
		binder.bind(ICourseService.class, CourseService.class);

		binder.bind(ITrainingComponentService.class, new TrainingComponentServiceBuilder());
		binder.bind(IOrganisationService.class, new OrganisationServiceBuilder());
		binder.bind(INTISUpdater.class, NTISUpdaterImpl.class);
		binder.bind(IRequestCacheService.class, RequestCacheService.class);
		binder.bind(ThreadSource.class, ThreadSourceImpl.class);
		binder.bind(IRichtextConverter.class, RichtextConverter.class);
		binder.bind(IPageRenderer.class, PageRenderer.class);
	}
}
