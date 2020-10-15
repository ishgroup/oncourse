/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.Outcome
import ish.oncourse.webservices.v21.stubs.replication.OutcomeStub
import ish.util.LocalDateUtils

/**
 */
class OutcomeStubBuilder extends AbstractAngelStubBuilder<Outcome, OutcomeStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected OutcomeStub createFullStub(Outcome outcome) {
		def stub = new OutcomeStub()
		stub.setCreated(outcome.getCreatedOn())
		if (outcome.getDeliveryMode() != null) {
			stub.setDeliveryMode(outcome.getDeliveryMode().getDatabaseValue())
		}

		if (outcome.getEnrolment() != null) {
			stub.setEnrolmentId(outcome.getEnrolment().getId())
		} else if (outcome.getPriorLearning() != null) {
			stub.setPriorLearningId(outcome.getPriorLearning().getId())
		}

		if (outcome.getOutcomeFundingSource() != null) {
			stub.setFundingSource(outcome.getOutcomeFundingSource().getDatabaseValue())
		}
		stub.setHoursAttended(outcome.getHoursAttended())
		stub.setModified(outcome.getModifiedOn())
		if (outcome.getModule() != null) {
			stub.setModuleId(outcome.getModule().getWillowId())
		}
		if (outcome.getStatus() != null) {
			stub.setStatus(outcome.getStatus().getDatabaseValue())
		}
		stub.setReportableHours(outcome.getReportableHours())

		def marker = outcome.getMarkedByTutor()
		if (marker != null) {
			stub.setMarkedByTutorId(marker.getId())
		}
		stub.setMarkedByTutorDate(outcome.getMarkedByTutorDate())

		if (outcome.getStartDateOverridden() != null && outcome.getStartDateOverridden()) {
			def startDate = LocalDateUtils.valueToDateAtNoon(outcome.getStartDate())
			stub.setStartDate(startDate)
		}
		if (outcome.getEndDateOverridden() != null && outcome.getEndDateOverridden()) {
			def endDate = LocalDateUtils.valueToDateAtNoon(outcome.getEndDate())
			stub.setEndDate(endDate)
		}

		return stub
	}

}
