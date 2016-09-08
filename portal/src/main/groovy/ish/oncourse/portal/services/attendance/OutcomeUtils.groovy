package ish.oncourse.portal.services.attendance

import ish.common.CalculateEndDate
import ish.common.CalculateStartDate
import ish.oncourse.model.Outcome

class OutcomeUtils {
	
	def static Date getOutcomeStartDate(Outcome outcome) {
		outcome.startDate?: new CalculateStartDate(outcome).calculate();
	}

	def static Date getOutcomeEndDate(Outcome outcome) {
		outcome.endDate?: new CalculateEndDate(outcome).calculate();
	}
	
}
