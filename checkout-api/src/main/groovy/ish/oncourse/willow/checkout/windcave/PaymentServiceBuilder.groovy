package ish.oncourse.willow.checkout.windcave

import ish.math.Country
import ish.oncourse.model.College
import ish.oncourse.model.PaymentGatewayType
import ish.oncourse.services.preference.GetPreference
import org.apache.cayenne.ObjectContext

import static ish.oncourse.services.preference.Preferences.PAYMENT_GATEWAY_TYPE
import static ish.persistence.Preferences.ACCOUNT_CURRENCY
import static ish.persistence.Preferences.PAYMENT_GATEWAY_PASS
import static ish.persistence.Preferences.PAYMENT_GATEWAY_PURCHASE_WITHOUT_AUTH

class PaymentServiceBuilder {

    IPaymentService build(College college, ObjectContext context) {
        PaymentGatewayType gatewayType = PaymentGatewayType.valueOf(new GetPreference(college, PAYMENT_GATEWAY_TYPE, context).getValue())
        IPaymentService service
        switch (gatewayType) {
            case PaymentGatewayType.TEST:
                service = new TestPaymentService()
                break
            case PaymentGatewayType.PAYMENT_EXPRESS:
                String gatewayPass = new GetPreference(college, PAYMENT_GATEWAY_PASS, context).getValue()
                Boolean skipAuth = new GetPreference(college, PAYMENT_GATEWAY_PURCHASE_WITHOUT_AUTH, context).getBooleanValue()
                Country country =  new GetPreference(college, ACCOUNT_CURRENCY, context).getCountry()
                service = new PaymentService(gatewayPass, skipAuth, country)

                break
            case PaymentGatewayType.DISABLED:
            default:
                throw new IllegalArgumentException()
        }


        return service
    }
}
