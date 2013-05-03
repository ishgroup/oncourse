package ish.oncourse.admin.services.billing;

import ish.oncourse.model.College;
import ish.oncourse.model.WebSite;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IBillingDataService {
	
	Map<Long, Map<Long, Map<String, Object>>> getLicenseFeeData();

	Map<Long, Map<Long, Map<String, Object>>> getBillingData(Date from, Date to);
	
	String getBillingDataExport(List<College> colleges, Date month);
	
	Map<Double, Double> getTasmaniaEcommerce(double thisMonth, Map<Long, Map<Long, Map<String, Object>>> billingData);
}
