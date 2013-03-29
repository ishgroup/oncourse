package ish.oncourse.cms.components;

import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.ui.pages.internal.Page;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class PaymentSettings {
	
	@Property
	private boolean redirect;
	
	@Property
	private String redirectToUrl;
	
	@Property
	private boolean refundPolicy;
	
	@Property
	private String refundPolicyUrl;
	
	@SuppressWarnings("all")
	@Property
	private boolean saved;
	
	@InjectComponent
	@Property
	private Form paymentSettingsForm;
	
	@Component
	private Zone paymentSettingsZone;
	
	@Inject
	private Request request;
	
	@Inject
	private PreferenceController preferenceController;
	
	@InjectPage
	private Page page;
	
	@SetupRender
	void beforeRender() {
		if (preferenceController.getEnrolSuccessUrl() != null) {
			this.redirect = true;
			this.redirectToUrl = preferenceController.getEnrolSuccessUrl();
		}
		else {
			this.redirect = false;
		}
		
		if (preferenceController.getRefundPolicyUrl() != null) {
			this.refundPolicy = true;
			this.refundPolicyUrl = preferenceController.getRefundPolicyUrl();
		} else {
			this.refundPolicy = false;
		}
	}
	
	@AfterRender
	void afterRender() {
		saved = false;
	}
	
	Object onSuccessFromPaymentSettingsForm() {
		if (request.getSession(false) == null) {
			return page.getReloadPageBlock();
		}
		paymentSettingsForm.clearErrors();
		
		if (redirect) {
			preferenceController.setEnrolSuccessUrl(this.redirectToUrl);
		}
		else {
			preferenceController.setEnrolSuccessUrl(null);
		}
		
		if (refundPolicy) {
			preferenceController.setRefundPolicyUrl(this.refundPolicyUrl);
		} else {
			preferenceController.setRefundPolicyUrl(null);
		}
		
		saved = true;
		
		return paymentSettingsZone.getBody();
	}
	
	public Zone getPaymentSettingsZone() {
		return paymentSettingsZone;
	}

}
