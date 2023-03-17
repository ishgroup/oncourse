/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.checkout.gateway.eway

enum EWayResponseMessage {

    A2000("A2000", "Transaction Approved", "Successful"),
    A2008("A2008", "Honour With Identification", "Successful"),
    A2010("A2010", "Approved For Partial Amount", "Successful"),
    A2011("A2011", "Approved, VIP", "Successful"),
    A2016("A2016", "Approved, Update Track 3", "Successful"),
    D4401("D4401", "Refer to Issuer", "Failed"),
    D4402("D4402", "Refer to Issuer, special", "Failed"),
    D4403("D4403", "No Merchant", "Failed"),
    D4404("D4404", "Pick Up Card", "Failed"),
    D4405("D4405", "Do Not Honour", "Failed"),
    D4406("D4406", "Error", "Failed"),
    D4407("D4407", "Pick Up Card, Special", "Failed"),
    D4409("D4409", "Request In Progress", "Failed"),
    D4412("D4412", "Invalid Transaction", "Failed"),
    D4413("D4413", "Invalid Amount", "Failed"),
    D4414("D4414", "Invalid Card Number", "Failed"),
    D4415("D4415", "No Issuer", "Failed"),
    D4417("D4417", "3D Secure Error", "Failed"),
    D4419("D4419", "Re-enter Last Transaction", "Failed"),
    D4421("D4421", "No Action Taken", "Failed"),
    D4422("D4422", "Suspected Malfunction", "Failed"),
    D4423("D4423", "Unacceptable Transaction Fee", "Failed"),
    D4425("D4425", "Unable to Locate Record On File", "Failed"),
    D4430("D4430", "Format Error", "Failed"),
    D4431("D4431", "Bank Not Supported By Switch", "Failed"),
    D4433("D4433", "Expired Card, Capture", "Failed"),
    D4434("D4434", "Suspected Fraud, Retain Card", "Failed"),
    D4435("D4435", "Card Acceptor, Contact Acquirer, Retain Card", "Failed"),
    D4436("D4436", "Restricted Card, Retain Card", "Failed"),
    D4437("D4437", "Contact Acquirer Security Department, Retain Card", "Failed"),
    D4438("D4438", "PIN Tries Exceeded, Capture", "Failed"),
    D4439("D4439", "No Credit Account", "Failed"),
    D4440("D4440", "Function Not Supported", "Failed"),
    D4441("D4441", "Lost Card", "Failed"),
    D4442("D4442", "No Universal Account", "Failed"),
    D4443("D4443", "Stolen Card", "Failed"),
    D4444("D4444", "No Investment Account", "Failed"),
    D4450("D4450", "Click to Pay Transaction Error", "Failed"),
    D4451("D4451", "Insufficient Funds", "Failed"),
    D4452("D4452", "No Cheque Account", "Failed"),
    D4453("D4453", "No Savings Account", "Failed"),
    D4454("D4454", "Expired Card", "Failed"),
    D4455("D4455", "Incorrect PIN", "Failed"),
    D4456("D4456", "No Card Record", "Failed"),
    D4457("D4457", "Function Not Permitted to Cardholder", "Failed"),
    D4458("D4458", "Function Not Permitted to Terminal", "Failed"),
    D4459("D4459", "Suspected Fraud", "Failed"),
    D4460("D4460", "Acceptor Contact Acquirer", "Failed"),
    D4461("D4461", "Exceeds Withdrawal Limit", "Failed"),
    D4462("D4462", "Restricted Card", "Failed"),
    D4463("D4463", "Security Violation", "Failed"),
    D4464("D4464", "Original Amount Incorrect", "Failed"),
    D4465("D4465", "Withdrawal Frequency Limit Exceeded", "Failed"),
    D4466("D4466", "Acceptor Contact Acquirer, Security", "Failed"),
    D4467("D4467", "Capture Card", "Failed"),
    D4475("D4475", "PIN Tries Exceeded", "Failed"),
    D4476("D4476", "Invalidate Txn Reference", "Failed"),
    D4481("D4481", "Accumulated Transaction Counter (Amount) Exceeded", "Failed"),
    D4482("D4482", "CVV Validation Error", "Failed"),
    D4483("D4483", "Acquirer Is Not Accepting Transactions From You At This Time", "Failed"),
    D4484("D4484", "Acquirer Is Not Accepting This Transaction", "Failed"),
    D4490("D4490", "Cut off In Progress", "Failed"),
    D4491("D4491", "Card Issuer Unavailable", "Failed"),
    D4492("D4492", "Unable To Route Transaction", "Failed"),
    D4493("D4493", "Cannot Complete, Violation Of The Law", "Failed"),
    D4494("D4494", "Duplicate Transaction", "Failed"),
    D4495("D4495", "Amex Declined", "Failed"),
    D4496("D4496", "System Error", "Failed"),
    D4497("D4497", "MasterPass Error", "Failed"),
    D4498("D4498", "PayPal Create Transaction Error", "Failed"),
    D4499("D4499", "Invalid Transaction for Auth/Void", "Failed");


    private String code
    private String explanation
    private String message

    EWayResponseMessage(String code, String explanation, String message) {
        this.code = code
        this.explanation = explanation
        this.message = message
    }
    
    String toString() {
        return "$explanation ($message)"
    }

    static String getExplanationByCode(String code) {
        return values().find {it.code == code}?.toString()
    }
}
