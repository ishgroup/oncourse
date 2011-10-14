package ish.oncourse.services.html;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.SourceCompactor;
import net.htmlparser.jericho.TextExtractor;

public class JerichoPlainTextExtractor implements IPlainTextExtractor {

	@Override
	public String extractFromHtml(String html) {
		Source source = new Source(html);
		TextExtractor extractor = new TextExtractor(source);
		extractor.setIncludeAttributes(false);
		extractor.setConvertNonBreakingSpaces(true);
		return extractor.toString();
	}

	/**
	 * Compacts the given html, removing useless whitespaces.
	 */
	public String compact(String html) {

		Source source = new Source(html);

		return new SourceCompactor(source).toString();
	}

	/**
	 * Method which compacts only the html tags, leaving plain text unchanged.
	 * 
	 * @param content
	 * @return
	 */
	public String compactHtmlTags(String content) {
		if (content == null) {
			return "";
		}
		StringBuffer resultBuffer = new StringBuffer();
		int currentIndex = 0;
		while (currentIndex != content.length()) {
			currentIndex = compactHtmlTag(content, resultBuffer, currentIndex);
		}
		return resultBuffer.toString();
	}

	private int compactHtmlTag(String content, StringBuffer resultBuffer, int currentIndex) {
		int startOfTag = content.indexOf("<", currentIndex);
		if (startOfTag != -1) {
			// append the text between the current index and the start of html
			// tag
			resultBuffer.append(content.substring(currentIndex, startOfTag));
			// now current index is a start of the selected tag
			currentIndex = startOfTag;

			// the closing symbol of the tag
			int closing = content.indexOf(">", currentIndex);

			// detect the first part of tag: <tag> or <tag/>
			String tag = content.substring(startOfTag, closing + 1);
			if (!tag.endsWith("/>")) {
				// we should find the whole tag with it content, if it is not
				// closed already
				// detect the tag name, there could be variants: <tag>, or <tag attr="smth">
				String tagName = tag.substring(1).split(" |>")[0];
				Matcher matcher = Pattern.compile("<" + tagName + ">.+</" + tagName + ">", Pattern.DOTALL).matcher(
						content);
				if (matcher.find()) {
					// we found the full html tag
					tag = matcher.group();
				}
			}

			resultBuffer.append(compact(tag));
			currentIndex = currentIndex + tag.length();

		} else {
			resultBuffer.append(content.substring(currentIndex, content.length()));
			currentIndex = content.length();
		}
		return currentIndex;
	}
}
