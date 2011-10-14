package ish.oncourse.admin.services.billing;

import java.util.Date;
import java.util.Map;

public interface IBillingDataService {
	
	Map<Long, Map<String, Object>> getLicenseFeeData();

	Map<Long, Map<String, Object>> getBillingData(Date from, Date to);

	void createInvoices();
}
