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
package ish.messaging;

import ish.common.payable.EnrolmentInterface;
import ish.common.types.EnrolmentStatus;
import ish.oncourse.cayenne.PersistentObjectI;
import ish.oncourse.server.cayenne.InvoiceLine;
import ish.oncourse.server.cayenne.Student;

import java.util.Date;
import java.util.List;

public interface IEnrolment extends PersistentObjectI {

    String STUDENT_KEY = "student";
	String STATUS_PROPERTY = "status";

	String IS_SUCCESSFULL_OR_QUEUED = "is_successfull_or_queued";

	ICourseClass getCourseClass();

	List<InvoiceLine> getInvoiceLines();

    InvoiceLine getOriginalInvoiceLine();

	Student getStudent();

	EnrolmentStatus getStatus();

	List<? extends IOutcome> getOutcomes();

	Date getModifiedOn();

}
