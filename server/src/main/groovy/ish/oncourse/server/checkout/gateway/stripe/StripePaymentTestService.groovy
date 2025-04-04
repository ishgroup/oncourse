/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.checkout.gateway.stripe

import com.google.inject.Inject
import groovy.transform.CompileDynamic
import ish.oncourse.server.PreferenceController

@CompileDynamic
class StripePaymentTestService extends StripePaymentService {

    @Inject
    private PreferenceController preferenceController

    @Override
    protected String getApiKey() {
        return preferenceController.testPaymentGatewayPassStripe
    }

    @Override
    String getClientKey() {
        return preferenceController.testPaymentGatewayClientPassStripe
    }
}
