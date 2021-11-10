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

package ish.print;

import ish.print.transformations.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 */
public class PrintTransformationsFactory {

	/**
	 * Map containing all print transformations defined in the system.
	 */
	private static final Map<String, Map<String, PrintTransformation>> TRANSFORMATION_MAP = mapFromValues(
			"Account", mapFromValues("AccountTransaction", new AccountAccountTransactionTransformation()),

			"AccountTransaction", mapFromValues("AccountTransaction", new AccountTransactionTransformation("AccountTransaction")),

			"Certificate", mapFromValues("Certificate", new CertificateReportTransformation()),

			"Contact", mapFromValues("AccountTransaction", new ContactAccountTransactionTransformation(),
									 "Contact", new StatementLineReportTransformation()),

			"CourseClass", mapFromValues("Enrolment", new CourseClassEnrolmentTransformation(),
										 "Outcome", new CourseClassOutcomeTransformation(),
										 "Session", new CourseClassSessionTransformation(),
										 "TutorAttendance",new CourseClassTutorAttendanceTransformation()),

			"Discount", mapFromValues("Discount", new DiscountDiscountTransformation()),

			"PaymentIn", mapFromValues("PaymentInterface", new PaymentInterfaceTransformation("PaymentIn")),

			"PaymentOut", mapFromValues("PaymentInterface", new PaymentInterfaceTransformation("PaymentOut")),

			"Payslip", mapFromValues("PayLine", new PayslipPayLineTransformation()),

			"Tag", mapFromValues("Contact", new TagContactTransformation()),

			"Site", mapFromValues("Session", new SiteSessionTransformation()),

			"Room", mapFromValues("Session", new RoomSessionTransformation()),

			"Banking", mapFromValues("PaymentInterface", new BankingPaymentInterfaceTransformation()),

			"ProductItem", mapFromValues("Voucher", new ProductItemTransformation(),
										"Article", new ProductItemTransformation(),
										"Membership", new ProductItemTransformation()),

			"AbstractInvoice", mapFromValues("Invoice", new AbstractInvoiceTransformation(),
											"Quote", new AbstractQuoteTransformation()),

			"Invoice", mapFromValues("InvoiceLine", new InvoiceTransformation()),
			"Quote", mapFromValues("QuoteLine", new QuoteTransformation())

	);

	public static PrintTransformation getPrintTransformationFor(String inputEnity, String outputEntity, String reportCode) {
		if (StringUtils.isEmpty(inputEnity) || StringUtils.isEmpty(outputEntity)) {
			return null;
		}

		try {

			PrintTransformation transform = null;

			// try to find defined transformation for entitues
			if (TRANSFORMATION_MAP.get(inputEnity) != null) {
				transform = TRANSFORMATION_MAP.get(inputEnity).get(outputEntity);
			}

			if (transform != null) {
				if (transform.getReportCodes() == null || transform.getReportCodes().contains(reportCode)) {
					return transform;
				}
			}

			if (!inputEnity.equals(outputEntity)) {

				// try to create a generic transform, works only for entities with a simple relationship

				transform = new PrintTransformation();
				transform.setInputEntityName(inputEnity);
				transform.setOutputEntityName(outputEntity);

				String relName = inputEnity.substring(0,1).toLowerCase() + inputEnity.substring(1);
				transform.setTransformationFilter(relName+".id in $sourceIds");

				return transform;
			}

			return null;
		} catch (Exception e) {
			throw new IllegalStateException("The print transform could not be executed.", e);
		}
	}

	/**
	 * Simple helper class constructing {@link HashMap} from provided key value pairs.
	 */
	private static <K, V> Map<K, V> mapFromValues(Object... values) {
		if (values.length % 2 != 0) {
			throw new IllegalArgumentException("Number of arguments should be even.");
		}

		Map<K, V> map = new HashMap<>();

		for (int i = 0; i < values.length; i += 2) {
			map.put((K) values[i], (V) values[i + 1]);
		}

		return map;
	}
}
