package ish.oncourse.cms.services;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;

import ish.oncourse.cms.services.access.AuthenticationStatus;
import ish.oncourse.cms.services.access.IAuthenticationService;
import ish.oncourse.model.College;
import ish.oncourse.model.Tag;
import ish.oncourse.model.WebHostName;
import ish.oncourse.model.WebMenu;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.WillowUser;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.environment.IEnvironmentService;
import ish.oncourse.services.jndi.ILookupService;
import ish.oncourse.services.menu.IWebMenuService;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.services.property.IPropertyService;
import ish.oncourse.services.property.Property;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.RequestHandler;
import org.apache.tapestry5.services.Response;

public class TestModule {

	public RequestFilter buildLogFilterOverride(org.slf4j.Logger log, RequestGlobals requestGlobals) {
		return new RequestFilter() {
			public boolean service(Request request, Response response, RequestHandler handler) throws IOException {
				return handler.service(request, response);
			}
		};
	}

	public void contributeRequestHandler(OrderedConfiguration<RequestFilter> configuration, @Local RequestFilter logFilter) {
		configuration.override("LogFilter", logFilter);
	}

	public ILookupService buildLookupServiceOverride() {
		ILookupService mockService = mock(ILookupService.class);
		return mockService;
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
			@Local ILookupService lookupServiceOverride) {
		configuration.add(ILookupService.class, lookupServiceOverride);
	}

	public IPropertyService buildPropertyServiceOverride() {
		IPropertyService mockService = mock(IPropertyService.class);

		when(mockService.string(eq(Property.CustomComponentsPath))).thenReturn("/var/onCourse/resources");

		return mockService;
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
			@Local IPropertyService propServiceOverride) {
		configuration.add(IPropertyService.class, propServiceOverride);
	}

	public IWebMenuService buildWebMenuServiceOverride() {
		IWebMenuService mockService = mock(IWebMenuService.class);

		WebMenu menu = mock(WebMenu.class);

		when(menu.getName()).thenReturn("RootItem");
		when(menu.getUrl()).thenReturn("http://google.com");

		when(mockService.getRootMenu()).thenReturn(menu);

		return mockService;
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
			@Local IWebMenuService menuServiceOverride) {
		configuration.add(IWebMenuService.class, menuServiceOverride);
	}

	public ITagService buildTagServiceOverride() {
		ITagService mockService = mock(ITagService.class, RETURNS_DEEP_STUBS);

		Tag tag = mock(Tag.class);

		when(tag.getName()).thenReturn("Subjects");

		when(mockService.getRootTag().getWebVisibleTags()).thenReturn(Arrays.asList(tag));

		return mockService;
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
			@Local ITagService tagServiceOverride) {
		configuration.add(ITagService.class, tagServiceOverride);
	}

	public static IEnvironmentService buildEnvironmentServiceOverride() {
		IEnvironmentService mockService = mock(IEnvironmentService.class);

		when(mockService.getApplicationName()).thenReturn("onCourse");
		when(mockService.getBuildServerID()).thenReturn("");
		when(mockService.getCiVersion()).thenReturn("");
		when(mockService.getScmVersion()).thenReturn("");

		return mockService;
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
			@Local IEnvironmentService envServiceOverride) {
		configuration.add(IEnvironmentService.class, envServiceOverride);
	}

	public static IWebNodeTypeService buildWebNodeTypeServiceOverride() {
		IWebNodeTypeService mockService = mock(IWebNodeTypeService.class);

		WebNodeType type = mock(WebNodeType.class);
		when(type.getLayoutKey()).thenReturn("default");
		when(type.getName()).thenReturn("page");

		when(mockService.getDefaultWebNodeType()).thenReturn(type);
		when(mockService.getWebNodeTypes()).thenReturn(Arrays.asList(type));

		return mockService;
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
			@Local IWebNodeTypeService webNodeTypeServiceOverride) {
		configuration.add(IWebNodeTypeService.class, webNodeTypeServiceOverride);
	}

	public static IWebSiteService buildWebSiteServiceOverride() {

		WebSite webSite = mock(WebSite.class);

		when(webSite.getName()).thenReturn("Sydney Community College Test Site");
		when(webSite.getSiteKey()).thenReturn("scc");

		College college = mock(College.class);

		when(college.getWebSites()).thenReturn(Arrays.asList(webSite));
		when(webSite.getCollege()).thenReturn(college);

		WebHostName host = mock(WebHostName.class);
		when(host.getName()).thenReturn("scc.test1.oncourse.net.au");
		when(host.getWebSite()).thenReturn(webSite);

		IWebSiteService mockService = mock(IWebSiteService.class);

		when(mockService.getCurrentCollege()).thenReturn(college);
		when(mockService.getCurrentDomain()).thenReturn(host);
		when(mockService.getCurrentWebSite()).thenReturn(webSite);

		return mockService;
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
			@Local IWebSiteService webSiteServiceOverride) {
		configuration.add(IWebSiteService.class, webSiteServiceOverride);
	}

	public static IAuthenticationService buildAuthenticationServiceOverride() {

		IAuthenticationService mockService = mock(IAuthenticationService.class);

		when(mockService.authenticate("test@right.com", "rpasswd")).thenReturn(AuthenticationStatus.SUCCESS);
		when(mockService.authenticate("test@right.com", "wpasswd")).thenReturn(AuthenticationStatus.INVALID_CREDENTIALS);
		when(mockService.authenticate("test@wrong.com", "rpasswd")).thenReturn(AuthenticationStatus.NO_MATCHING_USER);
		when(mockService.authenticate("test@dups.com", "rpasswd")).thenReturn(AuthenticationStatus.MORE_THAN_ONE_USER);

		Date today = new Date();

		WillowUser user = new WillowUser();
		user.setAngelId(1L);
		user.setCreated(today);
		user.setFirstName("Test firstname");
		user.setEmail("test@test.com");
		user.setIsActive(true);
		user.setModified(today);
		user.setPassword("12345");
		user.setPasswordHash("12345");

		when(mockService.getUser()).thenReturn(user);

		return mockService;
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
			@Local IAuthenticationService authServiceOverride) {
		configuration.add(IAuthenticationService.class, authServiceOverride);
	}

	public ICayenneService buildCayenneServiceOverride() {
		ICayenneService mock = mock(ICayenneService.class);
		return mock;
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
			@Local ICayenneService cayenneServiceOverride) {
		configuration.add(ICayenneService.class, cayenneServiceOverride);
	}
}
