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


	public InvoiceLine getOriginalInvoiceLine(Enrolment self) {
		return self.getInvoiceLines().stream()
				.sorted(Comparator.comparing(il -> il.getInvoice().getInvoiceNumber()))
				.findFirst()
				.orElse(null);
	}

}
