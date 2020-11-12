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

package ish.oncourse.server.api.v1.function

import ish.oncourse.server.api.v1.model.LanguageDTO
import ish.oncourse.server.cayenne.Language

class LanguageFunctions {

    static LanguageDTO toRestLanguage(Language cayenneObj) {
        cayenneObj ? new LanguageDTO().with { l ->
            l.id = cayenneObj.id
            l.name = cayenneObj.name
            l.isActive = cayenneObj.isActive
            l.absCode = l.absCode
            l
        } : null
    }
}
