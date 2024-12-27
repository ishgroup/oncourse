/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.checkout.gateway.stripe

import com.google.inject.Inject
import com.stripe.Stripe
import com.stripe.model.Charge
import com.stripe.model.PaymentIntent
import com.stripe.model.Refund
import com.stripe.model.checkout.Session
import com.stripe.param.PaymentIntentCreateParams
import com.stripe.param.RefundCreateParams
import com.stripe.param.checkout.SessionCreateParams
import groovy.transform.CompileDynamic
import ish.common.checkout.gateway.SessionAttributes
import ish.common.checkout.gateway.stripe.CardTypeAdapter
import ish.common.checkout.gateway.stripe.session.StripeSessionStatus
import ish.math.Money
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.checkout.Checkout
import ish.oncourse.server.api.v1.model.CheckoutResponseDTO
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.checkout.gateway.PaymentServiceInterface
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import static com.stripe.param.checkout.SessionCreateParams.PaymentIntentData.SetupFutureUsage.OFF_SESSION
import static com.stripe.param.checkout.SessionCreateParams.PaymentIntentData.SetupFutureUsage.ON_SESSION

@CompileDynamic
class StripePaymentTestService extends StripePaymentService {

    @Inject
    private PreferenceController preferenceController

    @Override
    protected String getApiKey() {
        return preferenceController.testPaymentGatewayPassStripe
    }
}
