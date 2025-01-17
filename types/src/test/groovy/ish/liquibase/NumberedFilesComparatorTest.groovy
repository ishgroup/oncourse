/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.liquibase

import ish.liquibase.NumberedFilesComparator
import org.junit.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class NumberedFilesComparatorTest {
    
    
    @Test
    void test() {
        List<String> sortedList = ['database/quartz.yml','database/105.upgrade.yml','database/replication.yml',
                                   'database/01.initial.schema.yml', 'database/45.upgrade.yml']
        sortedList.sort(new NumberedFilesComparator())
        assertEquals('database/01.initial.schema.yml', sortedList[0])
        assertEquals('database/45.upgrade.yml', sortedList[1])
        assertEquals('database/105.upgrade.yml', sortedList[2])
        assertEquals('database/quartz.yml', sortedList[3])
        assertEquals('database/replication.yml', sortedList[4])
    }
}
