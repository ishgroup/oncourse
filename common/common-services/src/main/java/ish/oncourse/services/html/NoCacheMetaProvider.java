package ish.oncourse.services.html;

/**
 * The class provides "no-cache" content for Cache-control meta
 */
public class NoCacheMetaProvider implements ICacheMetaProvider{

	private String metaContent = "no-cache";

	public String getMetaContent() {
		return metaContent;
	}
}
