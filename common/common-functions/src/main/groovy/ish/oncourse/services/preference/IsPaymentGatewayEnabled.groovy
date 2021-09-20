package ish.oncourse.services.preference

import ish.oncourse.model.College
import liquibase.util.StringUtils
import org.apache.cayenne.ObjectContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import static ish.persistence.Preferences.PAYMENT_GATEWAY_PASS

class IsPaymentGatewayEnabled extends GetPreference {
    
    static Logger logger = LogManager.getLogger()

    IsPaymentGatewayEnabled(College college, ObjectContext context) {
        super(college, PAYMENT_GATEWAY_PASS, context)
    }
    
    boolean get() {
        try {
            return StringUtils.trimToNull(getValue()) != null
        } catch (Exception ignored) {
            logger.error('Can not determine payment gateway type for college id:{}', college.id)
            false
        }
    }
}
