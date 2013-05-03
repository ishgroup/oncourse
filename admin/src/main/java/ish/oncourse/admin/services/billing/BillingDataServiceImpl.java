package ish.oncourse.admin.services.billing;

import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.system.ICollegeService;
import org.apache.cayenne.DataRow;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class BillingDataServiceImpl implements IBillingDataService {
	
	private static final DateFormat TRANSACTION_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	
    private static final String SQL_LICENSE_FEE = "SELECT l.college_id as collegeId, l.webSiteId as webSiteId, l.key_code as keyCode, l.fee as fee, l.paidUntil as paidUntil, l.renewalDate as renewalDate, l.plan_name as plan, l.free_transactions as freeTransactions FROM LicenseFee as l JOIN College as c on c.id = l.college_id WHERE c.billingCode IS NOT NULL";
    private static final String SQL_SMS = "SELECT count(*) as count, c.id as collegeId FROM MessagePerson AS m JOIN College AS c on c.Id = m.collegeId WHERE c.billingCode IS NOT NULL and type = 2 AND timeOfDelivery >= #bind($from)  AND timeOfDelivery <= #bind($to) GROUP BY collegeid";
    private static final String SQL_OFFICE_TRANSACTION_COUNT = "SELECT count(*) as count, c.id as collegeId FROM PaymentIn As p JOIN College AS c on c.Id = p.collegeId WHERE c.billingCode IS NOT NULL and p.created >= #bind($from) AND p.created <= #bind($to) AND source = 'O' AND p.type = 2 AND (status = 3 OR status = 6) GROUP BY collegeid";
    private static final String SQL_WEB_TRANSACTION_COUNT = "select count(p.id) as count, c.id as collegeId, i.webSiteId as webSiteId from PaymentIn p join PaymentInLine pil on p.id = pil.paymentInId join Invoice i on pil.invoiceId = i.id join College c on p.collegeId = c.id where c.billingCode is not null and p.created >= #bind($from) and p.created <= #bind($to) and p.source = 'W' and p.type = 2 and p.status in (3, 6) group by collegeId, i.webSiteId";
    private static final String SQL_WEB_TRANSACTION_VALUE = "SELECT c.id as collegeId, i.webSiteId as webSiteId, SUM(i.totalGst) as value FROM Invoice i JOIN College AS c on c.Id = i.collegeId WHERE c.billingCode IS NOT NULL and i.created >= #bind($from) AND i.created <= #bind($to) AND i.source = 'W' GROUP BY collegeid, i.webSiteId";
    private static final String SQL_TASMANIA_ECOMMERCE = "SELECT SUM(i.totalGst) as value FROM Invoice i WHERE i.created >= #bind($from) AND i.created <= #bind($to) AND i.collegeId = 15 AND i.source = 'W'";

    @Inject
	private ICayenneService cayenneService;

	@Inject
	private ICollegeService collegeService;

	public static Map<Double, Double> getTasmaniaEcommerceMap(double thisMonth, Map<Long, Map<Long, Map<String, Object>>> billingData) {

		Map<Double, Double> fees = new HashMap<>();
		fees.put(3.50, 100000.0);
		fees.put(3.00, 100000.0);
		fees.put(1.80, 200000.0);
		fees.put(1.25, 400000.0);
		fees.put(1.00, 800000.0);
		fees.put(0.95, 1600000.0);
		fees.put(0.90, 9999999999.0);

		Map<Double, Double> result = new HashMap<>();

		BigDecimal tasmaniaYearToDate = (BigDecimal) billingData.get((long) 15).get(null).get("tasmaniaYearToDate");
		double yearToDate = tasmaniaYearToDate == null ? 0 : tasmaniaYearToDate.doubleValue();

		for (Double key : fees.keySet()) {
			if (yearToDate > 0) {
				double deductThisIteration = Math.min(yearToDate, fees.get(key));
				fees.put(key, fees.get(key) - deductThisIteration);
				yearToDate -= deductThisIteration;
			}
		}

		for (Double key : fees.keySet()) {
			if (fees.get(key) > 0 && thisMonth > 0) {
				result.put(key, Math.min(thisMonth, fees.get(key)));
				thisMonth -= fees.get(key);
			}
		}

		return result;
	}

	@Override
	public Map<Long, Map<Long, Map<String, Object>>> getLicenseFeeData() {

		Map<Long, Map<Long, Map<String, Object>>> data = createMap();

		SQLTemplate query = new SQLTemplate(LicenseFee.class, SQL_LICENSE_FEE);
		query.setFetchingDataRows(true);

		List<DataRow> result = cayenneService.sharedContext().performQuery(query);

		for (DataRow r : result) {
			Long collegeId = (Long) r.get("collegeId");

			Long webSiteId = (Long) r.get("webSiteId");

			Map<Long, Map<String, Object>> billingRow = data.get(collegeId);

			String key = (String) r.get("keyCode");
			billingRow.get(webSiteId).put(key, r.get("fee"));
			billingRow.get(webSiteId).put(key + "-renewMonth", r.get("billingMonth"));
			billingRow.get(webSiteId).put(key + "-plan", r.get("plan"));
			billingRow.get(webSiteId).put(key + "-paidUntil", r.get("paidUntil"));
			billingRow.get(webSiteId).put(key + "-renewalDate", r.get("renewalDate"));

			Integer freeTransactions = (Integer) r.get("freeTransactions");

			if (freeTransactions != null && freeTransactions != 0) {
				billingRow.get(webSiteId).put(key + "-free", freeTransactions);
			}
		}

		return data;
	}

	@Override
	public Map<Long, Map<Long, Map<String, Object>>> getBillingData(Date from, Date to) {

        Map<Long, Map<Long, Map<String, Object>>> data = createMap();
		
		// SMS
		SQLTemplate query = new SQLTemplate(MessagePerson.class, SQL_SMS);

		Map<String, Object> params = new HashMap<>();
		params.put("from", from);
		params.put("to", to);

		query.setParameters(params);
		query.setFetchingDataRows(true);

		List<DataRow> result = cayenneService.sharedContext().performQuery(query);

		for (DataRow r : result) {
			Long collegeId = (Long) r.get("collegeId");
			data.get(collegeId).get(null).put("sms", r.get("count"));
		}

		// office transaction count
		query = new SQLTemplate(PaymentIn.class, SQL_OFFICE_TRANSACTION_COUNT);
		query.setParameters(params);
		query.setFetchingDataRows(true);

		result = cayenneService.sharedContext().performQuery(query);

		for (DataRow r : result) {
			Long collegeId = (Long) r.get("collegeId");
			data.get(collegeId).get(null).put("ccOffice", r.get("count"));
		}

		// web transaction count
		query = new SQLTemplate(PaymentIn.class, SQL_WEB_TRANSACTION_COUNT);
		query.setParameters(params);
		query.setFetchingDataRows(true);

		result = cayenneService.sharedContext().performQuery(query);

		for (DataRow r : result) {
			Long collegeId = (Long) r.get("collegeId");
			Long webSiteId = (Long) r.get("webSiteId");
			data.get(collegeId).get(webSiteId).put("ccWeb", r.get("count"));
		}

		// web transaction value
		query = new SQLTemplate(PaymentIn.class, SQL_WEB_TRANSACTION_VALUE);
		query.setParameters(params);
		query.setFetchingDataRows(true);

		result = cayenneService.sharedContext().performQuery(query);

		for (DataRow r : result) {
			Long collegeId = (Long) r.get("collegeId");
			Long webSiteId = (Long) r.get("webSiteId");
			data.get(collegeId).get(webSiteId).put("ccWebValue", r.get("value"));
		}

		// tasmania ecommerce
		Long tasmaniaId = 15L;
		
		if (data.get(tasmaniaId) != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(to);
			
			if (cal.get(Calendar.MONTH) == 1) {
				data.get(tasmaniaId).get(null).put("tasmaniaYearToDate", new BigDecimal(0.0));
			}
			else {
				Map<String, Object> tasmaniaParams = new HashMap<>();
				
				cal.setTime(from);
				cal.set(Calendar.DAY_OF_YEAR, 1);
				tasmaniaParams.put("from", cal.getTime());
				cal.setTime(to);
				cal.add(Calendar.MONTH, -1);
				tasmaniaParams.put("to", cal.getTime());
				
				query = new SQLTemplate(PaymentIn.class, SQL_TASMANIA_ECOMMERCE);
				query.setParameters(tasmaniaParams);
				query.setFetchingDataRows(true);
				
				result = cayenneService.sharedContext().performQuery(query);
				
				BigDecimal value = (BigDecimal) result.get(0).get("value");
				
				if (value != null) {
					data.get(tasmaniaId).get(null).put("tasmaniaYearToDate", value);
				}
			}
		}
		
		return data;
	}

	private Map<Long, Map<Long, Map<String, Object>>> createMap() {
		Map<Long, Map<Long, Map<String, Object>>> data = new HashMap<>();

		for (College college : collegeService.allColleges()) {
			if (!data.containsKey(college.getId())) {
				data.put(college.getId(), new HashMap<Long, Map<String, Object>>());
			}

			for (WebSite webSite : college.getWebSites()) {
				data.get(college.getId()).put(webSite.getId(), new HashMap<String, Object>());
			}

			data.get(college.getId()).put(null, new HashMap<String, Object>());
		}
		return data;
	}

    public String getBillingDataExport(List<College> colleges, Date month) {
		String exportData = "Type\tNameCode\tDetail.StockCode\tDetail.Description\tDetail.StockQty\tDetail.UnitPrice\tDescription\tTransactionDate\n";
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(month);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		
		Date from = cal.getTime();
		
		cal.add(Calendar.MONTH, 1);
		
		Date to = cal.getTime();

		Map<Long, Map<Long, Map<String, Object>>> billingData = getBillingData(from, to);
		Map<Long, Map<Long, Map<String, Object>>> licenseData = getLicenseFeeData();

		for (College college : colleges) {
			exportData += buildMWExport(college, from, billingData, licenseData);
		}
		
		return exportData;
	}

	private String buildMWExport(
			College college,
			Date from,
			Map<Long, Map<Long, Map<String, Object>>> billingData,
			Map<Long, Map<Long, Map<String, Object>>> licenseData) {
		
		StringBuilder text = new StringBuilder();
		
		text.append(new SupportExportLineBuilder(college, null, from, billingData, licenseData).buildLine());
		text.append(new SMSExportLineBuilder(college, null, from, billingData, licenseData).buildLine());
		text.append(new OfficeCCExportLineBuilder(college, null, from, billingData, licenseData).buildLine());

		for (WebSite webSite : college.getWebSites()) {
			text.append(new HostingExportLineBuilder(college, webSite, from, billingData, licenseData).buildLine());
			text.append(new WebCCExportLineBuilder(college, webSite, from, billingData, licenseData).buildLine());
			text.append(new EcommerceExportLineBuilder(college, webSite, from, billingData, licenseData).buildLine());
		}
		
		return text.toString();
	}

	@Override
	public Map<Double, Double> getTasmaniaEcommerce(double thisMonth, Map<Long, Map<Long, Map<String, Object>>> billingData) {
		return getTasmaniaEcommerceMap(thisMonth, billingData);
	}
}
