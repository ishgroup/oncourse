/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.willow.checkout.windcave

enum WindcaveResponseCode {

    _00("00", "Transaction Approved"),

    _05("05", "Credit transaction is not allowed"),

    _12("12", "Invalid Transaction - declined"),

    _30("30", "The message contains Invalid Data"),

    _51("51", "Not sufficient Funds in the Account"),

    _54("54", "Card Expired"),

    _76("76", "Transaction Declined"),

    U9("U9", "Timeout"),

    J1("J1", "Refund Not Matched"),
    J2("J2", "Refund Not Matched"),
    J3("J3", "Refund Not Matched"),

    J5("J5", "Already refunded");


    private String code
    private String explanation

    WindcaveResponseCode(String code, String explanation) {
        this.code = code
        this.explanation = explanation
    }

    static String getExplanationByCode(String code) {
        return values().find {it.code == code}?.explanation
    }
}