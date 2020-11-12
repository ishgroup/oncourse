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

package ish.oncourse.server.export.avetmiss8

import ish.common.types.OutcomeStatus
import ish.oncourse.common.ExportJurisdiction
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.export.avetmiss.AvetmissExportResult

/**


 */
abstract class AvetmissFactory {

    protected static final List<OutcomeStatus> badOutcomes = Arrays.asList(
            OutcomeStatus.STATUS_ASSESSABLE_FAIL,
            OutcomeStatus.STATUS_ASSESSABLE_RCC_NOT_GRANTED,
            OutcomeStatus.STATUS_ASSESSABLE_RPL_NOT_GRANTED,
            OutcomeStatus.STATUS_ASSESSABLE_DET_DID_NOT_START,
            OutcomeStatus.STATUS_SUPERSEDED_QUALIFICATION_QLD,
            OutcomeStatus.STATUS_ASSESSABLE_WITHDRAWN_INCOMPLETE_DUE_TO_RTO,
            OutcomeStatus.STATUS_NON_ASSESSABLE_NOT_COMPLETED)

    protected static final List<OutcomeStatus> goodOutcomes = Arrays.asList(
            OutcomeStatus.STATUS_ASSESSABLE_PASS,
            OutcomeStatus.STATUS_ASSESSABLE_WITHDRAWN,
            OutcomeStatus.STATUS_ASSESSABLE_RPL_GRANTED,
            OutcomeStatus.STATUS_ASSESSABLE_RCC_GRANTED,
            OutcomeStatus.STATUS_NON_ASSESSABLE_COMPLETED,
            OutcomeStatus.STATUS_ASSESSABLE_CREDIT_TRANSFER)


    protected ExportJurisdiction jurisdiction
    protected PreferenceController preferenceController
    protected AvetmissExportResult result
    /**
     * We use ConcurrentMap here since we use parallel stream to handle outcomes.
     */

    /**
     * Make a new factory for one of the AVETMISS export files.
     *
     * @param factories
     * @param jurisdiction
     */
    AvetmissFactory(AvetmissExportResult result, ExportJurisdiction jurisdiction, PreferenceController preferenceController) {
        this.result = result
        this.jurisdiction = jurisdiction
        this.preferenceController = preferenceController
    }
}
