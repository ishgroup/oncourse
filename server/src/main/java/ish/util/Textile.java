/**
 * Textile4J
 * Java implementation of Textism's Textile Humane Web Text Generator
 * Portions  Copyright (c) 2003 Mark Lussier, All Rights Reserved
 *
 * Changes by Ari Maniatis
 *
 * --------------------------------------------------------------------------------
 *
 * Textile is Copyright (c) 2003, Dean Allen, www.textism.com, All rights reserved
 * The  origional Textile can be found at http://www.textism.com/tools/textile
 *
 * _______________
 * TEXTILE LICENSE
 *
 * Redistribution and use in source and binary forms, with or without
 * modifcation, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name Textile nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package ish.util;

import net.java.textilej.parser.Attributes;
import net.java.textilej.parser.MarkupParser;
import net.java.textilej.parser.builder.HtmlDocumentBuilder;
import net.java.textilej.parser.markup.textile.TextileDialect;
import org.apache.commons.lang3.StringUtils;

import java.io.StringWriter;
import java.io.Writer;

public final class Textile {
	private static final String BRACKETS_PATTERN = "(\\{([^}]*)})";

	// « (\u00AB) in UTF-8
	// » (\u00BB) in UTF-8
	// “ (\u201C) in UTF-8
	// ” (\u201D) in UTF-8
	// „ (\u201E) in UTF-8
	// ‟ (\u201F) in UTF-8
	// ‟ (\u0093) in UTF-8
	// ‟ (\u0094) in UTF-8
	public static final String REGEXP_UNICODE_DOUBLE_QUOTES = "[\\u0093\\u0094\\u201F\\u201E\\u201D\\u201C\\u00BB\\u00AB]";

	// ‘ (\u2018) in UTF-8
	// ’ (\u2019) in UTF-8
	// ‚ (\u201A) in UTF-8
	// ‛ (\u201B) in UTF-8
	// ‹ (\u2039) in UTF-8
	// › (\u203A) in UTF-8
	public static final String REGEXP_UNICODE_SINGLE_QUOTES = "[\\u2018\\u2019\\u201A\\u201B\\u2039\\u203A]";

	public static final String QUOT = "\"|&#8220;|&#8221;|\u201C|\u201D";

	private Textile() {}

	/**
	 * Process a textile formatted string
	 * 
	 * @param content Textile formatted content
	 * @return Content converted to HTML
	 */
	public static final String process(String content) {
		if (content == null) {
			return null;
		}
		StringWriter writer = new StringWriter();

		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
		// avoid the <html> and <body> tags
		builder.setEmitAsDocument(false);

		TextileDialect textileDialect = new TextileDialect();
		MarkupParser parser = new MarkupParser(textileDialect);

		parser.setBuilder(builder);

		parser.parse(content, false);
		return writer.toString();
	}

	/**
	 * Process textile converting it to jasper-friendly HTML
	 * 
	 * @param content
	 * @return jasper-friendly HTML
	 */
	public static final String processToJasperHtml(String content) {
		if (content == null) {
			return null;
		}
		StringWriter writer = new StringWriter();

		HtmlDocumentBuilder builder = new BasicHtmlDocumentBuilder(writer);
		// avoid the <html> and <body> tags
		builder.setEmitAsDocument(false);

		TextileDialect textileDialect = new TextileDialect();
		MarkupParser parser = new MarkupParser(textileDialect);

		parser.setBuilder(builder);

		parser.parse(content, false);
		String result = writer.toString();
		//remove textile code after parse
		return result.replaceAll(BRACKETS_PATTERN, StringUtils.EMPTY);
	}

	private static class BasicHtmlDocumentBuilder extends HtmlDocumentBuilder {

		public BasicHtmlDocumentBuilder(Writer out) {
			super(out);
		}

		@Override
		public void beginHeading(int level, Attributes attributes) {
			writer.writeStartElement("br");

			// replacing html <h4> like tags which jasper ignores with <b><font size="+1">text</font></b> combination
			writer.writeStartElement("b");
			writer.writeStartElement("font");
			writer.writeAttribute("size", "+1");
		}

		@Override
		public void endHeading() {
			writer.writeEndElement();
			writer.writeEndElement();
		}
	}

	public static String getValue(String tag, boolean isInQuots) {
		String separator = null;
		if (isInQuots) {
			separator = QUOT;
		} else {
			separator = "[:]|\\s+|[}]";
		}
		String[] splitted = tag.split(separator);
		return splitted.length >= 2 ? splitted[1] : null;
	}

	public static String unicodeQuotesEncoding(String content) {
		return content.replaceAll(REGEXP_UNICODE_DOUBLE_QUOTES, "\"").replaceAll(REGEXP_UNICODE_SINGLE_QUOTES, "\'");
	}
}
