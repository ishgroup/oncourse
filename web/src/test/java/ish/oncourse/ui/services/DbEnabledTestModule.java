/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.services;

import ish.oncourse.model.College;
import ish.oncourse.model.Product;
import ish.oncourse.model.WebHostName;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.property.IPropertyService;
import ish.oncourse.services.property.Property;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.services.voucher.IVoucherService;
import ish.oncourse.services.voucher.VoucherService;
import ish.oncourse.util.CommonUtils;
import ish.oncourse.website.services.site.WebSiteVersionService;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.services.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SubModule({ ServiceModule.class, UIModule.class })

public class DbEnabledTestModule {

	public static final String TEST_COLLEGE_SECURIRY_CODE_PROPERTY = "oncourse.test.server.code";


	public static void bind(ServiceBinder binder) {
		binder.bind(IWebSiteService.class, WebSiteServiceOverride.class).withId("testWebSiteService");
		binder.bind(IWebSiteVersionService.class, WebSiteVersionService.class);
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

	public IVoucherService buildIVoucherServiceOverride(IWebSiteService webSiteService, ICayenneService cayenneService) {
		IVoucherService service = new VoucherServiceOverride(webSiteService, cayenneService);
		return service;
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
										  @Local IVoucherService voucherService) {
		configuration.add(IVoucherService.class, voucherService);
	}

	private class VoucherServiceOverride extends VoucherService {
		private VoucherServiceOverride() {
			super();
		}

		private VoucherServiceOverride(IWebSiteService webSiteService, ICayenneService cayenneService) {
			super(webSiteService, cayenneService);
		}
		@SuppressWarnings("unchecked")
		@Override
		public List<Product> getAvailableProducts(Integer startDefault, Integer rowsDefault) {
			return Collections.EMPTY_LIST;
		}


		@Override
		public boolean isAbleToPurchaseProductsOnline() {
			String angelVersion = System.getProperty(TestModule.TEST_COLLEGE_ANGEL_VERSION_PROPERTY);
			return CommonUtils.compare(angelVersion, CommonUtils.VERSION_5_0) >= 0;
		}
	}
}
