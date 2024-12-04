/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package au.com.ish.docs.utils

import au.com.ish.docs.Configuration
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyClassDoc

class GroovyDocUtils {

    static isVisible = { doc -> doc.annotations().find { ann -> ann.type().fullPathName == Configuration.DSL_ANNONATION }}

    static boolean isCayenneEntity(SimpleGroovyClassDoc doc) {
        return doc.parentClasses.find { it?.fullPathName?.startsWith(Configuration.CAYENNE_ENTITIES_PACKAGE) }
    }
}
