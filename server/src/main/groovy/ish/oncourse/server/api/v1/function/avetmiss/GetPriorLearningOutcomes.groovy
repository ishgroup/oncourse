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

package ish.oncourse.server.api.v1.function.avetmiss

import ish.oncourse.server.cayenne.Outcome
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

import java.time.LocalDate

/**
 * The code here is very wrong and should be removed.
 */
@Deprecated
class GetPriorLearningOutcomes {

    private ObjectContext context
    private LocalDate from
    private LocalDate to
    private boolean exportVet
    private boolean exportNonVet

    GetPriorLearningOutcomes(ObjectContext context, LocalDate from, LocalDate to, boolean exportVet, boolean exportNonVet) {
        this.context = context
        this.from = from
        this.to = to
        this.exportVet = exportVet
        this.exportNonVet = exportNonVet
    }

    List<Outcome> getOutcomes() {
        if (exportVet || exportNonVet) {
            ObjectSelect<Outcome> select = (ObjectSelect.query(Outcome.class)
                    .where(Outcome.PRIOR_LEARNING.isNotNull()) & Outcome.START_DATE.lte(to)) & Outcome.END_DATE.gte(from)

            if (!(exportNonVet && exportVet)) {
                select & (exportVet ? Outcome.MODULE.isNotNull() : Outcome.MODULE.isNull())
            }

            select.select(context)
        } else {
            []
        }
    }
}
