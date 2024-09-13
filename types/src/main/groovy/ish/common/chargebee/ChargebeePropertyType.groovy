/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.common.chargebee

enum ChargebeePropertyType {
    SMS("chargebee.sms.item", "Sms item (addon)"),
    TOTAL_CREDIT_PAYMENT_IN("chargebee.total.paymentIn.item", "Total payments in item (addon)"),
    TOTAL_CREDIT_WEB_PAYMENT_IN("chargebee.total.paymentIn.web.item", "Total web payments in item (addon)"),
    TOTAL_CREDIT_PAYMENT("chargebee.total.payment", "Total payment item"),
    TOTAL_CORPORATE_PASS("chargebee.total.corporatePass", "Total corporate pass item"),
    TOTAL_OFFICE_PAYMENT_IN_NUMBER("chargebee.office.paymentIn.number", "Total card transactions item"),
    SITE("chargebee.site", "Site prefix"),
    API_KEY("chargebee.apiKey", "Api key"),

    public static final String ADDONS_SEPARATOR = ";"

    private String dbPropertyName
    private String description

    ChargebeePropertyType(String dbPropertyName, String description) {
        this.dbPropertyName = dbPropertyName
        this.description = description
    }

    String getDbPropertyName() {
        return dbPropertyName
    }

    String getDescription() {
        return description
    }

    static List<ChargebeePropertyType> getItems(){
        return values().findAll {it != SITE && it != API_KEY}
    }
}