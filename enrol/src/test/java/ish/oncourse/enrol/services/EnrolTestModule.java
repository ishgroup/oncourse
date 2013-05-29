package ish.oncourse.enrol.services;

import ish.oncourse.enrol.services.concessions.ConcessionsService;
import ish.oncourse.enrol.services.concessions.IConcessionsService;
import ish.oncourse.enrol.services.invoice.IInvoiceProcessingService;
import ish.oncourse.enrol.services.invoice.InvoiceProcessingService;
import ish.oncourse.enrol.services.payment.IPurchaseControllerBuilder;
import ish.oncourse.enrol.services.payment.PurchaseControllerBuilder;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.enrol.services.student.StudentService;
import ish.oncourse.model.College;
import ish.oncourse.model.WebHostName;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.services.ModelModule;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.cookies.CookiesService;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.property.IPropertyService;
import ish.oncourse.services.property.Property;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.ui.services.UIModule;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.services.*;

import java.io.IOException;
import java.util.HashMap;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SubModule({ ModelModule.class, ServiceModule.class,  UIModule.class })
public class EnrolTestModule {
	
	public static void bind(ServiceBinder binder) {
		binder.bind(IConcessionsService.class, ConcessionsService.class);
		binder.bind(IStudentService.class, StudentService.class);
		binder.bind(IInvoiceProcessingService.class, InvoiceProcessingService.class);
		
		binder.bind(IWebSiteService.class, WebSiteServiceOverride.class).withId("testWebSiteService");
		binder.bind(ICookiesService.class, CookiesServiceOverride.class).withId("testCookiesService");
		binder.bind(IPurchaseControllerBuilder.class, PurchaseControllerBuilder.class);
	}

	public RequestFilter buildLogFilterOverride(org.slf4j.Logger log, RequestGlobals requestGlobals) {
		return new RequestFilter() {
			public boolean service(Request request, Response response, RequestHandler handler) throws IOException {
				return handler.service(request, response);
			}
		};
	}

	public void contributeRequestHandler(OrderedConfiguration<RequestFilter> configuration,
										 @Local RequestFilter logFilter) {
		configuration.override("LogFilter", logFilter);
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

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local IWebSiteService webSiteService) {
		configuration.add(IWebSiteService.class, webSiteService);
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local ICookiesService cookiesService) {
		configuration.add(ICookiesService.class, cookiesService);
	}


	public static class WebSiteServiceOverride implements IWebSiteService {
		
		@Inject
		private ICollegeService collegeService;

		@Override
		public WebSite getCurrentWebSite() {
			return getCurrentCollege().getWebSites().get(0);
		}

		@Override
		public College getCurrentCollege() {
			return collegeService.findBySecurityCode("345ttn44$%9");
		}

		@Override
		public WebHostName getCurrentDomain() {
			return getCurrentWebSite().getToWebHostName();
		}
	}

	public static class CookiesServiceOverride extends CookiesService
	{
		private HashMap<String, String> cookies = new HashMap<>();

		@Override
		public String getCookieValue(String cookieKey) {
			return cookies.get(cookieKey);
		}

		@Override
		public void writeCookieValue(String cookieKey, String cookieValue) {
			cookies.put(cookieKey,cookieValue);
		}


		@Override
		public void removeCookieValue(String name) {
			cookies.remove(name);
		}
	}
}
