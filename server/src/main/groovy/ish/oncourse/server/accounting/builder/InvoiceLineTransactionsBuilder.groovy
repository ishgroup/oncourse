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

package ish.oncourse.server.accounting.builder

import ish.math.Money
import ish.oncourse.server.accounting.AccountTransactionDetail
import ish.oncourse.server.accounting.TransactionSettings
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.InvoiceLine

import java.time.LocalDate

/**
 for initial invoice transactions
 */
class InvoiceLineTransactionsBuilder implements TransactionsBuilder{

    private InvoiceLine invoiceLine

    private InvoiceLineTransactionsBuilder() {

    }

    static InvoiceLineTransactionsBuilder valueOf(InvoiceLine invoiceLine) {
        InvoiceLineTransactionsBuilder builder = new InvoiceLineTransactionsBuilder()
        builder.invoiceLine = invoiceLine
        builder
    }

    /**
     * invoice is related to two accounts, debtors and prepaid fees. The latter is used for delayed income posting for invoicelines like enrolment fees.
     * creating transactions follows the rules:
     * <ul>
     * <li>the tax amount is posted to the tax and debtors account</li>
     * <li>the remaining prepaid fees are posted to the prepaid fees and debtors account</li>
     * <li>the difference between prepaid fees and total price is posted to the income and debtors account (ie. this is if the class has started)</li>
     * <li>the discount is posted to the income and default (system wide) discount expense account</li>
     * </ul>
     */
    @Override
    TransactionSettings build() {
        List<AccountTransactionDetail> details = []

        LocalDate transactionDate = invoiceLine.invoice.invoiceDate

        Account taxAccount = invoiceLine.tax.receivableFromAccount
        Account assetAccount = invoiceLine.invoice.debtorsAccount
        Account liabilityAccount = invoiceLine.prepaidFeesAccount
        Account incomeAccount = invoiceLine.account
        // if invoice line has linked discount and the discount has defined 'discountCosAccount' - post discount amount to 'discountCosAccount' which defined in discount
        Account discountCosAccount = invoiceLine.cosAccount ?: invoiceLine.invoiceLineDiscounts[0]?.discount?.cosAccount ?: null

        Money amountToPostToIncomeAccount = invoiceLine.finalPriceToPayExTax.subtract(invoiceLine.prepaidFeesRemaining)

        //transaction details for tax:
        details.add(AccountTransactionDetail.valueOf(invoiceLine, invoiceLine.totalTax, taxAccount, assetAccount, transactionDate))
        //transaction details for prepaid fees:
        details.add(AccountTransactionDetail.valueOf(invoiceLine, invoiceLine.prepaidFeesRemaining, liabilityAccount, assetAccount, transactionDate))
        //transaction details for debtors account:
        details.add(AccountTransactionDetail.valueOf(invoiceLine, amountToPostToIncomeAccount, incomeAccount, assetAccount, transactionDate))
        //transaction details for discount:
        if (discountCosAccount) {
            details.add(AccountTransactionDetail.valueOf(invoiceLine, invoiceLine.discountTotalExTax, incomeAccount, discountCosAccount, transactionDate))
        }

        TransactionSettings.valueOf(details).initialTransaction()
    }
}
