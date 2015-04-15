package ish.oncourse.admin.services.billing;

import ish.oncourse.model.College;
import ish.oncourse.model.WebSite;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

public class PlanExportLineBuilderTest extends TestCase {
	
	private Map<Long, Map<Long, Map<String, Object>>> licenseData;
	private College college;
	
	private College createCollege() {
		return new College()
		{
			private WebSite webSite = new WebSite() {
				@Override
				public Long getId() {
					return 1L;
				}

				@Override
				public String getName() {
					return "Cherson";
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
	}
	
	@Before
	public void setUp() {
		this.college = createCollege();
		this.licenseData = new HashMap<>();
	}
	
	@Test
	public void testIsHostingBillingMonth() {
		Map<Long, Map<String,Object>> cData = new HashMap<>();

		this.licenseData.put(college.getId(), cData);
		cData.put(1L, new HashMap<String, Object>());
		
		Calendar calFrom = Calendar.getInstance();
		calFrom.set(Calendar.YEAR, 2013);
		calFrom.set(Calendar.MONTH, Calendar.JANUARY);

		WebSite webSite = college.getWebSites().get(0);

		HostingExportLineBuilder builder = new HostingExportLineBuilder(college, webSite, calFrom.getTime(), null, licenseData);
		assertFalse(builder.isPlanBillingMonth(HostingExportLineBuilder.HOSTING_PLAN_KEY, HostingExportLineBuilder.HOSTING_PAID_UNTIL_KEY));
		
		cData.get(webSite.getId()).put(HostingExportLineBuilder.HOSTING_PLAN_KEY, StockCodes.standard.name());
		cData.get(webSite.getId()).put(HostingExportLineBuilder.HOSTING_PLAN_TYPE, new BigDecimal(1234567.89d));
		
		assertTrue(builder.isPlanBillingMonth(HostingExportLineBuilder.HOSTING_PLAN_KEY, HostingExportLineBuilder.HOSTING_PAID_UNTIL_KEY));
		
		Calendar paidUntil = Calendar.getInstance();
		paidUntil.set(Calendar.YEAR, 2013);
		paidUntil.set(Calendar.MONTH, Calendar.NOVEMBER);
		
		cData.get(webSite.getId()).put(HostingExportLineBuilder.HOSTING_PAID_UNTIL_KEY, paidUntil.getTime());
		
		assertFalse(builder.isPlanBillingMonth(HostingExportLineBuilder.HOSTING_PLAN_KEY, HostingExportLineBuilder.HOSTING_PAID_UNTIL_KEY));
		
		paidUntil.set(Calendar.YEAR, 2012);
		cData.get(webSite.getId()).put(HostingExportLineBuilder.HOSTING_PAID_UNTIL_KEY, paidUntil.getTime());
		
		assertTrue(builder.isPlanBillingMonth(HostingExportLineBuilder.HOSTING_PLAN_KEY, HostingExportLineBuilder.HOSTING_PAID_UNTIL_KEY));
	}
	
	@Test
	public void testIsSupportBillingMonth() {
		Map<Long, Map<String,Object>> cData = new HashMap<>();
		
		this.licenseData.put(college.getId(), cData);
		cData.put(1L, new HashMap<String, Object>());
		
		Calendar calFrom = Calendar.getInstance();
		calFrom.set(Calendar.YEAR, 2013);
		calFrom.set(Calendar.MONTH, Calendar.JANUARY);

		WebSite webSite = college.getWebSites().get(0);
		
		SupportExportLineBuilder builder = new SupportExportLineBuilder(college, webSite, calFrom.getTime(), null, licenseData);
		assertFalse(builder.isPlanBillingMonth(SupportExportLineBuilder.SUPPORT_PLAN_KEY, SupportExportLineBuilder.SUPPORT_PAID_UNTIL_KEY));
		
		cData.get(webSite.getId()).put(SupportExportLineBuilder.SUPPORT_PLAN_KEY, StockCodes.standard.name());
		cData.get(webSite.getId()).put(SupportExportLineBuilder.SUPPORT_PLAN_TYPE, new BigDecimal(1234567.89d));
		
		assertTrue(builder.isPlanBillingMonth(SupportExportLineBuilder.SUPPORT_PLAN_KEY, SupportExportLineBuilder.SUPPORT_PAID_UNTIL_KEY));
		
		Calendar paidUntil = Calendar.getInstance();
		paidUntil.set(Calendar.YEAR, 2013);
		paidUntil.set(Calendar.MONTH, Calendar.NOVEMBER);
		
		cData.get(webSite.getId()).put(SupportExportLineBuilder.SUPPORT_PAID_UNTIL_KEY, paidUntil.getTime());
		
		assertFalse(builder.isPlanBillingMonth(SupportExportLineBuilder.SUPPORT_PLAN_KEY, SupportExportLineBuilder.SUPPORT_PAID_UNTIL_KEY));
		
		paidUntil.set(Calendar.YEAR, 2013);
		paidUntil.set(Calendar.MONTH, Calendar.JANUARY);
		cData.get(webSite.getId()).put(SupportExportLineBuilder.SUPPORT_PAID_UNTIL_KEY, paidUntil.getTime());
		
		assertTrue(builder.isPlanBillingMonth(SupportExportLineBuilder.SUPPORT_PLAN_KEY, SupportExportLineBuilder.SUPPORT_PAID_UNTIL_KEY));
	}

}
