package ish.oncourse.admin.services.billing;

import ish.oncourse.admin.services.billing.StockCodes;
import ish.oncourse.model.College;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class BillingDataServiceImplTest {

	@Test
	public void testHostingFormat()
	{
		String original = "DI\tCHERSON01\tOCW-21\tonCourse standard hosting plan for Cherson: March, 2012 (contract until March, 2013)\t1\t1234567.890\tonCourse March, 2012\n";
		Map<Long, Map<String, Object>> licenseData = new HashMap<Long, Map<String, Object>>();
		Map<String,Object> cData = new HashMap<String, Object>();
		cData.put(HostingExportLineBuilder.HOSTING_PLAN_KEY, StockCodes.standard.name());
		cData.put(HostingExportLineBuilder.HOSTING_PLAN_TYPE, new BigDecimal(1234567.89d));
		licenseData.put(1L, cData);

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

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2012);
		calendar.set(Calendar.MONTH, 2);

		Calendar calendarRenewalMonth = Calendar.getInstance();
		calendarRenewalMonth.set(Calendar.YEAR, 2013);
		calendarRenewalMonth.set(Calendar.MONTH, 2);
		cData.put(HostingExportLineBuilder.HOSTING_RENEWAL_DATE_KEY, calendarRenewalMonth.getTime());
		
		String result = new HostingExportLineBuilder(college, calendar.getTime(), null, licenseData).buildLine(); 
		
		assertEquals("Check result message", result,original);
	}

	@Test
	public void testSupportFormat()
	{
		String original = "DI\tCHERSON01\tOCW-21\tonCourse standard support plan for Cherson: March, 2012 (contract until March, 2013)\t1\t1234567.890\tonCourse March, 2012\n";
		Map<Long, Map<String, Object>> licenseData = new HashMap<Long, Map<String, Object>>();

		Map<String,Object> cData = new HashMap<String, Object>();
		cData.put(SupportExportLineBuilder.SUPPORT_PLAN_KEY, StockCodes.standard.name());
		cData.put(SupportExportLineBuilder.SUPPORT_PLAN_TYPE, new BigDecimal(1234567.89d));
		licenseData.put(1L, cData);

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

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2012);
		calendar.set(Calendar.MONTH, 2);

		Calendar calendarRenewalMonth = Calendar.getInstance();
		calendarRenewalMonth.set(Calendar.YEAR, 2013);
		calendarRenewalMonth.set(Calendar.MONTH, 2);
		cData.put(SupportExportLineBuilder.SUPPORT_RENEWAL_DATE_KEY, calendarRenewalMonth.getTime());
		
		String result = new SupportExportLineBuilder(college, calendar.getTime(), null, licenseData).buildLine(); 
		
		assertEquals("Check result message", result,original);
	}
}
