package ish.oncourse.cms.services;

import ish.oncourse.model.*;
import ish.oncourse.model.services.ModelModule;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.environment.IEnvironmentService;
import ish.oncourse.services.jndi.ILookupService;
import ish.oncourse.services.menu.IWebMenuService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.property.IPropertyService;
import ish.oncourse.services.property.Property;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.ui.services.UIModule;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.services.*;

import java.io.IOException;
import java.util.Arrays;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;


@SubModule({ ModelModule.class, ServiceModule.class, UIModule.class })
public class TestModuleForContentStructure {
		/**
		 * Test BinaryInfo name.
		 */
		private static final String NAME_FOR_BINARY_INFO = "TEST";

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

			when(mockService.string(eq(Property.CustomComponentsPath))).thenReturn("src/test/resources");

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

			when(mockService.getSubjectsTag().getWebVisibleTags()).thenReturn(Arrays.asList(tag));

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
			when(webSite.getResourceFolderName()).thenReturn("default");

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

	

		public ICayenneService buildCayenneServiceOverride() {
			ICayenneService mock = mock(ICayenneService.class);
			return mock;
		}

		public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
				@Local ICayenneService cayenneServiceOverride) {
			configuration.add(ICayenneService.class, cayenneServiceOverride);
		}
		
		public IBinaryDataService buildBinaryDataServiceOverride() {
			IBinaryDataService mock = mock(IBinaryDataService.class);
			BinaryInfo binaryInfo = mock(BinaryInfo.class);
			when(binaryInfo.getName()).thenReturn(TestModuleForContentStructure.NAME_FOR_BINARY_INFO);

			when(mock.getBinaryInfo(BinaryInfo.NAME_PROPERTY,TestModuleForContentStructure.NAME_FOR_BINARY_INFO)).thenReturn(
					binaryInfo);

			return mock;
		}

		public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
				@Local IBinaryDataService binaryDataServiceOverride) {
			configuration.add(IBinaryDataService.class, binaryDataServiceOverride);
		}
		
		public IWebNodeService buildWebNodeOverride() {
			IWebNodeService mock = mock(IWebNodeService.class);
			return mock;
		}

		public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
				@Local IWebNodeService webNodeServiceOverride) {
			configuration.add(IWebNodeService.class, webNodeServiceOverride);
		}

}
