package ish.oncourse.server.scripting.converter

import org.apache.commons.lang3.StringUtils
import org.commonmark.Extension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension
import org.commonmark.node.Node
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

class ConvertCoreMarkdown extends CoreConverter {

    private static final String HEADER_PREFIX = "header-"

    private ConvertCoreMarkdown(){}

    static ConvertCoreMarkdown valueOf(String content) {
        ConvertCoreMarkdown coreMarkdown = new ConvertCoreMarkdown()
        coreMarkdown.content = content
        return coreMarkdown
    }

    @Override
    String convert() {
        content = MarkdownTableAnalyzer.valueOf(content).getContentWithFixedSeparators()
        List<Extension> extensions = Arrays.asList(HeadingAnchorExtension.builder().idPrefix(HEADER_PREFIX).build(),
                TablesExtension.create())
        Parser parser = Parser.builder().extensions(extensions).build()
        Node document = parser.parse(content) as Node
        HtmlRenderer renderer = HtmlRenderer.builder().extensions(extensions).build()
        clearGenerated(renderer.render(document as Node) as String)
    }

    String clearGenerated(String result) {
        if (result.endsWith(StringUtils.LF)) {
            result = result.substring(0, result.lastIndexOf(StringUtils.LF))
        }
        if (result.startsWith("<p>") && result.endsWith("</p>")) {
            String cutted = result.substring(3)
            if (!cutted.contains("<p>") || cutted.contains("<p>") && cutted.indexOf("</p>") > cutted.indexOf("<p>")) {
                result = cutted.substring(0, cutted.lastIndexOf("</p>"));
            }
        }
        return result.replaceAll("(&amp;nbsp;)", " ").replaceAll("(&quot;)", "\"")
    }
}
