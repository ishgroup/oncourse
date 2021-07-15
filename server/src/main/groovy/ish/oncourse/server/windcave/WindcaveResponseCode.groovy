/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.windcave

enum WindcaveResponseCode {

    _00("00", "Transaction Approved", "APPROVED"),

    _04("04", "Pick up card", "DECLINED"),

    _05("05", "Credit transaction is not allowed", "DO NOT HONOUR"),

    _12("12", "Invalid Transaction - declined", "ERROR - INVLD TRAN"),

    _30("30", "The message contains Invalid Data", "ERROR - INVLD FORMAT"),

    _51("51", "Not sufficient Funds in the Account", "DECLINED"),

    _54("54", "Card Expired", "Card Expired"),

    _76("76", "Transaction Declined", "DECLINED"),

    U9("U9", "Transaction Declined", "TIMEOUT"),

    Z3("Z3", "Invalid Product - Amount Too Large", ""),

    J1("J1", "Refund Not Matched", ""),
    J2("J2", "Refund Not Matched", ""),
    J3("J3", "Refund Not Matched", ""),

    J5("J5", "Already refunded", "");


    private String code
    private String explanation
    private String message

    WindcaveResponseCode(String code, String explanation, String message) {
        this.code = code
        this.explanation = explanation
        this.message = message
    }
    
    String toStriing() {
        return "$explanation ($message)"
    }

    static String getExplanationByCode(String code) {
        return values().find {it.code == code}?.toString()
    }
}
