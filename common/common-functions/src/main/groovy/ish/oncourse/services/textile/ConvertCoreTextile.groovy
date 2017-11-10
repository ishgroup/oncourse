package ish.oncourse.services.textile

import net.java.textilej.parser.MarkupParser
import net.java.textilej.parser.builder.HtmlDocumentBuilder
import net.java.textilej.parser.markup.textile.TextileDialect
import org.apache.commons.lang.StringEscapeUtils

class ConvertCoreTextile {

    private String content
    
    
    private ConvertCoreTextile(){}

    static ConvertCoreTextile valueOf(String content) {
        ConvertCoreTextile coreTextile = new ConvertCoreTextile()
        coreTextile.content = content
        return coreTextile
    }

    String convert() {
        if (content == null) {
            return null
        }

        // commented as seems to be useless(brake the textile enclosed by html
        // tag) - uncomment when this willl be solved and the extra <br> will
        // spoil the life
        // content = extractor.compactHtmlTags(content);

        content = TextileUtil.unicodeQuotesEncoding(content)
        StringWriter writer = new StringWriter()

        HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer)
        // avoid the <html> and <body> tags
        builder.emitAsDocument = false
        TextileDialect textileDialect = new TextileDialect()
        MarkupParser parser = new MarkupParser(textileDialect)
        parser.builder = builder
        parser.parse(content, false)
        String result = writer.toString()
        result = clearGenerated(result)
        return result
    }

    static String clearGenerated(String result) {
        if (result.startsWith("<p>") && result.endsWith("</p>")) {
            String cutted = result.substring(3);
            if (!cutted.contains("<p>") || cutted.contains("<p>") && cutted.indexOf("</p>") > cutted.indexOf("<p>")) {
                result = cutted.substring(0, cutted.lastIndexOf("</p>"));
            }
        }
        return StringEscapeUtils.unescapeHtml(result).replaceAll("(&amp;nbsp;)", " ");
    }
}
