package ish.oncourse.ui.services;

public class TapestryOverrideModule {

//	public PageLoader buildPageLoaderOverride(@Autobuild PageLoaderOverride service, @ComponentTemplates InvalidationEventHub templatesHub,
//											  @ComponentMessages InvalidationEventHub messagesHub, @ComponentClasses InvalidationEventHub classesInvalidationEventHub,
//											  IWebNodeService webNodeService,
//											  Request request) {
//
////		service.setRequest(request);
////		service.setWebNodeService(webNodeService);
//		return service;
//	}

//	public PageSource buildPageSourceOverride(PageLoader pageLoader, @ComponentTemplates InvalidationEventHub templatesHub,
//											  @ComponentMessages InvalidationEventHub messagesHub, @ComponentClasses InvalidationEventHub classesInvalidationEventHub,
//											  IWebNodeService webNodeService, IWebSiteVersionService webSiteVersionService, PageLoadService webCacheService,
//											  Request request) {
//
////		PageSourceOverride service = new PageSourceOverride(pageLoader, webNodeService, request, webSiteVersionService, webCacheService);
//		return null;
//	}
//
//	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
//										  @Local ComponentTemplateSource componentTemplateSourceOverride) {
//		configuration.add(ComponentTemplateSource.class, componentTemplateSourceOverride);
//	}
//
//	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local PageLoader override) {
//		configuration.add(PageLoader.class, override);
//	}
//
//	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local PageSource override) {
//		configuration.add(PageSource.class, override);
//	}
//
//	public void contributeComponentClassResolver(Configuration<LibraryMapping> configuration) {
//		configuration.add(new LibraryMapping("ui", "ish.oncourse.ui"));
//	}
}
