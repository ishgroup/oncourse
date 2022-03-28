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

package ish.oncourse.server.api.v1.service.impl

import javax.inject.Inject
import ish.oncourse.server.api.service.PriorLearningApiService
import ish.oncourse.server.api.v1.model.PriorLearningDTO
import ish.oncourse.server.api.v1.service.PriorLearningApi

class PriorLearningApiImpl implements PriorLearningApi {

    @Inject
    private PriorLearningApiService service

    @Override
    void create(PriorLearningDTO priorLearning) {
        service.create(priorLearning)
    }

    @Override
    PriorLearningDTO get(Long id) {
        service.get(id)
    }

    @Override
    void update(Long id, PriorLearningDTO priorLearning) {
        service.update(id, priorLearning)
    }

    @Override
    void remove(Long id) {
        service.remove(id)
    }
}
