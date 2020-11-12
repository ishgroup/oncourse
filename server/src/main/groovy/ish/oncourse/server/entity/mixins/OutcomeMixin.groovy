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

package ish.oncourse.server.entity.mixins

import ish.oncourse.API
import ish.oncourse.server.cayenne.Outcome

class OutcomeMixin {

	/**
	* @return the national code of this outcome.
	 * If it is not VET, then the code of the course or name of prior learning
	*/
	@API
	static String getCode(Outcome self) {
		if (self.module) {
			return self.module.nationalCode
		}

		if (self.enrolment) {
			return self.enrolment.courseClass.uniqueCode
		}
		return self.priorLearning.title
	}

	/**
	* @return the title of this outcome. If it is not VET, then the name of the course or prior learning
	*/
	@API
	static String getName(Outcome self) {
		if (self.module) {
			return self.module.title
		}

		if (self.enrolment) {
			return self.enrolment.courseClass.course.name
		}

		return self.priorLearning.title
	}

	/**
	* @return start date and time of the outcome
	*/
	@API
	static getStartDate(Outcome self) {
		return self.getStartDate()
	}

	/**
	* @return end date and time of the outcome
	*/
	@API
	static getEndDate(Outcome self) {
		return self.getEndDate()
	}

	/**
	* @return reportable hours for this outcome
	*/
	static getReportableHours(Outcome self) {
		return self.getReportableHours()
	}
}
