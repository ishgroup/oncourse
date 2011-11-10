package ish.oncourse.admin.pages.college;

import java.text.SimpleDateFormat;
import java.util.List;

import ish.oncourse.model.College;
import ish.oncourse.model.LicenseFee;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.system.ICollegeService;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Billing {
	
	@Property
	private College college;
	
	@Property
	private LicenseFee currentLicenseFee;
	
	@Property
	private List<LicenseFee> licenseFees;
	
	@Inject
	private ICayenneService cayenneService;
	
	@Inject
	private ICollegeService collegeService;
	
	private SimpleDateFormat dateFormat;
	
	@SetupRender
	void setupRender() {
		this.licenseFees = college.getLicenseFees();
		this.dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	}
	
	Object onActivate(Long id) {
		this.college = collegeService.findById(id);
		return null;
	}

	public String getPaymentExpUser() {
		return college.getPaymentGatewayAccount();
	}
	
	public String getPaymentExpPass() {
		return college.getPaymentGatewayPass();
	}
	
	public String getPlanName() {
		return currentLicenseFee.getPlanName();
	}
	
	public String getKeyCode() {
		return currentLicenseFee.getKeyCode();
	}
	
	public String getBillingMonth() {
		return String.valueOf(currentLicenseFee.getBillingMonth());
	}
	
	public String getValidUntil() {
		if (currentLicenseFee.getValidUntil() != null) {
			return dateFormat.format(currentLicenseFee.getValidUntil());
		}
		else {
			return "";
		}
	}
	
	public String getFee() {
		return String.valueOf(currentLicenseFee.getFee());
	}
}
