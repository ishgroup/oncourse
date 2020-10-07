package ish.oncourse.services.converter

import ish.oncourse.services.markdown.ConvertCoreMarkdown
import ish.oncourse.services.textile.ConvertCoreTextile
import org.apache.commons.lang.StringEscapeUtils

import java.util.regex.Matcher
import java.util.regex.Pattern

abstract class CoreConverter {

    private final static String RENDER_RGXP = "(\\{render:\\s*'\\w+'})"
    protected String content

    CoreConverter() {
    }

    static String convert(String content) {
        if (content == null) {
            return null
        }

        String type = "textile"
        Pattern pattern = Pattern.compile(RENDER_RGXP)
        Matcher matcher = pattern.matcher(content)
        if (matcher.find()) {
            String renderMarker = matcher.group(matcher.groupCount()-1)
            pattern = Pattern.compile("(md)|(html)|(textile)")
            matcher = pattern.matcher(renderMarker)
            if (matcher.find())
                type = matcher.group()
            content = content.substring(0, content.indexOf("{render: '" + type + "'")).trim()
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
        return result
    }

    abstract String convert()
}
