package ish.oncourse.ui.services;

import ish.oncourse.services.search.SearchService;
import ish.oncourse.services.visitor.ParsedContentVisitor;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.ImportModule;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@ImportModule({UIModule.class})
public class TestModule {

	public static final String TEST_COLLEGE_ANGEL_VERSION_PROPERTY = "oncourse.test.server.angelversion";

	public static void bind(ServiceBinder binder) {
		ish.oncourse.test.tapestry.TestModule.bind(binder);

//		binder.bind(IResourceService.class, resources -> {
//			IResourceService mock = mock(IResourceService.class);
//			when(mock.getDbTemplateResource(any(WebSiteLayout.class), Matchers.anyString())).thenReturn(null);
//			return mock;
//		});

	}

	public static void contributeApplicationDefaults(
			MappedConfiguration<String, String> configuration) {
		configuration.add(SymbolConstants.COMPACT_JSON, "false");
		configuration.add(SymbolConstants.COMPRESS_WHITESPACE, "false");
		configuration.add(SearchService.ALIAS_SUFFIX_PROPERTY, EMPTY);
		configuration.add(ParsedContentVisitor.WEB_CONTENT_CACHE, "false");
	}

//	public RequestFilter buildLogFilterOverride(org.slf4j.Logger log, RequestGlobals requestGlobals) {
//		return new RequestFilter() {
//			public boolean service(Request request, Response response, RequestHandler handler) throws IOException {
//				return handler.service(request, response);
//			}
//		};
//	}
//
//	public void contributeRequestHandler(OrderedConfiguration<RequestFilter> configuration,
//										 @Local RequestFilter logFilter) {
//		configuration.override("LogFilter", logFilter);
//	}
//
//	public IPropertyService buildPropertyServiceOverride() {
//		IPropertyService mockService = mock(IPropertyService.class);
//
//		when(mockService.string(eq(Property.CustomComponentsPath))).thenReturn("src/test/resources");
//
//		return mockService;
//	}
//
//	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
//										  @Local IPropertyService propServiceOverride) {
//		configuration.add(IPropertyService.class, propServiceOverride);
//	}
//
//	public static IEnvironmentService buildEnvironmentServiceOverride() {
//		IEnvironmentService mockService = mock(IEnvironmentService.class);
//
//		when(mockService.getApplicationName()).thenReturn("onCourse");
//		when(mockService.getBuildServerID()).thenReturn("");
//		when(mockService.getCiVersion()).thenReturn("");
//		when(mockService.getScmVersion()).thenReturn("");
//
//		return mockService;
//	}
//
//	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
//										  @Local IEnvironmentService envServiceOverride) {
//		configuration.add(IEnvironmentService.class, envServiceOverride);
//	}
//
//	public static IWebNodeTypeService buildWebNodeTypeServiceOverride() {
//		IWebNodeTypeService mockService = mock(IWebNodeTypeService.class);
//
//		WebNodeType type = mock(WebNodeType.class);
//		WebSiteLayout layout = mock(WebSiteLayout.class);
//		when(layout.getLayoutKey()).thenReturn("default");
//
//		when(type.getWebSiteLayout()).thenReturn(layout);
//		when(type.getName()).thenReturn("page");
//
//		when(mockService.getDefaultWebNodeType()).thenReturn(type);
//		when(mockService.getWebNodeTypes()).thenReturn(Arrays.asList(type));
//
//		return mockService;
//	}
//
//	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
//										  @Local IWebNodeTypeService webNodeTypeServiceOverride) {
//		configuration.add(IWebNodeTypeService.class, webNodeTypeServiceOverride);
//	}
//
//	public static IWebSiteService buildWebSiteServiceOverride() {
//
//		WebSite webSite = mock(WebSite.class);
//
//		when(webSite.getName()).thenReturn("Sydney Community College Test Site");
//		when(webSite.getSiteKey()).thenReturn("scc");
//		when(webSite.getResourceFolderName()).thenReturn("default");
//
//		College college = mock(College.class);
//
//		when(college.getWebSites()).thenReturn(Arrays.asList(webSite));
//		when(webSite.getCollege()).thenReturn(college);
//		when(webSite.getCollege().getAngelVersion()).thenReturn(System.getProperty(TEST_COLLEGE_ANGEL_VERSION_PROPERTY));
//
//		WebHostName host = mock(WebHostName.class);
//		when(host.getName()).thenReturn("scc.oncourse.cc");
//		when(host.getWebSite()).thenReturn(webSite);
//
//		IWebSiteService mockService = mock(IWebSiteService.class);
//
//		when(mockService.getCurrentCollege()).thenReturn(college);
//		when(mockService.getCurrentWebSite()).thenReturn(webSite);
//
//		return mockService;
//	}
//
//	public static IWebSiteVersionService buildWebSiteVersionServiceOverride() {
//
//		WebSite webSite = mock(WebSite.class);
//
//		when(webSite.getName()).thenReturn("Sydney Community College Test Site");
//		when(webSite.getSiteKey()).thenReturn("scc");
//		when(webSite.getResourceFolderName()).thenReturn("default");
//
//		College college = mock(College.class);
//
//		when(college.getWebSites()).thenReturn(Arrays.asList(webSite));
//
//		when(webSite.getCollege()).thenReturn(college);
//		when(webSite.getCollege().getAngelVersion()).thenReturn(System.getProperty(TEST_COLLEGE_ANGEL_VERSION_PROPERTY));
//
//		WebSiteVersion webSiteVersion = mock(WebSiteVersion.class);
//
//		when(webSiteVersion.getWebSite()).thenReturn(webSite);
//
//		IWebSiteVersionService mockVersionService = mock(IWebSiteVersionService.class);
//
//		when(mockVersionService.getCurrentVersion()).thenReturn(webSiteVersion);
//
//		return mockVersionService;
//	}
//
//	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
//										  @Local IWebSiteService webSiteServiceOverride) {
//		configuration.add(IWebSiteService.class, webSiteServiceOverride);
//	}
//
//	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
//										  @Local IWebSiteVersionService webSiteVersionServiceOverride) {
//		configuration.add(IWebSiteVersionService.class, webSiteVersionServiceOverride);
//	}
//
//	@SuppressWarnings("unchecked")
////	public ICourseClassService buildCourseClassServiceOverride() {
////		ICourseClassService mock = mock(ICourseClassService.class);
////
////		List<Long> ids = new ArrayList<>(1);
////		ids.add(1L);
////		List<CourseClass> courseClasses = new ArrayList<>(1);
////
////
////		ObjectContext context = null;
////		Site site = context.newObject(Site.class);
////		site.setIsWebVisible(true);
////		Site site1 = context.newObject(Site.class);
////		site1.setIsWebVisible(true);
////		Course course = context.newObject(Course.class);
////		course.setCode("course");
////		CourseClass courseClass = context.newObject(CourseClass.class);
////		courseClass.setCode("class");
////		courseClass.setCourse(course);
////
////		Session session1 = context.newObject(Session.class);
////		session1.setStartDate(new Date());
////		session1.setEndDate(new Date());
////		Room room1 = context.newObject(Room.class);
////		room1.setName(TimelineDataTest.ROOM1);
////		room1.setSite(site);
////		session1.setRoom(room1);
////		session1.setTimeZone("GMT");
////		courseClass.addToSessions(session1);
////
////		Session session2 = context.newObject(Session.class);
////		session2.setStartDate(new Date());
////		session2.setEndDate(new Date());
////		Room room2 = context.newObject(Room.class);
////		room2.setName(TimelineDataTest.ROOM2);
////		room2.setSite(site1);
////
////		session2.setRoom(room2);
////		session2.setTimeZone("GMT");
////		courseClass.addToSessions(session2);
////		Session session3 = context.newObject(Session.class);
////		session3.setStartDate(new Date());
////		session3.setEndDate(new Date());
////		session3.setTimeZone("GMT");
////		courseClass.addToSessions(session3);
////
////		courseClasses.add(courseClass);
////		when(mock.loadByIds(anyList())).thenReturn(courseClasses);
////		return mock;
////	}
//
//	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
//										  @Local ICourseClassService courseClassServiceOverride) {
//		configuration.add(ICourseClassService.class, courseClassServiceOverride);
//	}
//
//	public ICayenneService buildCayenneServiceOverride() {
//		ICayenneService mock = mock(ICayenneService.class);
//		ObjectContext mockContext = mock(ObjectContext.class);
//
//		when(mockContext.performQuery(any(SelectQuery.class))).thenReturn(Collections.EMPTY_LIST);
//		when(mock.newContext()).thenReturn(mockContext);
//		return mock;
//	}
//
//	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
//										  @Local ICayenneService cayenneServiceOverride) {
//		configuration.add(ICayenneService.class, cayenneServiceOverride);
//	}
//
//	public PreferenceController buildPreferenceControllerOverride() {
//		PreferenceController mock = mock(PreferenceController.class);
//		when(mock.getEmailFromAddress()).thenReturn("somevalidmail@gmail.com");
//		return mock;
//	}
//
//	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
//										  @Local PreferenceController preferenceController) {
//		configuration.add(PreferenceController.class, preferenceController);
//	}
//
//	@SuppressWarnings("unchecked")
//	public ITagService buildITagServiceOverride() {
//		ITagService mock = mock(ITagService.class);
//		Tag tag = mock(Tag.class);
//		when(tag.getWebVisibleTags()).thenReturn(Collections.EMPTY_LIST);
//		when(mock.getSubjectsTag()).thenReturn(tag);
//		return mock;
//	}
//
//	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
//										  @Local ITagService tagServiceOverride) {
//		configuration.add(ITagService.class, tagServiceOverride);
//	}
//
//	@SuppressWarnings("unchecked")
//	public IWebMenuService buildIWebMenuServiceOverride() {
//		IWebMenuService mock = mock(IWebMenuService.class);
//		WebMenu webMenu = mock(WebMenu.class);
//		when(mock.getRootMenu()).thenReturn(webMenu);
//		return mock;
//	}
//
//
//	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
//										  @Local IResourceService resourceServiceOverride) {
//		configuration.add(IResourceService.class, resourceServiceOverride);
//	}
//
//	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
//										  @Local IWebMenuService webMenuServiceOverride) {
//		configuration.add(IWebMenuService.class, webMenuServiceOverride);
//	}
//
//	public IVoucherService buildIVoucherServiceOverride(IWebSiteService webSiteService, ICayenneService cayenneService) {
//		IVoucherService service = new VoucherServiceOverride(webSiteService, cayenneService);
//		return service;
//	}
//
//	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
//										  @Local IVoucherService voucherService) {
//		configuration.add(IVoucherService.class, voucherService);
//	}
//
//	private class VoucherServiceOverride extends VoucherService {
//		private VoucherServiceOverride() {
//			super();
//		}
//
//		private VoucherServiceOverride(IWebSiteService webSiteService, ICayenneService cayenneService) {
//			super(webSiteService, cayenneService);
//		}
//
//		@SuppressWarnings("unchecked")
//		@Override
//		public List<Product> getAvailableProducts(Integer startDefault, Integer rowsDefault) {
//			return Collections.EMPTY_LIST;
//		}
//	}

}
