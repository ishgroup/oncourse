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

import ish.common.types.DeliveryMode;
import ish.common.types.OutcomeStatus;
import ish.oncourse.cayenne.PersistentObjectI;
import ish.oncourse.server.cayenne.CertificateOutcome;
import ish.oncourse.server.cayenne.Module;
import ish.oncourse.server.cayenne.PriorLearning;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 */
public interface IOutcome extends PersistentObjectI {

	Module getModule();

	IEnrolment getEnrolment();

	PriorLearning getPriorLearning();

	List<? extends CertificateOutcome> getCertificateOutcomes();

	OutcomeStatus getStatus();

	LocalDate getStartDate();

	LocalDate getEndDate();

	BigDecimal getReportableHours();

	DeliveryMode getDeliveryMode();
}
