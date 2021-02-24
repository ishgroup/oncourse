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

package ish.oncourse.server.integration.myob


import ish.oncourse.API
import ish.oncourse.server.cayenne.AccountTransaction
import ish.oncourse.server.scripting.ScriptClosureTrait
import ish.oncourse.server.scripting.ScriptClosure

@API
@ScriptClosure(key = "myob", integration = MyobIntegration)
class MyobScriptClosure implements ScriptClosureTrait<MyobIntegration> {

    List<AccountTransaction> transactions

    def transactions(List<AccountTransaction> transactions) {
        this.transactions = transactions
    }

    /**
     * Execute the closure with the configuration from the passed integration
     *
     * @param integration
     */
    @Override
    Object execute(MyobIntegration integration) {
        integration.addManualJournalForTransactions(transactions)
        return null
    }
}
