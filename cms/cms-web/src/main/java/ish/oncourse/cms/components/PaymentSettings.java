package ish.oncourse.cms.components;

import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.ui.pages.internal.Page;

import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
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
		this.redirect = preferenceController.getRedirectOnPaymentSuccessful();
		if (preferenceController.getPaymentSuccessfulRedirectUrl() != null) {
			this.redirectToUrl = preferenceController.getPaymentSuccessfulRedirectUrl();
		}
		else {
			this.redirectToUrl = "";
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
		
		preferenceController.setRedirectOnPaymentSuccessful(this.redirect);
		preferenceController.setPaymentSuccessfulRedirectUrl(this.redirectToUrl);
		
		saved = true;
		
		return paymentSettingsZone.getBody();
	}
	
	public Zone getPaymentSettingsZone() {
		return paymentSettingsZone;
	}

}
