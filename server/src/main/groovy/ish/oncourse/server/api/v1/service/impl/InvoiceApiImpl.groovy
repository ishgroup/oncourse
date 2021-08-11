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

import com.google.inject.Inject
import ish.oncourse.server.api.service.InvoiceApiService
import ish.oncourse.server.api.v1.model.InvoiceDTO
import ish.oncourse.server.api.v1.service.InvoiceApi

class InvoiceApiImpl implements InvoiceApi {

    @Inject
    private InvoiceApiService service

    @Override
    void contraInvoice(Long id, List<Long> invoicesToPay) {
        service.contraInvoice(id, invoicesToPay)
    }

    @Override
    void create(InvoiceDTO invoice) {
        service.create(invoice)
    }

    @Override
    InvoiceDTO get(Long id) {
        service.get(id)
    }

    @Override
    List<InvoiceDTO> search(String search) {
        service.search(search)
    }

    @Override
    void update(Long id, InvoiceDTO invoice) {
        service.update(id, invoice)
    }

    @Override
    void remove(Long id) {
        service.remove(id)
    }
}
