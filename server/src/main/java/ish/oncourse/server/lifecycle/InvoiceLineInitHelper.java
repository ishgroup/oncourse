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
package ish.oncourse.server.lifecycle;

import ish.common.types.PaymentType;
import ish.math.Money;
import ish.oncourse.server.cayenne.*;
import ish.oncourse.server.cayenne.glue._PaymentInLine;
import ish.util.AccountUtil;
import org.apache.cayenne.ObjectContext;

import java.util.List;

/**
 * Helper class for initializing account and tax for invoice lines coming from willow.
 *
 * This is a workaround for issue with with accounts not being set properly for invoice lines
 * created by willow.
 */
public class InvoiceLineInitHelper {

	public void processInvoiceLine(InvoiceLine invoiceLine) {

		// try to assign account and tax using reverse payment
		processUsingReversePayment(invoiceLine);

		// if no reverse payment linked to the invoice, then try to assign values using enrolment
		if (invoiceLine.getAccount() == null) {
			processUsingEnrolment(invoiceLine);

			// if no enrolments linked to the invoice, then try to assign values using product
			if (invoiceLine.getAccount() == null) {
				processUsingProduct(invoiceLine);

				// if there is still nothing, then assign default account and tax from preferences
				if (invoiceLine.getAccount() == null) {
					assignDefaults(invoiceLine);
				}
			}
		}
	}

	private void processUsingReversePayment(InvoiceLine invoiceLine) {

		// an ugly logic searching for originating invoice lines for provided reverse invoice line
		// using sorderOrder field. Lookup is goes following way:
		// Invoice -> PaymentInLine -> reverse PaymentIn -> PaymentInLine -> originating invoice -> originating invoice line

		var reverseInvoice = invoiceLine.getInvoice();

		if (reverseInvoice.getPaymentInLines().size() == 1) {
			var reversePaymentLine = reverseInvoice.getPaymentInLines().get(0);

			if (PaymentType.REVERSE.equals(reversePaymentLine.getPaymentIn().getPaymentMethod().getType())) {
				var reversePayment = reversePaymentLine.getPaymentIn();

				// original payment line should be the only payment line of a positive amount
				var originalPaymentLines =
						_PaymentInLine.AMOUNT.gt(Money.ZERO()).filterObjects(reversePayment.getPaymentInLines());

				if (originalPaymentLines.size() == 1) {
					var orginalPaymentLine = originalPaymentLines.get(0);

					for (var originalInvoiceLine : orginalPaymentLine.getInvoice().getInvoiceLines()) {
						if (invoiceLine.getSortOrder().equals(originalInvoiceLine.getSortOrder())) {

							// if original invoice line comes from willow in the same batch with reverse line
							// reverse line can get to processing first, in this case we need to call processing
							// logic for original invoice lines here
							if (originalInvoiceLine.getAccount() == null) {
								processInvoiceLine(originalInvoiceLine);
							}

							invoiceLine.setAccount(originalInvoiceLine.getAccount());
							invoiceLine.setTax(originalInvoiceLine.getTax());

							recalculateTax(invoiceLine);
						}
					}
				}
			}
		}
	}

	private void processUsingEnrolment(InvoiceLine invoiceLine) {

		// assigns account and tax from related enrolment's class

		if (invoiceLine.getEnrolment() != null) {
			var enrolment = invoiceLine.getEnrolment();

			invoiceLine.setAccount(enrolment.getCourseClass().getIncomeAccount());
			var taxOverride = invoiceLine.getInvoice().getContact().getTaxOverride();
			if (taxOverride != null) {
				invoiceLine.setTax(taxOverride);
			} else {
				invoiceLine.setTax(enrolment.getCourseClass().getTax());
			}

			recalculateTax(invoiceLine);
		}
	}

	private void processUsingProduct(InvoiceLine invoiceLine) {

		// assigns account and tax from first related product item's product

		if (!invoiceLine.getProductItems().isEmpty()) {
			var productItem = invoiceLine.getProductItems().get(0);

			var account = productItem.getProduct() instanceof VoucherProduct ?
					((VoucherProduct) productItem.getProduct()).getLiabilityAccount() :
					productItem.getProduct().getIncomeAccount();

			invoiceLine.setAccount(account);

			var taxOverride = invoiceLine.getInvoice().getContact().getTaxOverride();
			if (taxOverride != null) {
				invoiceLine.setTax(taxOverride);
			} else {
				invoiceLine.setTax(productItem.getProduct().getTax());
			}

			recalculateTax(invoiceLine);
		}
	}

	private void assignDefaults(InvoiceLine invoiceLine) {
		var aContext = invoiceLine.getContext();

		invoiceLine.setAccount(AccountUtil.getDefaultStudentEnrolmentsAccount(aContext, Account.class));
	}

	private void recalculateTax(InvoiceLine invoiceLine) {
		invoiceLine.getInvoice().updateAmountOwing();
	}
}
