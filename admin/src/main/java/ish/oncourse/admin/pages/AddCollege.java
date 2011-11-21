package ish.oncourse.admin.pages;

import java.math.BigDecimal;

import ish.oncourse.admin.pages.college.Billing;
import ish.oncourse.model.College;
import ish.oncourse.model.LicenseFee;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.system.ICollegeService;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;

public class AddCollege {
	
	@Property
	private String findCollegeUrl;
	
	@Property
	private Long collegeId;
	
	@Property
	private String billingCode;
	
	@Property
	private String servicesPassword;
	
	@Inject
	private Request request;
	
	@Inject
	private Response response;
	
	@Inject
	private ICayenneService cayenneService;
	
	@Inject
	private ICollegeService collegeService;
	
	@Inject
	private PageRenderLinkSource prls;
	
	@SetupRender
	void setupRender() {
		this.findCollegeUrl = response.encodeURL(request.getContextPath() + "/findCollege");
	}
	
	@OnEvent(component="newCollegeForm", value="success")
	Object newCollege() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		
		College college = (College) context.localObject(collegeService.findById(collegeId).getObjectId(), null);
		if (college != null) {
			college.setBillingCode(billingCode);
			college.setWebServicesPass(servicesPassword);
		}
		
		createFee(context, college, "sms");
		createFee(context, college, "cc-office");
		createFee(context, college, "cc-web");
		createFee(context, college, "ecommerce");
		createFee(context, college, "support");
		createFee(context, college, "hosting");
		
		context.commitChanges();
		
		return prls.createPageRenderLinkWithContext(Billing.class, collegeId);
	}
	
	private LicenseFee createFee(ObjectContext context, College college, String keyCode) {
		LicenseFee fee = context.newObject(LicenseFee.class);
		fee.setCollege(college);
		fee.setKeyCode(keyCode);
		fee.setFreeTransactions(0);
		fee.setFee(new BigDecimal(0));
		return fee;
	}
}
