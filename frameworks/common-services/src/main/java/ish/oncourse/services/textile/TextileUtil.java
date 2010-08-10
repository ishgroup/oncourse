package ish.oncourse.services.textile;


public class TextileUtil {

	public static final String IMAGE_NAME_REGEXP = "\\{image name:(\"|&#8220;|&#8221;)((\\w|\\s)+)(\"|&#8220;|&#8221;)}";
	public static final String IMAGE_ID_REGEXP = "\\{image id:(\"|&#8220;|&#8221;)(\\d+)(\"|&#8220;|&#8221;)}";
	public static final String BLOCK_NAME_REGEXP = "\\{block name:(\"|&#8220;|&#8221;)((\\w|\\s)+)(\"|&#8220;|&#8221;)}";
	public static final String BLOCK_TAG_REGEXP = "\\{block tag:(\"|&#8220;|&#8221;)((\\w|\\s)+)(\"|&#8220;|&#8221;)}";
	public static final String QUOT_REGEXP = "\"|&#8220;|&#8221;";

	/**
	 * @param tag
	 * @return
	 */
	public static String getValueInFirstQuots(String tag) {
		return tag.split(QUOT_REGEXP)[1];
	}

}
