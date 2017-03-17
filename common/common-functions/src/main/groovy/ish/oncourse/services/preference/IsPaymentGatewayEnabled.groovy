package ish.oncourse.services.preference

import ish.oncourse.model.College
import ish.oncourse.model.PaymentGatewayType
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import static ish.oncourse.services.preference.PreferenceConstant.PAYMENT_GATEWAY_TYPE

class IsPaymentGatewayEnabled {
    
    static Logger logger = LogManager.getLogger()
    College college
    
    boolean get() {
        try {
            PaymentGatewayType.DISABLED != PaymentGatewayType.valueOf(new GetPreference(college: college, key: PAYMENT_GATEWAY_TYPE).get())
        } catch (Exception ignored) {
            logger.error('Can not determine payment gateway type for college id:{}', college.id)
            false
        }
    }
}
