package ish.oncourse.services.markdown

import org.apache.commons.lang3.StringUtils
import org.commonmark.node.Node
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

class ConvertCoreMarkdown {

    private String content

    private ConvertCoreMarkdown(){}

    static ConvertCoreMarkdown valueOf(String content) {
        ConvertCoreMarkdown coreMarkdown = new ConvertCoreMarkdown()
        coreMarkdown.content = content
        return coreMarkdown
    }

    String convert() {
        Parser parser = Parser.builder().build()
        Node document = parser.parse(content)
        HtmlRenderer renderer = HtmlRenderer.builder().build()
        clearGenerated(renderer.render(document))
    }

    static String clearGenerated(String result) {
        if (result.endsWith(StringUtils.LF)) {
            result = result.substring(0, result.lastIndexOf(StringUtils.LF))
        }
        if (result.startsWith("<p>") && result.endsWith("</p>")) {
            String cutted = result.substring(3)
            if (!cutted.contains("<p>") || cutted.contains("<p>") && cutted.indexOf("</p>") > cutted.indexOf("<p>")) {
                result = cutted.substring(0, cutted.lastIndexOf("</p>"));
            }
        }
        return result.replaceAll("(&amp;nbsp;)", " ")
    }
}
