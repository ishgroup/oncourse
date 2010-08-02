package ish.oncourse.services.textile;


public class TextileUtil {

	public static final String IMAGE_NAME_REGEXP = "\\{image:name=(\"|&#8220;)(\\w+)(\"|&#8220;)}";
	public static final String IMAGE_ID_REGEXP = "\\{image:id=(\"|&#8220;)(\\d+)(\"|&#8220;)}";
	public static final String QUOT_REGEXP = "\"|&#8220;";

	/**
	 * @param tag
	 * @return
	 */
	public static String getValueInFirstQuots(String tag) {
		return tag.split(QUOT_REGEXP)[1];
	}

}
