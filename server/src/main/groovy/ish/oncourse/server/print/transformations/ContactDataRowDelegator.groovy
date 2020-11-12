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

package ish.oncourse.server.print.transformations

import groovy.transform.CompileDynamic
import ish.oncourse.cayenne.PersistentObjectI
import ish.oncourse.server.cayenne.Contact
import org.apache.cayenne.DataRow

@CompileDynamic
class ContactDataRowDelegator implements PersistentObjectI {
    @Delegate(interfaces = false)
    DataRow dataRow
    @Delegate(interfaces = false)
    Contact contact

    Object get(String property) {
        def result = dataRow.get(property)
        result = result ? result : dataRow.get(property.toUpperCase())
        return result ? result : contact."${property}"
    }

    Contact getContact() {
        return contact
    }
}
