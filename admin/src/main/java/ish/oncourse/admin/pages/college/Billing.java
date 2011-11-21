package ish.oncourse.admin.pages.college;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import ish.oncourse.model.College;
import ish.oncourse.model.LicenseFee;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.system.ICollegeService;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Billing {
	
	@Property
	@Persist
	private College college;
	
	@Property
	@Persist
	private Map<String, Map<String, String>> licenseInfo;
	
	@Property
	private String currentLicenseInfoKey;
	
	@Property
	private String infoKey;
	
	@Inject
	private ICayenneService cayenneService;
	
	@Inject
	private ICollegeService collegeService;
	
	private SimpleDateFormat dateFormat;
	
	@SetupRender
	void setupRender() {
		this.dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		
		this.licenseInfo = new LinkedHashMap<String, Map<String,String>>();
		for (LicenseFee fee : college.getLicenseFees()) {
			Map<String, String> info = new LinkedHashMap<String, String>();
			
			info.put(LicenseFee.PLAN_NAME_PROPERTY, fee.getPlanName());
			
			if (fee.getBillingMonth() != null) {
				info.put(LicenseFee.BILLING_MONTH_PROPERTY, String.valueOf(fee.getBillingMonth()));
			}
			else {
				info.put(LicenseFee.BILLING_MONTH_PROPERTY, "");
			}
			
			if (fee.getValidUntil() != null) {
				info.put(LicenseFee.VALID_UNTIL_PROPERTY, dateFormat.format(fee.getValidUntil()));
			}
			else {
				info.put(LicenseFee.VALID_UNTIL_PROPERTY, "");
			}
			
			info.put(LicenseFee.FREE_TRANSACTIONS_PROPERTY, String.valueOf(fee.getFreeTransactions()));
			info.put(LicenseFee.FEE_PROPERTY, String.valueOf(fee.getFee()));
			
			this.licenseInfo.put(fee.getKeyCode(), info);
		}
	}
	
	@OnEvent(component="billingForm", value="success")
	void submitted() throws Exception {
		this.dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		
		ObjectContext context = cayenneService.newNonReplicatingContext();
		
		College col = (College) context.localObject(college.getObjectId(), null);
		if (col != null) {
			col.setPaymentGatewayAccount(college.getPaymentGatewayAccount());
			col.setPaymentGatewayPass(college.getPaymentGatewayPass());
		}
		
		for (LicenseFee fee : college.getLicenseFees()) {
			Map<String, String> info = licenseInfo.get(fee.getKeyCode());
			LicenseFee lf = (LicenseFee) context.localObject(fee.getObjectId(), null);
			
			if (info != null && lf != null) {
				if (info.get(LicenseFee.PLAN_NAME_PROPERTY) != null) {
					lf.setPlanName(info.get(LicenseFee.PLAN_NAME_PROPERTY));
				}
				if (info.get(LicenseFee.BILLING_MONTH_PROPERTY) != null) {
					lf.setBillingMonth(Integer.parseInt(info.get(LicenseFee.BILLING_MONTH_PROPERTY)));
				}
				if (info.get(LicenseFee.VALID_UNTIL_PROPERTY) != null) {
					lf.setValidUntil(dateFormat.parse(info.get(LicenseFee.VALID_UNTIL_PROPERTY)));
				}
				if (info.get(LicenseFee.FREE_TRANSACTIONS_PROPERTY) != null) {
					lf.setFreeTransactions(Integer.parseInt(info.get(LicenseFee.FREE_TRANSACTIONS_PROPERTY)));
				}
				if (info.get(LicenseFee.FEE_PROPERTY) != null) {
					lf.setFee(new BigDecimal(info.get(LicenseFee.FEE_PROPERTY)));
				}
			}
		}
		
		context.commitChanges();
	}
	
	Object onActivate(Long id) {
		this.college = collegeService.findById(id);
		return null;
	}

	public String getPaymentExpUser() {
		return college.getPaymentGatewayAccount();
	}
	
	public void setPaymentExpUser(String value) {
		college.setPaymentGatewayAccount(value);
	}
	
	public String getPaymentExpPass() {
		return college.getPaymentGatewayPass();
	}
	
	public void setPaymentExpPass(String value) {
		college.setPaymentGatewayPass(value);
	}
	
	public Map<String, String> getCurrentLicenseInfo() {
		return licenseInfo.get(currentLicenseInfoKey);
	}
	
	public String getInfoValue() {
		return getCurrentLicenseInfo().get(infoKey);
	}
	
	public void setInfoValue(String value) {
		getCurrentLicenseInfo().put(infoKey, value);
	}
}
