package ish.oncourse.services.preference

import ish.oncourse.model.College
import ish.oncourse.model.PaymentGatewayType
import org.apache.cayenne.ObjectContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import static ish.oncourse.services.preference.Preferences.PAYMENT_GATEWAY_TYPE

class IsPaymentGatewayEnabled {
    
    static Logger logger = LogManager.getLogger()
    College college
    ObjectContext context

    IsPaymentGatewayEnabled(College college, ObjectContext context) {
        this.college = college
        this.context = context
    }
    
    boolean get() {
        try {
            PaymentGatewayType.DISABLED != PaymentGatewayType.valueOf(new GetPreference(college, PAYMENT_GATEWAY_TYPE, context).getValue())
        } catch (Exception ignored) {
            logger.error('Can not determine payment gateway type for college id:{}', college.id)
            false
        }
    }
}
