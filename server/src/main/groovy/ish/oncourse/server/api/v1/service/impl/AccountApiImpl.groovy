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
import ish.oncourse.server.api.service.AccountApiService
import ish.oncourse.server.api.v1.model.AccountDTO
import ish.oncourse.server.api.v1.service.AccountApi


class AccountApiImpl implements AccountApi {

    @Inject
    private AccountApiService accountApiService

    @Override
    void create(AccountDTO account) {
        accountApiService.create(account)
    }

    @Override
    AccountDTO get(Long id) {
        accountApiService.get(id)
    }

    @Override
    List<AccountDTO> getDepositAccounts() {
        accountApiService.getDepositAccounts()
    }

    @Override
    void remove(Long id) {
        accountApiService.remove(id)
    }

    @Override
    void update(Long id, AccountDTO account) {
        accountApiService.update(id, account)
    }
}
