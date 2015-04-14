package ish.oncourse.services.html;

import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.SourceCompactor;
import net.htmlparser.jericho.TextExtractor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JerichoPlainTextExtractor implements IPlainTextExtractor {
	private final static Logger logger = LogManager.getLogger();

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
		Integer currentIndex = 0;
		while (currentIndex != null && currentIndex != content.length()) {
			currentIndex = compactHtmlTag(content, resultBuffer, currentIndex);
		}
		return resultBuffer.toString();
	}

	/**
	 * Compacts the closest html tag, appending the result to resultBuffer.
	 * 
	 * @param content
	 *            content for processing
	 * @param resultBuffer
	 * @param currentIndex
	 * @return index of symbol which follows the processed tag.
	 */
	protected Integer compactHtmlTag(String content, StringBuffer resultBuffer, int currentIndex) {
		if (content == null || resultBuffer == null) {
			return null;
		}
		if (content.length() < currentIndex) {
			logger.error("Something unexpected during the compacting html from the rich text. Content {} with length {} has current index {}. Result in buffer: {}", content, content.length(), currentIndex, resultBuffer);
			return null;
		}
		int startOfTag = content.indexOf("<", currentIndex);

		if (startOfTag != -1) {
			boolean shouldCompact = true;
			// append the text between the current index and the start of html
			// tag
			resultBuffer.append(content.substring(currentIndex, startOfTag));
			// now current index is a start of the selected tag
			currentIndex = startOfTag;

			String restOfTag = content.substring(currentIndex);
			// the closing symbol of the tag
			int closing = restOfTag.indexOf(">");
			if (closing == -1) {
				shouldCompact = false;
			}
			// detect the first part of tag: <tag> or <tag/>
			String tag = shouldCompact ? restOfTag.substring(0, closing + 1) : restOfTag;

			if (!tag.endsWith("/>") && shouldCompact) {
				// we should find the whole tag with it content, if it is not
				// closed already
				// detect the tag name, there could be variants: <tag>, or <tag
				// attr="smth">
				String[] splitted = tag.substring(1).split(" |\\s|>");
				if (splitted.length != 0) {
					String tagName = splitted[0];

					String closingTag = "</" + tagName + ">";

					int closingTagIndex = restOfTag.indexOf(closingTag);
					if (closingTagIndex != -1) {
						tag = restOfTag.substring(0, closingTagIndex + closingTag.length());
					}
				} else {
					shouldCompact = false;
				}
			}
			currentIndex += tag.length();
			resultBuffer.append(shouldCompact ? compact(tag) : tag);

		} else {
			resultBuffer.append(content.substring(currentIndex, content.length()));
			currentIndex = content.length();
		}
		return currentIndex;
	}
}
