package ish.oncourse.ui.components;

import ish.oncourse.services.preference.PreferenceController;
import org.apache.tapestry5.ioc.annotations.Inject;

public class ConfirmOrderDialog {
	
	@Inject
	private PreferenceController preferenceController;
	
	public boolean isPaymentGatewayEnabled() {
		return preferenceController.isPaymentGatewayEnabled();
	}

}
