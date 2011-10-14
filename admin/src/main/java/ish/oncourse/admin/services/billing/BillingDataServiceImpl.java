package ish.oncourse.admin.services.billing;

import ish.oncourse.model.College;
import ish.oncourse.model.LicenseFee;
import ish.oncourse.model.MessagePerson;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.system.ICollegeService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.tapestry5.ioc.annotations.Inject;

public class BillingDataServiceImpl implements IBillingDataService {

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private ICollegeService collegeService;

	@Override
	public Map<Long, Map<String, Object>> getLicenseFeeData() {

		Map<Long, Map<String, Object>> data = new HashMap<Long, Map<String, Object>>();

		for (College college : collegeService.allColleges()) {
			if (!data.containsKey(college.getId())) {
				data.put(college.getId(), new HashMap<String, Object>());
			}
		}

		String sql = "SELECT l.college_id as collegeId, l.key_code as keyCode, l.fee as fee, l.billingMonth as billingMonth, l.plan_name as plan, l.free_transactions as freeTransactions FROM LicenseFee as l JOIN College as c on c.id = l.college_id WHERE c.billingCode IS NOT NULL";
		SQLTemplate query = new SQLTemplate(LicenseFee.class, sql);
		query.setFetchingDataRows(true);

		List<DataRow> result = cayenneService.sharedContext().performQuery(query);

		for (DataRow r : result) {
			Long collegeId = (Long) r.get("collegeId");

			Map<String, Object> billingRow = data.get(collegeId);

			String key = (String) r.get("keyCode");
			billingRow.put(key, r.get("fee"));
			billingRow.put(key + "-renewMonth", r.get("billingMonth"));
			billingRow.put(key + "-plan", r.get("plan"));

			Integer freeTransactions = (Integer) r.get("freeTransactions");

			if (freeTransactions != null && freeTransactions.intValue() != 0) {
				billingRow.put(key + "-free", freeTransactions);
			}
		}

		return data;
	}

	@SuppressWarnings("unchecked")
	public Map<Long, Map<String, Object>> getBillingData(Date from, Date to) {

		Map<Long, Map<String, Object>> data = new HashMap<Long, Map<String, Object>>();
		
		for (College college : collegeService.allColleges()) {
			if (!data.containsKey(college.getId())) {
				data.put(college.getId(), new HashMap<String, Object>());
			}
		}
		
		// SMS
		String sql = "SELECT count(*) as count, c.id as collegeId FROM MessagePerson AS m JOIN College AS c on c.Id = m.collegeId WHERE c.billingCode IS NOT NULL and type = 2 AND timeOfDelivery >= #bind($from)  AND timeOfDelivery <= #bind($to) GROUP BY collegeid";
		SQLTemplate query = new SQLTemplate(MessagePerson.class, sql);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("from", from);
		params.put("to", to);

		query.setParameters(params);
		query.setFetchingDataRows(true);

		List<DataRow> result = cayenneService.sharedContext().performQuery(query);

		for (DataRow r : result) {
			Long collegeId = (Long) r.get("collegeId");
			data.get(collegeId).put("sms", r.get("count"));
		}

		// office transaction count
		sql = "SELECT count(*) as count, c.id as collegeId FROM PaymentIn As p JOIN College AS c on c.Id = p.collegeId WHERE c.billingCode IS NOT NULL and p.created >= #bind($from) AND p.created <= #bind($to) AND source = 'O' AND creditCardType IS NOT NULL AND (status = 3 OR status = 6) GROUP BY collegeid";
		query = new SQLTemplate(PaymentIn.class, sql);
		query.setParameters(params);
		query.setFetchingDataRows(true);

		result = cayenneService.sharedContext().performQuery(query);

		for (DataRow r : result) {
			Long collegeId = (Long) r.get("collegeId");
			data.get(collegeId).put("ccOffice", r.get("count"));
		}

		// web transaction count
		sql = "SELECT count(*) as count, c.id as collegeId FROM PaymentIn As p JOIN College AS c on c.Id = p.collegeId WHERE c.billingCode IS NOT NULL and p.created >= #bind($from) AND p.created <= #bind($to) AND source = 'W' AND creditCardType IS NOT NULL AND (status = 3 OR status = 6) GROUP BY collegeid";
		query = new SQLTemplate(PaymentIn.class, sql);
		query.setParameters(params);
		query.setFetchingDataRows(true);

		result = cayenneService.sharedContext().performQuery(query);

		for (DataRow r : result) {
			Long collegeId = (Long) r.get("collegeId");
			data.get(collegeId).put("ccWeb", r.get("count"));
		}

		// web transaction value
		sql = "SELECT c.id as collegeId, SUM(i.totalExGst) as value, SUM(i.totalGst) as valueGST FROM PaymentIn As p inner join PaymentInLine pl on pl.paymentInId = p.id inner join Invoice i on pl.invoiceId=i.id inner JOIN College AS c on c.Id = p.collegeId WHERE c.billingCode IS NOT NULL and p.created >= #bind($from) AND p.created <= #bind($to) AND p.source = 'W' AND p.status = 3 GROUP BY collegeid";
		query = new SQLTemplate(PaymentIn.class, sql);
		query.setParameters(params);
		query.setFetchingDataRows(true);

		result = cayenneService.sharedContext().performQuery(query);

		for (DataRow r : result) {
			Long collegeId = (Long) r.get("collegeId");
			data.get(collegeId).put("ccWebValue", ((BigDecimal) r.get("value")).add((BigDecimal) r.get("valueGST")));
		}

		return data;
	}

	@Override
	public void createInvoices() {
		// TODO Auto-generated method stub

	}
}
