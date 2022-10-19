/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.export

import groovy.json.JsonOutput
import ish.oncourse.types.OutputType

class Formatter {
    private static Map<OutputType, Closure<String>> formatters = [(OutputType.JSON) : { String str -> JsonOutput.prettyPrint(str) }]

    static String formatOutput(String output, OutputType outputType){
        formatters.getOrDefault(outputType, Closure.IDENTITY)(output)
    }
}
