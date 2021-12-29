/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.extended

import ish.oncourse.server.scripting.converter.ConvertCoreMarkdown
import ish.oncourse.server.scripting.converter.ConvertCoreTextile
import ish.oncourse.server.scripting.converter.RenderType
import org.apache.commons.lang3.StringUtils

import java.util.regex.Matcher
import java.util.regex.Pattern

class StringMethods {

    private final static String RENDER_RGXP = "(\\{render:\"\\w+\"})"

    private final static String TAG_RGXP = '<[^>]*>'

    static String render(final String self, RenderType rednerType) {

        String content
        String type = "textile"
        Pattern pattern = Pattern.compile(RENDER_RGXP)
        Matcher matcher = pattern.matcher(self)
        if (matcher.find()) {
            String renderMarker = matcher.group(matcher.groupCount() - 1)
            pattern = Pattern.compile("(md)|(html)|(textile)")
            matcher = pattern.matcher(renderMarker)
            if (matcher.find())
                type = matcher.group()
            content = self.substring(0, self.indexOf("{render:\"" + type + "\"")).trim()
        }

        if (content == null) {
            return null
        }

        if (rednerType == RenderType.RAW) {
            return content
        }

        String result
        switch (type) {
            case ("html"):
                result = content
                break
            case ("md"):
                result = ConvertCoreMarkdown.valueOf(content).convert()
                break
            default:
                result = ConvertCoreTextile.valueOf(content).convert()
        }

        if (rednerType == RenderType.HTML) {
            return result
        }

        def plainText = result.split(System.lineSeparator()).collect { line ->
            line.replaceAll(TAG_RGXP, StringUtils.SPACE).replaceAll("\\s+",StringUtils.SPACE).trim()
        }.findAll { !it.isEmpty() }

        return String.join(StringUtils.SPACE, plainText)
    }


}