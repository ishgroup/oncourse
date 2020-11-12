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

package ish.oncourse.server.imports.avetmiss

import ish.oncourse.server.cayenne.Contact
import org.apache.cayenne.ObjectContext

abstract class AbstractAvetmissParser {
    protected InputLine line
    protected Integer lineNumber
    protected ObjectContext context
    protected List<String> errors = new ArrayList<>()

    AvetmissImportService service

    List<String> getErrors() {
        return errors
    }

    abstract def parse()
    abstract def fill(Contact contact)
}
