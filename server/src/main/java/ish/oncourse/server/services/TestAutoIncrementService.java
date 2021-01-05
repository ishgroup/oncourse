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
package ish.oncourse.server.services;

import com.google.inject.Inject;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.Invoice;
import ish.oncourse.server.cayenne.Student;
import org.apache.cayenne.query.ObjectSelect;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static ish.oncourse.server.api.servlet.ApiFilter.validateOnly;


public class TestAutoIncrementService implements IAutoIncrementService {

	private AtomicLong studentNumber;
	private AtomicLong invoiceNumber;
	
	private ICayenneService  cayenneService;
	private AtomicBoolean initialized = new AtomicBoolean();
	
	@Inject
	public TestAutoIncrementService(ICayenneService  cayenneService) {
		this.cayenneService = cayenneService;
	}
	
	private void init() {
		var context = cayenneService.getNewNonReplicatingContext();

		var invoice = ObjectSelect.query(Invoice.class)
				.orderBy(Invoice.INVOICE_NUMBER.desc())
				.selectFirst(context);
		invoiceNumber = new AtomicLong(invoice != null ? invoice.getInvoiceNumber() : 0);

		var student = ObjectSelect.query(Student.class)
				.orderBy(Student.STUDENT_NUMBER.desc())
				.selectFirst(context);
		studentNumber = new AtomicLong(student != null ? student.getStudentNumber() : 0);
	}

	public Long getNextStudentNumber() {
		if (!initialized.getAndSet(true)) {
			init();
		}
		
		if (validateOnly.get() != null && validateOnly.get()) {
			return 100L;
		}
		return studentNumber.incrementAndGet();
	}

	public Long getNextInvoiceNumber() {
		if (!initialized.getAndSet(true)) {
			init();
		}
		
		if (validateOnly.get() != null && validateOnly.get()) {
			return 100L;
		}
		return invoiceNumber.incrementAndGet();
	}
}
