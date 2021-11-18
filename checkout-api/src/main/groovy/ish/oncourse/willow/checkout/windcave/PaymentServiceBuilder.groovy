package ish.oncourse.willow.checkout.windcave

import groovy.transform.CompileStatic
import ish.math.Country
import ish.oncourse.model.College
import ish.oncourse.services.preference.GetPreference
import ish.oncourse.services.preference.IsPaymentGatewayEnabled
import org.apache.cayenne.ObjectContext

import static ish.persistence.Preferences.ACCOUNT_CURRENCY
import static ish.persistence.Preferences.PAYMENT_GATEWAY_PASS
import static ish.persistence.Preferences.PAYMENT_GATEWAY_PURCHASE_WITHOUT_AUTH

@CompileStatic
class PaymentServiceBuilder {

    IPaymentService build(College college, ObjectContext context) {
        
        boolean gatewayEnabled = new IsPaymentGatewayEnabled(college, context).get()
        IPaymentService service

        if (System.getProperty('payment.gateway.type.test')) {
            service = new TestPaymentService()
        } else if (gatewayEnabled) {
            String gatewayPass = new GetPreference(college, PAYMENT_GATEWAY_PASS, context).getValue()
            Boolean skipAuth = new GetPreference(college, PAYMENT_GATEWAY_PURCHASE_WITHOUT_AUTH, context).getBooleanValue()
            Country country =  new GetPreference(college, ACCOUNT_CURRENCY, context).getCountry()
            service = new PaymentService(gatewayPass, skipAuth, country)
        } else {
            throw new IllegalArgumentException()
        }

        return service
    }
}
