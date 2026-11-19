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

package ish.oncourse.entity.services;

import ish.oncourse.server.cayenne.Enrolment;
import ish.oncourse.server.cayenne.InvoiceLine;

import java.util.Comparator;

public class EnrolmentService {

	/**
	 * Orders invoice lines by invoice number, oldest invoice first.
	 * An invoice gets its number on commit, so lines of a not yet committed invoice go last as the newest ones.
	 */
	public static final Comparator<InvoiceLine> BY_INVOICE_NUMBER = Comparator.comparing(
			(InvoiceLine il) -> il.getInvoice().getInvoiceNumber(),
			Comparator.nullsLast(Comparator.naturalOrder()));


	public InvoiceLine getOriginalInvoiceLine(Enrolment self) {
		return self.getInvoiceLines().stream()
				.sorted(BY_INVOICE_NUMBER)
				.findFirst()
				.orElse(null);
	}

}
