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

import ish.common.types.OutcomeStatus;
import ish.messaging.IOutcome;
import ish.oncourse.server.cayenne.Certificate;
import ish.oncourse.server.cayenne.CertificateOutcome;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 */
public class CertificateService {

	public boolean isQualification(Certificate certificate) {
		if (certificate.getIsQualification() != null && certificate.getIsQualification()) {
			for (CertificateOutcome certificateOutcome : certificate.getCertificateOutcomes()) {
				IOutcome outcome = certificateOutcome.getOutcome();
				if (outcome == null || outcome.getStatus() == null) {
					return false;
				}
				OutcomeStatus status = outcome.getStatus();
				return !(status == OutcomeStatus.STATUS_ASSESSABLE_FAIL || status == OutcomeStatus.STATUS_ASSESSABLE_WITHDRAWN ||
						status == OutcomeStatus.STATUS_NON_ASSESSABLE_NOT_COMPLETED);
			}
		}
		return false;
	}

	public List<? extends IOutcome> getSuccessfulOutcomes(Certificate certificate) {
		List<IOutcome> result = null;
		if (certificate.getCertificateOutcomes() != null) {
			result = new ArrayList<>();
			for (CertificateOutcome certificateOutcome : certificate.getCertificateOutcomes()) {
				if (certificateOutcome.getOutcome() != null &&
						certificateOutcome.getOutcome().getStatus() != null &&
						certificateOutcome.getOutcome().getStatus().isAssessable()) {
					result.add(certificateOutcome.getOutcome());
				}
			}
		}
		return result;
	}

	/**
	 * Faked flattened join to Outcome.
	 *
	 * @return the list of outcomes across the join CertificateOutcome
	 */
	public List<? extends IOutcome> getOutcomes(Certificate certificate) {
		List<IOutcome> result = null;
		if (certificate.getCertificateOutcomes() != null) {
			result = new ArrayList<>();
			for (CertificateOutcome certificateOutcome : certificate.getCertificateOutcomes()) {
				if (certificateOutcome.getOutcome() != null) {
					result.add(certificateOutcome.getOutcome());
				}
			}
		}
		return result;
	}

	public LocalDate getCompletedOn(Certificate certificate) {
		// making sure outcomes with null end date do not cause exceptions, filtering them out
		List<IOutcome> filteredOutcomes = new ArrayList<>();

		for (IOutcome outcome : getOutcomes(certificate)) {
			if (outcome.getEndDate() != null) {
				filteredOutcomes.add(outcome);
			}
		}

		if (!filteredOutcomes.isEmpty()) {
			// sort outcomes by end date in DESCENDING order
			Collections.sort(filteredOutcomes, new Comparator<IOutcome>() {
				@Override
				public int compare(IOutcome o1, IOutcome o2) {
					return o2.getEndDate().compareTo(o1.getEndDate());
				}
			});
			return filteredOutcomes.get(0).getEndDate();
		}
		return null;
	}

	public LocalDate getCommencedOn(Certificate certificate) {
		LocalDate commencementDate = null;

		for (IOutcome outcome : getOutcomes(certificate)) {
			LocalDate outcomeCommencement = outcome.getStartDate();

			if (outcomeCommencement == null) {
				outcomeCommencement = outcome.getStartDate();
			}

			if (commencementDate == null) {
				commencementDate = outcomeCommencement;
			} else if (outcomeCommencement != null && outcomeCommencement.isBefore(commencementDate)) {
				commencementDate = outcomeCommencement;
			}
		}
		return commencementDate;
	}
}
