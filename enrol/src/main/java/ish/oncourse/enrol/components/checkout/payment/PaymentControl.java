package ish.oncourse.enrol.components.checkout.payment;

import ish.oncourse.enrol.checkout.payment.PaymentEditorDelegate;
import ish.oncourse.enrol.pages.Payment;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.util.URLUtils;
import ish.oncourse.util.ValidateHandler;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

public class PaymentControl {

    @Parameter(required = true)
    @Property
    private PaymentEditorDelegate delegate;

    @Inject
    private Messages messages;

    @Inject
    private PreferenceController preferenceController;

    @InjectPage
    private Payment paymentPage;

    @Property
    private Boolean userAgreed;


    private ValidateHandler validateHandler;


    @SetupRender
    void beforeRender() {
        userAgreed = false;
    }

    public ValidateHandler getValidateHandler()
    {
        if (validateHandler == null)
        {
            validateHandler = new ValidateHandler();
            validateHandler.setErrors(delegate.getErrors());
        }
        return validateHandler;
    }

    public String getEnrolmentDisclosure() {
        return StringUtils.trimToNull(preferenceController.getFeatureEnrolmentDisclosure());
    }

    public String getRefundPolicyUrl() {
    	if (isHasRefundPolicyUrl()) {
    		String url = preferenceController.getRefundPolicyUrl();
    		
    		if (!URLUtils.isAbsolute(url) && !url.startsWith("/")) {
    			url = "/" + url;
    		}
    		
    		return url;
    	}
    	
    	return null;
    }
    
    public boolean isHasRefundPolicyUrl() {
    	return preferenceController.getRefundPolicyUrl() != null;
    }

    public String getSubmitButtonText() {
        return isZeroPayment() ? messages.get("submit.button.text.enrol") : messages.get("submit.button.text.payment");
    }

    public boolean isZeroPayment() {
        return delegate.getPaymentIn().isZeroPayment();
    }

    @OnEvent(component = "paymentSubmit", value = "selected")
    public Object makePayment()
    {
        return paymentPage.makePayment();
    }


}
