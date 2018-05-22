package ish.oncourse.admin.services.billing;

import ish.oncourse.model.College;
import ish.oncourse.model.CustomFee;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.system.ICollegeService;
import org.apache.cayenne.DataRow;
import org.apache.cayenne.query.SQLSelect;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.*;

public class BillingDataServiceImpl implements IBillingDataService {
	
    private static final String SQL_LICENSE_FEE = "SELECT l.college_id as collegeId, l.webSiteId as webSiteId, l.key_code as keyCode, l.fee as fee, l.paidUntil as paidUntil, l.plan_name as plan, l.free_transactions as freeTransactions FROM LicenseFee as l JOIN College as c on c.id = l.college_id WHERE c.billingCode IS NOT NULL";
    private static final String SQL_SMS = "SELECT count(*) as count, c.id as collegeId FROM MessagePerson AS m JOIN College AS c on c.Id = m.collegeId WHERE c.billingCode IS NOT NULL and type = 2 AND timeOfDelivery >= #bind($from)  AND timeOfDelivery <= #bind($to) GROUP BY collegeid";
    private static final String SQL_OFFICE_TRANSACTION_COUNT = "SELECT count(*) as count, c.id as collegeId FROM PaymentIn As p JOIN College AS c on c.Id = p.collegeId WHERE c.billingCode IS NOT NULL and p.created >= #bind($from) AND p.created <= #bind($to) AND source = 'O' AND p.type = 2 AND (status = 3 OR status = 6) GROUP BY collegeid";
    private static final String SQL_WEB_TRANSACTION_COUNT = "select count(p.id) as count, c.id as collegeId, i.webSiteId as webSiteId from PaymentIn p join PaymentInLine pil on p.id = pil.paymentInId join Invoice i on pil.invoiceId = i.id join College c on p.collegeId = c.id where c.billingCode is not null and p.created >= #bind($from) and p.created <= #bind($to) and p.source = 'W' and p.type = 2 and p.status in (3, 6) group by collegeId, i.webSiteId";
    private static final String SQL_WEB_TRANSACTION_VALUE = "SELECT c.id as collegeId, i.webSiteId as webSiteId, SUM(i.totalGst) as value FROM Invoice i JOIN College AS c on c.Id = i.collegeId WHERE c.billingCode IS NOT NULL and i.created >= #bind($from) AND i.created <= #bind($to) AND i.source = 'W' GROUP BY collegeid, i.webSiteId";

    @Inject
	private ICayenneService cayenneService;

	@Inject
	private ICollegeService collegeService;

	@Override
	public Map<Long, Map<Long, Map<String, Object>>> getLicenseFeeData() {

		Map<Long, Map<Long, Map<String, Object>>> data = createMap();

		List<DataRow> result = SQLSelect.dataRowQuery(SQL_LICENSE_FEE).select(cayenneService.newContext());

		for (DataRow r : result) {
			Long collegeId = (Long) r.get("collegeId");

			Long webSiteId = (Long) r.get("webSiteId");

			Map<Long, Map<String, Object>> billingRow = data.get(collegeId);

			String key = (String) r.get("keyCode");
			billingRow.get(webSiteId).put(key, r.get("fee"));
			billingRow.get(webSiteId).put(key + "-renewMonth", r.get("billingMonth"));
			billingRow.get(webSiteId).put(key + "-plan", r.get("plan"));
			billingRow.get(webSiteId).put(key + "-paidUntil", r.get("paidUntil"));

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

		Map<String, Object> params = new HashMap<>();
		params.put("from", from);
		params.put("to", to);
		
		// SMS
		List<DataRow> result = SQLSelect.dataRowQuery(SQL_SMS).params(params).select(cayenneService.newContext());

		for (DataRow r : result) {
			Long collegeId = (Long) r.get("collegeId");
			data.get(collegeId).get(null).put("sms", r.get("count"));
		}
		
		// office transaction count
		result = SQLSelect.dataRowQuery(SQL_OFFICE_TRANSACTION_COUNT).params(params).select(cayenneService.newContext());

		for (DataRow r : result) {
			Long collegeId = (Long) r.get("collegeId");
			data.get(collegeId).get(null).put("ccOffice", r.get("count"));
		}
		
		// web transaction count
		result = SQLSelect.dataRowQuery(SQL_WEB_TRANSACTION_COUNT).params(params).select(cayenneService.newContext());

		for (DataRow r : result) {
			Long collegeId = (Long) r.get("collegeId");
			Long webSiteId = (Long) r.get("webSiteId");
			data.get(collegeId).get(webSiteId).put("ccWeb", r.get("count"));
		}
		
		// web transaction value
		result =  SQLSelect.dataRowQuery(SQL_WEB_TRANSACTION_VALUE).params(params).select(cayenneService.newContext());

		for (DataRow r : result) {
			Long collegeId = (Long) r.get("collegeId");
			Long webSiteId = (Long) r.get("webSiteId");
			data.get(collegeId).get(webSiteId).put("ccWebValue", r.get("value"));
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
		String exportData = "Type\tTheirRef\tNameCode\tDetail.StockCode\tDetail.Description\tDetail.StockQty\tDetail.UnitPrice\tDescription\tTransactionDate\n";
		
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
		
		for (CustomFee customFee : college.getCustomFees()) {
			text.append(new CustomFeeExportLineBuilder(college, from, customFee).buildLine());
		}
				
		for (WebSite webSite : college.getWebSites()) {
			text.append(new HostingExportLineBuilder(college, webSite, from, billingData, licenseData).buildLine());
			text.append(new WebCCExportLineBuilder(college, webSite, from, billingData, licenseData).buildLine());
			text.append(new EcommerceExportLineBuilder(college, webSite, from, billingData, licenseData).buildLine());
		}
		
		return text.toString();
	}
}
