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

package ish.oncourse.server.modules

import com.google.inject.Binder
import com.google.inject.Module
import com.google.inject.Scopes
import ish.oncourse.server.api.service.RefundService
import ish.oncourse.server.duplicate.DuplicateInvoiceService

class CustomServicesModule implements Module {

    @Override
    void configure(Binder binder) {
        binder.bind(DuplicateInvoiceService).in(Scopes.SINGLETON)
        binder.bind(RefundService).in(Scopes.SINGLETON)
    }
}
