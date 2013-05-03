package ish.oncourse.admin.services.billing;

import ish.oncourse.model.College;
import ish.oncourse.model.WebSite;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class BillingDataServiceImplTest {

	@Test
	public void testHostingFormat()
	{
		String original = "DI\tCHERSON01\tOCW-21\tCherson Website standard web plan: 1 month (contract until March, 2013)\t1\t1234567.890\tonCourse March, 2012\t31/03/2012\n";
		Map<Long, Map<Long, Map<String, Object>>> licenseData = new HashMap<>();
		Map<Long, Map<String,Object>> cData = new HashMap<>();
		cData.put(1L, new HashMap<String, Object>());
		cData.get(1L).put(HostingExportLineBuilder.HOSTING_PLAN_KEY, StockCodes.standard.name());
		cData.get(1L).put(HostingExportLineBuilder.HOSTING_PLAN_TYPE, new BigDecimal(1234567.89d));
		licenseData.put(1L, cData);

		College college = new College()
		{
			private WebSite webSite = new WebSite() {
				@Override
				public Long getId() {
					return 1L;
				}

				@Override
				public String getName() {
					return "Cherson Website";
				}

				@Override
				public String getSiteKey() {
					return "chers";
				}
			};

			@Override
			public Long getId() {
				return 1L;
			}

			@Override
			public String getBillingCode() {
				return "CHERSON01";
			}
			
			@Override
			public String getName() {
				return "Cherson";
			}

			@Override
			public List<WebSite> getWebSites() {
				return Arrays.asList(webSite);
			}
		};

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2012);
		calendar.set(Calendar.MONTH, 2);

		Calendar calendarRenewalMonth = Calendar.getInstance();
		calendarRenewalMonth.set(Calendar.YEAR, 2013);
		calendarRenewalMonth.set(Calendar.MONTH, 2);
		cData.get(1L).put(HostingExportLineBuilder.HOSTING_RENEWAL_DATE_KEY, calendarRenewalMonth.getTime());

		String result = "";

		for (WebSite webSite : college.getWebSites()) {
			result += new HostingExportLineBuilder(college, webSite, calendar.getTime(), null, licenseData).buildLine();
		}
		
		assertEquals("Check result message", result,original);
	}

	@Test
	public void testSupportFormat()
	{
		String original = "DI\tCHERSON01\tOCW-21\tStandard plan for Cherson: 1 month (contract until March, 2013)\t1\t1234567.890\tonCourse March, 2012\t31/03/2012\n";
		Map<Long, Map<Long, Map<String, Object>>> licenseData = new HashMap<>();
		Map<Long, Map<String,Object>> cData = new HashMap<>();
		cData.put(1L, new HashMap<String, Object>());
		cData.get(1L).put(SupportExportLineBuilder.SUPPORT_PLAN_KEY, StockCodes.standard.name());
		cData.get(1L).put(SupportExportLineBuilder.SUPPORT_PLAN_TYPE, new BigDecimal(1234567.89d));
		licenseData.put(1L, cData);

		College college = new College()
		{
			private WebSite webSite = new WebSite() {
				@Override
				public Long getId() {
					return 1L;
				}

				@Override
				public String getName() {
					return "Cherson Website";
				}

				@Override
				public String getSiteKey() {
					return "chers";
				}
			};

			@Override
			public Long getId() {
				return 1L;
			}

			@Override
			public String getBillingCode() {
				return "CHERSON01";
			}

			@Override
			public String getName() {
				return "Cherson";
			}

			@Override
			public List<WebSite> getWebSites() {
				return Arrays.asList(webSite);
			}
		};

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2012);
		calendar.set(Calendar.MONTH, 2);

		Calendar calendarRenewalMonth = Calendar.getInstance();
		calendarRenewalMonth.set(Calendar.YEAR, 2013);
		calendarRenewalMonth.set(Calendar.MONTH, 2);
		cData.get(1L).put(SupportExportLineBuilder.SUPPORT_RENEWAL_DATE_KEY, calendarRenewalMonth.getTime());

		String result = "";

		for (WebSite webSite : college.getWebSites()) {
			result += new SupportExportLineBuilder(college, webSite, calendar.getTime(), null, licenseData).buildLine();
		}
		
		assertEquals("Check result message", result,original);
	}
}
