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

package ish.oncourse.server.api.v1.function

import ish.oncourse.server.api.dao.EnrolmentDao
import ish.oncourse.server.api.dao.ModuleDao
import ish.oncourse.server.api.dao.OutcomeDao
import ish.oncourse.server.api.v1.model.OutcomeDTO
import ish.oncourse.server.cayenne.Outcome
import ish.oncourse.server.cayenne.PriorLearning

class PriorLearningFunctions {

    static void updateOutcomes(OutcomeDao outcomeDao, ModuleDao moduleDao, PriorLearning priorLearning, List<OutcomeDTO> outcomeDTOs){
        List<Long> relationsToSave = outcomeDTOs*.id ?: [] as List<Long>
        priorLearning.context.deleteObjects(priorLearning.outcomes.findAll { !relationsToSave.contains(it.id) })

        outcomeDTOs.findAll { priorLearning.outcomes*.id.contains(it.id) }.each { dto ->
            Outcome o = priorLearning.outcomes.find{it.id == dto.id}
            OutcomeFunctions.toCayenneOutcome(moduleDao, null as EnrolmentDao, dto, o)
        }

        outcomeDTOs.findAll { !priorLearning.outcomes*.id.contains(it.id) }.each { it ->
            Outcome newOutcome = outcomeDao.newObject(priorLearning.context)
            newOutcome.priorLearning = priorLearning
            OutcomeFunctions.toCayenneOutcome(moduleDao, null as EnrolmentDao, it, newOutcome)
            newOutcome
        }
    }
}
