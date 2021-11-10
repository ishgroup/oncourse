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

package ish.oncourse.entity.delegator;

import ish.common.payable.EnrolmentInterface;
import ish.oncourse.cayenne.ModuleInterface;
import ish.oncourse.cayenne.OutcomeInterface;
import ish.oncourse.server.cayenne.Outcome;
import ish.util.LocalDateUtils;

import java.time.LocalDate;
import java.util.Date;

public class OutcomeDelegator implements OutcomeInterface {

    private Outcome outcome;

    private OutcomeDelegator() {

    }

    @Override
    public ModuleInterface getModule() {
        return outcome.getModule();
    }

    @Override
    public EnrolmentInterface getEnrolment() {
        return outcome.getEnrolment();
    }

    @Override
    public Date getStartDate() {
        LocalDate startDate = outcome.getStartDate();
        return LocalDateUtils.valueToDateAtNoon(startDate);
    }

    @Override
    public Date getEndDate() {
        LocalDate endDate = outcome.getEndDate();
        return LocalDateUtils.valueToDateAtNoon(endDate);
    }

    public static OutcomeDelegator valueOf(Outcome outcome) {
        OutcomeDelegator outcomeDelegator = new OutcomeDelegator();
        outcomeDelegator.outcome = outcome;
        return outcomeDelegator;
    }

    public Outcome getOutcome() {
        return outcome;
    }
}
