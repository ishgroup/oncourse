package ish.oncourse.services.preference

import ish.oncourse.model.College
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils

import static ish.oncourse.services.preference.Preferences.ENROLMENT_CREDITCARD_PAYMENT_ENABLED

class IsCreditCardPaymentEnabled extends GetPreference {

    IsCreditCardPaymentEnabled(College college, ObjectContext objectContext) {
        super(college, ENROLMENT_CREDITCARD_PAYMENT_ENABLED, objectContext)
    }

    boolean get() {
        String valueString = StringUtils.trimToNull(getValue())
        if (valueString == null)
            return true
        return Boolean.valueOf(value)
    }
}
