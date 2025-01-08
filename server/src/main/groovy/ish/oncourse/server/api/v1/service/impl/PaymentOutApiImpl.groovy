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
import ish.oncourse.server.api.service.PaymentOutApiService
import ish.oncourse.server.api.v1.model.PaymentOutDTO
import ish.oncourse.server.api.v1.service.PaymentOutApi

class PaymentOutApiImpl implements PaymentOutApi {
    @Inject
    private PaymentOutApiService service

    @Override
    void create(PaymentOutDTO dto) {
        service.create(dto)
    }

    @Override
    PaymentOutDTO get(Long id) {
        return service.get(id)
    }

    @Override
    void update(PaymentOutDTO payment) {
        service.update(payment.id, payment)
    }
}
