package ish.oncourse.portal.services.attendance

import ish.common.CalculateEndDate
import ish.common.CalculateStartDate
import ish.oncourse.model.Outcome

class OutcomeUtils {

	def static boolean isEditingAllowed(Outcome outcome) {
		getOutcomeStartDate(outcome).before(new Date()) && !linkedToActiveCertificate(outcome)
	}

	def static boolean linkedToActiveCertificate(Outcome outcome) {
		outcome.certificateOutcomes*.certificate.findAll{c -> c.revokedWhen == null}.size() > 0
	}
	
	def static Date getOutcomeStartDate(Outcome outcome) {
		outcome.startDate?: new CalculateStartDate(outcome, true).calculate();
	}

	def static Date getOutcomeEndDate(Outcome outcome) {
		outcome.endDate?: new CalculateEndDate(outcome, true).calculate();
	}
	
}
