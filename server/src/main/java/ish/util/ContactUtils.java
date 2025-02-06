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

import ish.common.types.PaymentStatus;
import ish.math.Money;
import ish.oncourse.cayenne.ContactInterface;
import ish.oncourse.cayenne.FinancialItem;
import ish.oncourse.cayenne.InvoiceInterface;
import ish.oncourse.cayenne.PaymentInterface;
import ish.oncourse.function.GetDateOrderingForFinancialItem;
import org.apache.cayenne.query.Ordering;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ContactUtils {

	/**
	 * Forms list of invoices and payments for contact wrapped into {@link FinancialItem} object.
	 *
	 * @return list of financial items for contact
	 */
	public static <K extends FinancialItem> List<K> getFinancialItems(ContactInterface contact, Class<K> proxyClass) {
		List<K> financials = new ArrayList<>();

		for (InvoiceInterface invoice : contact.getInvoices()) {
			if (!invoice.isNewRecord()) {
				try {
					Constructor<K> constructor = proxyClass.getConstructor(InvoiceInterface.class);
					K item = constructor.newInstance(invoice);
					financials.add(item);
				} catch (Exception e) {
					throw new RuntimeException(String.format("Failed to instantiate '%s' FinancialItem for Invoice object.", proxyClass.getClass().getSimpleName()), e);
				}
			}
		}

		for (PaymentInterface payment : contact.getPayments()) {
			if (!payment.isNewRecord()) {
				try {
					Constructor<K> constructor = proxyClass.getConstructor(PaymentInterface.class);
					K item = constructor.newInstance(payment);
					financials.add(item);
				} catch (Exception e) {
					throw new RuntimeException(String.format("Failed to instantiate '%s' FinancialItem for Payment object.", proxyClass.getClass().getSimpleName()), e);
				}
			}
		}

		//we should order by two field to sort items with same FinancialItem.DATE (which are LocalDate)
		//we use this ordering for calculating balance which we show on ui
		Ordering.orderList(financials, GetDateOrderingForFinancialItem.valueOf().get());

		Money total = Money.ZERO();

		for (FinancialItem item : financials) {
			if (FinancialItem.FinancialItemType.INVOICE.equals(item.getFinancialItemType())) {
				total = total.add(item.getAmount());
			} else if (!PaymentStatus.STATUSES_FAILED.contains(item.getPaymentStatus())) {
				if (FinancialItem.FinancialItemType.PAYMENT_IN.equals(item.getFinancialItemType())) {
					total = total.subtract(item.getAmount());
				} else {
					total = total.add(item.getAmount());
				}
			}
			item.setTotal(total);
		}
		return financials;
	}
}
