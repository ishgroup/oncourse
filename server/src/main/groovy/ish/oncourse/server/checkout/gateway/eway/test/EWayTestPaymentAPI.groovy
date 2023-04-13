/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.checkout.gateway.eway.test

import ish.oncourse.server.checkout.gateway.eway.EWayPaymentAPI

class EWayTestPaymentAPI extends EWayPaymentAPI {

    // Authorization: "Basic $preferenceController.paymentGatewayPassEWay"
    // QTEwMDFDWis4cnlMd21VTEs5UU84U1d0UllrYXZVdVNBVW5ybUh5d1Q0WC92V0xBNXVHdzRBRDRxSXF2VXdoaTdQOThsVjphNUpOcUFMUw==

    private static final String EWAY_BASE_TEST = 'https://api.sandbox.ewaypayments.com'

    EWayTestPaymentAPI() {
        this.eWayBaseUrl = EWAY_BASE_TEST
    }
}
