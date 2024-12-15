/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package au.com.ish.docs.helpers

import com.github.jknack.handlebars.Options
import org.apache.commons.lang3.StringUtils

class RenderHelper {

    @Helper
    def static getNewLineOuput(Object self, Options options) {
        return StringUtils.LF
    }

    @Helper
    def static getSpace(Object self, Options options) {
        return StringUtils.SPACE
    }
}
