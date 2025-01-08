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

import io.bootique.di.BQModule
import io.bootique.di.Binder
import ish.oncourse.server.api.service.RefundService
import ish.oncourse.server.duplicate.DuplicateInvoiceService

class CustomServicesModule implements BQModule {

    @Override
    void configure(Binder binder) {
        binder.bind(DuplicateInvoiceService).inSingletonScope()
        binder.bind(RefundService).inSingletonScope()
    }
}
