/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.common.types

enum PaymentGatewayType {

    EWAY("EWAY"),

    EWAY_TEST("EWAY_TEST"),

    STRIPE("STRIPE"),

    STRIPE_TEST("STRIPE_TEST"),

    SQUARE("SQUARE"),

    SQUARE_TEST("SQUARE_TEST"),

    WINDCAVE("WINDCAVE"),

    TEST_PAYMENT_SYSTEM("TEST"),

    OFFLINE("OFFLINE"),

    DISABLED("DISABLED");

    private String value

    PaymentGatewayType(String value) {
        this.value = value
    }

    String getValue() {
        return value
    }

    static PaymentGatewayType getByValue(String value) {
        return values().find {
            it.value.equalsIgnoreCase(value)
        }
    }
}
