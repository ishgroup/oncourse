/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.checkout

enum PaymentGatewayError {

    GATEWAY_ERROR(411),

    PAYMENT_ERROR(412),

    VALIDATION_ERROR(400)

    private Integer errorCode

    PaymentGatewayError(Integer errorCode) {
        this.errorCode = errorCode
    }

    Integer getErrorNumber() {
        return errorCode
    }
}