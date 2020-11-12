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

package ish.util;

import ish.common.types.CreditCardType;
import ish.oncourse.cayenne.PaymentInterface;
import ish.oncourse.common.BankingType;
import ish.oncourse.server.cayenne.Account;
import ish.oncourse.server.cayenne.Banking;
import ish.oncourse.server.cayenne.PaymentIn;
import ish.oncourse.server.cayenne.PaymentOut;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;

import java.time.LocalDate;

public class SetBankingMethod {

    private PaymentInterface payment;

    private LocalDate settlementDate;

    private SetBankingMethod() {

    }

    public static SetBankingMethod valueOf(PaymentInterface payment) {
        var setBankingMethod = new SetBankingMethod();
        setBankingMethod.payment = payment;
        return setBankingMethod;
    }


    public static SetBankingMethod valueOf(PaymentInterface payment, LocalDate settlementDate) {
        var setBankingMethod = new SetBankingMethod();
        setBankingMethod.payment = payment;
        setBankingMethod.settlementDate = settlementDate;
        return setBankingMethod;
    }

    public void set() {
        var context = payment.getObjectContext();

        BankingType bankingType;

        if (CreditCardType.MASTERCARD.equals(payment.getCreditCardType()) || CreditCardType.VISA.equals(payment.getCreditCardType())) {
            bankingType = BankingType.AUTO_MCVISA;
        } else if (CreditCardType.AMEX.equals(payment.getCreditCardType())) {
            bankingType = BankingType.AUTO_AMEX;
        } else {
            bankingType = BankingType.AUTO_OTHER;
        }

        if (settlementDate == null) {
            settlementDate = payment instanceof PaymentIn ?
                    ((PaymentIn) payment).getPaymentDate() : ((PaymentOut) payment).getPaymentDate();
        }

        var assetAccount = payment instanceof PaymentIn ?
                ((PaymentIn)payment).getAccountIn() : ((PaymentOut)payment).getAccountOut();

        Banking banking;

        // first, try to find existing banking in the database
        banking = ObjectSelect.query(Banking.class)
                .where(Banking.TYPE.eq(bankingType))
                .and(Banking.SETTLEMENT_DATE.eq(settlementDate))
                .and(Banking.ASSET_ACCOUNT.eq(assetAccount))
                .selectFirst(context);

        // it is also worth checking if there is uncommitted banking matching our date
        if (banking == null) {
            banking = context.newObjects().stream()
                    .filter(obj -> obj instanceof Banking)
                    .map(obj -> (Banking) obj)
                    .filter(b -> settlementDate.isEqual(b.getSettlementDate()) &&
                            bankingType.equals(b.getType()) &&
                            assetAccount.equals(b.getAssetAccount()))
                    .findAny()
                    .orElse(null);
        }

        // still nothing... create new banking record with required settlement date
        if (banking == null) {
            banking = context.newObject(Banking.class);
            banking.setType(bankingType);
            banking.setSettlementDate(settlementDate);
            banking.setAssetAccount(assetAccount);
        }

        payment.setBanking(banking);
    }

}
