package ish.oncourse.admin.services.billing;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import ish.oncourse.model.College;

public class PlanExportLineBuilderTest extends TestCase {
	
	private Map<Long, Map<String, Object>> licenseData;
	private College college;
	
	private College createCollege() {
		College college = new College()
		{
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
		};
		
		return college;
	}
	
	@Before
	public void setUp() {
		this.college = createCollege();
		this.licenseData = new HashMap<Long, Map<String,Object>>();
	}
	
	@Test
	public void testIsHostingBillingMonth() {
		Map<String,Object> cData = new HashMap<String, Object>();
		
		this.licenseData.put(college.getId(), cData);
		
		Calendar calFrom = Calendar.getInstance();
		calFrom.set(Calendar.YEAR, 2013);
		calFrom.set(Calendar.MONTH, Calendar.JANUARY);
		
		HostingExportLineBuilder builder = new HostingExportLineBuilder(college, calFrom.getTime(), null, licenseData);
		assertFalse(builder.isPlanBillingMonth(HostingExportLineBuilder.HOSTING_PLAN_KEY, HostingExportLineBuilder.HOSTING_PAID_UNTIL_KEY));
		
		cData.put(HostingExportLineBuilder.HOSTING_PLAN_KEY, StockCodes.standard.name());
		cData.put(HostingExportLineBuilder.HOSTING_PLAN_TYPE, new BigDecimal(1234567.89d));
		
		assertTrue(builder.isPlanBillingMonth(HostingExportLineBuilder.HOSTING_PLAN_KEY, HostingExportLineBuilder.HOSTING_PAID_UNTIL_KEY));
		
		Calendar paidUntil = Calendar.getInstance();
		paidUntil.set(Calendar.YEAR, 2013);
		paidUntil.set(Calendar.MONTH, Calendar.NOVEMBER);
		
		cData.put(HostingExportLineBuilder.HOSTING_PAID_UNTIL_KEY, paidUntil.getTime());
		
		assertFalse(builder.isPlanBillingMonth(HostingExportLineBuilder.HOSTING_PLAN_KEY, HostingExportLineBuilder.HOSTING_PAID_UNTIL_KEY));
		
		paidUntil.set(Calendar.YEAR, 2012);
		cData.put(HostingExportLineBuilder.HOSTING_PAID_UNTIL_KEY, paidUntil.getTime());
		
		assertTrue(builder.isPlanBillingMonth(HostingExportLineBuilder.HOSTING_PLAN_KEY, HostingExportLineBuilder.HOSTING_PAID_UNTIL_KEY));
	}
	
	@Test
	public void testIsSupportBillingMonth() {
		Map<String,Object> cData = new HashMap<String, Object>();
		
		this.licenseData.put(college.getId(), cData);
		
		Calendar calFrom = Calendar.getInstance();
		calFrom.set(Calendar.YEAR, 2013);
		calFrom.set(Calendar.MONTH, Calendar.JANUARY);
		
		SupportExportLineBuilder builder = new SupportExportLineBuilder(college, calFrom.getTime(), null, licenseData);
		assertFalse(builder.isPlanBillingMonth(SupportExportLineBuilder.SUPPORT_PLAN_KEY, SupportExportLineBuilder.SUPPORT_PAID_UNTIL_KEY));
		
		cData.put(SupportExportLineBuilder.SUPPORT_PLAN_KEY, StockCodes.standard.name());
		cData.put(SupportExportLineBuilder.SUPPORT_PLAN_TYPE, new BigDecimal(1234567.89d));
		
		assertTrue(builder.isPlanBillingMonth(SupportExportLineBuilder.SUPPORT_PLAN_KEY, SupportExportLineBuilder.SUPPORT_PAID_UNTIL_KEY));
		
		Calendar paidUntil = Calendar.getInstance();
		paidUntil.set(Calendar.YEAR, 2013);
		paidUntil.set(Calendar.MONTH, Calendar.NOVEMBER);
		
		cData.put(SupportExportLineBuilder.SUPPORT_PAID_UNTIL_KEY, paidUntil.getTime());
		
		assertFalse(builder.isPlanBillingMonth(SupportExportLineBuilder.SUPPORT_PLAN_KEY, SupportExportLineBuilder.SUPPORT_PAID_UNTIL_KEY));
		
		paidUntil.set(Calendar.YEAR, 2013);
		paidUntil.set(Calendar.MONTH, Calendar.JANUARY);
		cData.put(SupportExportLineBuilder.SUPPORT_PAID_UNTIL_KEY, paidUntil.getTime());
		
		assertTrue(builder.isPlanBillingMonth(SupportExportLineBuilder.SUPPORT_PLAN_KEY, SupportExportLineBuilder.SUPPORT_PAID_UNTIL_KEY));
	}

}
