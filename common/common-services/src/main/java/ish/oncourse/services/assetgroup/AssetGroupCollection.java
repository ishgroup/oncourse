package ish.oncourse.services.assetgroup;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class AssetGroupCollection {

	private Map<String, Collection<String>> assetUrlsByGroup;

	public AssetGroupCollection() {
		this.assetUrlsByGroup = new HashMap<>();
	}

	public Collection<String> getAssetUrls(String group) {
		Collection<String> assetUrls = assetUrlsByGroup.get(group);
		return assetUrls != null ? assetUrls : Collections.<String> emptyList();
	}

	public void addUrl(String group, String url) {
		Collection<String> assetUrls = assetUrlsByGroup.get(group);
		if (assetUrls == null) {
			assetUrls = new LinkedHashSet<>();
			assetUrlsByGroup.put(group, assetUrls);
		}

		assetUrls.add(url);
	}
}
