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

package ish.oncourse.function;

import ish.oncourse.server.cayenne.Outcome;

import java.math.BigDecimal;

public class CalculateOutcomeReportableHours {

    Outcome outcome;

    private CalculateOutcomeReportableHours() {}

    public static CalculateOutcomeReportableHours valueOf(Outcome o) {
        CalculateOutcomeReportableHours obj = new CalculateOutcomeReportableHours();
        obj.outcome = o;
        return obj;
    }

    public BigDecimal calculate() {
        BigDecimal result = BigDecimal.ZERO;

        if (outcome.getModule() != null) {
            result = outcome.getModule().getNominalHours() != null ? outcome.getModule().getNominalHours() : BigDecimal.ZERO;
        } else if (outcome.getEnrolment() != null) {
            if (outcome.getEnrolment().getCourseClass().getIsDistantLearningCourse()) {
                result = outcome.getEnrolment().getCourseClass().getExpectedHours();
            } else {
                result = CalculateCourseClassReportableHours.valueOf(outcome.getEnrolment().getCourseClass()).calculate();
            }
        }

        return result;
    }
}
