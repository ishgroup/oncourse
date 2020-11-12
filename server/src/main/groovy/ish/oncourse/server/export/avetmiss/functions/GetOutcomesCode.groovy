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

package ish.oncourse.server.export.avetmiss.functions

import ish.common.types.OutcomeStatus
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Outcome

/**
 * Unit of study completion status
 * If all outcome.status = 40, then 1 - Withdrew without penalty.
 * If any outcome.status = bad outcome, then 2 - Failed.
 * If any outcome status =  null, 70 or 90, then 4 - Status not yet determined.
 * If all outcome status = 51, then 5 - RPL.
 * If all outcome.status = good outcomes, then 3 - Successfully completed all the requirements
 */
class GetOutcomesCode {
    private Enrolment e

    public static final List<OutcomeStatus> badOutcomes =
            Arrays.asList(
                    OutcomeStatus.STATUS_NON_ASSESSABLE_NOT_COMPLETED,
                    OutcomeStatus.STATUS_ASSESSABLE_FAIL,
                    OutcomeStatus.STATUS_ASSESSABLE_RCC_NOT_GRANTED,
                    OutcomeStatus.STATUS_ASSESSABLE_RPL_NOT_GRANTED,
                    OutcomeStatus.STATUS_ASSESSABLE_DET_DID_NOT_START,
                    OutcomeStatus.STATUS_SUPERSEDED_QUALIFICATION_QLD
            )

    public static final List<OutcomeStatus> goodOutcomes =
            Arrays.asList(
                    OutcomeStatus.STATUS_ASSESSABLE_PASS,
                    OutcomeStatus.STATUS_ASSESSABLE_RPL_GRANTED,
                    OutcomeStatus.STATUS_ASSESSABLE_RCC_GRANTED,
                    OutcomeStatus.STATUS_NON_ASSESSABLE_COMPLETED,
                    OutcomeStatus.STATUS_ASSESSABLE_CREDIT_TRANSFER
            )


    static GetOutcomesCode valueOf(Enrolment e) {
        def getOutcomesCode = new GetOutcomesCode()
        getOutcomesCode.e = e

        return getOutcomesCode
    }

    int get() {
        def outcomes = e.getOutcomes()
        def anyBad = Outcome.STATUS.in(badOutcomes)

        if (anyBad.filterObjects(outcomes).size() > 0) {
            return 2
        }

        def anyNotYetDetermined = Outcome.STATUS.in(OutcomeStatus.STATUS_NOT_SET, OutcomeStatus.STATUS_NO_RESULT_QLD, OutcomeStatus.STATUS_ASSESSABLE_CONTINUING_ENROLMENT)
        if (outcomes.isEmpty() || anyNotYetDetermined.filterObjects(outcomes).size() > 0) {
            return 4
        }

        def allWithoutPenalty = Outcome.STATUS.eq(OutcomeStatus.STATUS_ASSESSABLE_WITHDRAWN)

        if (allWithoutPenalty.filterObjects(outcomes).size() == outcomes.size()) {
            return 1
        }

        def allRPL = Outcome.STATUS.eq(OutcomeStatus.STATUS_ASSESSABLE_RPL_GRANTED)

        if (allRPL.filterObjects(outcomes).size() == outcomes.size()) {
            return 5
        }

        def allGood = Outcome.STATUS.in(goodOutcomes)
        if (allGood.filterObjects(outcomes).size() == outcomes.size()) {
            return 3
        }

        return 4
    }
}
