package ish.oncourse.ui.components;

import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.services.preference.PreferenceController;
import org.apache.tapestry5.ioc.annotations.Inject;

public class ConfirmOrderDialog extends ISHCommon {
	
	@Inject
	private PreferenceController preferenceController;
	
	public boolean isPaymentGatewayEnabled() {
		return preferenceController.isPaymentGatewayEnabled();
	}

}
