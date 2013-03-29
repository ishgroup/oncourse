package ish.oncourse.util;

public class URLUtils {
	
	/**
	 * Determines if given URL is absolute. Assumption is that every absolute url must have 
	 * semicolon and slash in it and no slashes should precede first semicolon occurrence.
	 * 
	 * @param url
	 * @return true if url is absolute
	 */
	public static boolean isAbsolute(String url) {
		
		if (url == null) {
			throw new IllegalArgumentException("URL cannot be null.");
		}
		
		int semicolonPosition = url.indexOf(':');
		int slashPosition = url.indexOf('/');
		
		return semicolonPosition > 0 && slashPosition > 0 && semicolonPosition < slashPosition;
	}

}
