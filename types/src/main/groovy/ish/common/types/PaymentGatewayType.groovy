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

    WINDCAVE("WINDCAVE"),

    TEST_PAYMENT_SYSTEM("TEST"),

    DISABLED("DISABLED");

    private String value

    PaymentGatewayType(String value) {
        this.value = value
    }

    String getValue() {
        return value
    }

    static PaymentGatewayType getByValue(String value) {
        if (EWAY.value.equalsIgnoreCase(value)) {
            return EWAY
        }
        if (EWAY_TEST.value.equalsIgnoreCase(value)) {
            return EWAY_TEST
        }
        if (WINDCAVE.value.equalsIgnoreCase(value)) {
            return WINDCAVE
        }
        if (TEST_PAYMENT_SYSTEM.value.equalsIgnoreCase(value)) {
            return TEST_PAYMENT_SYSTEM
        }
        if (DISABLED.value.equalsIgnoreCase(value)) {
            return DISABLED
        }
        return null
    }
}
