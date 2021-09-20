package ish.oncourse.admin.pages.college;

import ish.oncourse.admin.pages.Index;
import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.preference.PreferenceControllerFactory;
import ish.oncourse.services.system.ICollegeService;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;


public class Billing {

	private static final Logger logger = LogManager.getLogger();

	@Property
	@Persist
	private College college;
	
	@Property
	private boolean skipPaymentAuth;

	@Property
	private String gatewayPass;
	
	@Property
	private String collegeName;


	@Inject
	private ICayenneService cayenneService;
	
	@Inject
	private ICollegeService collegeService;

	@Inject
	private PreferenceControllerFactory prefsFactory;

	private PreferenceController preferenceController;

    @InjectPage
    private Index indexPage;

	Object onActivate() {
		this.preferenceController = prefsFactory.getPreferenceController(college);
		return null;
	}

	@SetupRender
	void setupRender() {

		ObjectContext context = cayenneService.newContext();

		this.college = context.localObject(college);

		this.collegeName = college.getName();
		this.gatewayPass = preferenceController.getPaymentGatewayPass();
		this.skipPaymentAuth = preferenceController.isPurchaseWithoutAuth();
	}

	@OnEvent(component="billingForm", value="success")
	void submitted() {
		ObjectContext context = college.getObjectContext();

		if (StringUtils.trimToNull(collegeName) != null) {
			college.setName(collegeName);
		}

		preferenceController.setPaymentGatewayPass(gatewayPass);
		preferenceController.setPurchaseWithoutAuth(skipPaymentAuth);
		context.commitChanges();
	}
	

    void onActivate(Long id) {
		if (college == null || !college.getId().equals(id)) {
			this.college = collegeService.findById(id);
		}
	}

	Object onPassivate() {
		return this.college.getId();
	}
	
    public Object onException(Throwable cause){
        //redirect to index page when session was expired and persist properties got null value
        if (college == null) {
            return indexPage;
		} else {
			throw new IllegalStateException(cause);
		}
    }
}
