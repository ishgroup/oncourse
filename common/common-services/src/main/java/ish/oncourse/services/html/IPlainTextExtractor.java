package ish.oncourse.services.html;

public interface IPlainTextExtractor {
	String extractFromHtml(String html);
	/**
	 * Compacts the given html, removing useless whitespaces.
	 */
	String compact(String html);
	/**
	 * Method which compacts only the html tags, leaving plain text unchanged.
	 * 
	 * @param content
	 * @return
	 */
	String compactHtmlTags(String content);
}
