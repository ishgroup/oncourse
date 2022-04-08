/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.export

import groovy.transform.CompileStatic
import ish.DatabaseSetup
import ish.TestWithDatabase
import ish.oncourse.server.cayenne.Account
import org.apache.cayenne.query.ObjectSelect
import org.junit.jupiter.api.Test

@CompileStatic
@DatabaseSetup
class MyExportTest extends TestWithDatabase{

    @Test
    void test(){
        def records = ObjectSelect.query(Account).select(cayenneContext)
        def a = 2
    }
}
