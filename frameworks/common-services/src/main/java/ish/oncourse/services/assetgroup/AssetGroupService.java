package ish.oncourse.services.assetgroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.services.cache.CacheGroup;
import ish.oncourse.services.cache.CachedObjectProvider;
import ish.oncourse.services.cache.ICacheService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.resource.IResourceService;

public class AssetGroupService implements IAssetGroupService {

	private final Pattern groupLinePattern;
	private final Pattern assetLinePattern;

	@Inject
	private IResourceService resourceService;

	@Inject
	private IWebSiteService siteService;

	@Inject
	private ICacheService cacheService;

	
	public AssetGroupService() {
		groupLinePattern = Pattern.compile("^([\\w]+):");
		assetLinePattern = Pattern.compile("(\\[([\\w]+)\\])?(.*)$");
	}

	/**
	 * Non-public constructor used by JUnit tests.
	 */
	AssetGroupService(IResourceService resourceService) {
		this();
		this.resourceService = resourceService;
	}

	public Collection<String> getAssetGroupUrls(final AssetType type,
			String group) {

		String key = createKeyForAssetTypeCollection(type);

		// cache parsed config files
		AssetGroupCollection collection = cacheService.get(key,
				new CachedObjectProvider<AssetGroupCollection>() {
					public AssetGroupCollection create() {
						return createAssetGroupCollection(type);
					}
				}, CacheGroup.ASSET_GROUP_COLLECTION);

		return collection.getAssetUrls(group);
	}

	AssetGroupCollection createAssetGroupCollection(AssetType type) {
		URL url = resourceService.getConfigResource(type.name() + ".conf")
				.getPrivateUrl();

		AssetGroupCollection collection = new AssetGroupCollection();

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(url
					.openStream()));
			try {

				String line;
				String group = null;
				while ((line = in.readLine()) != null) {

					// strip comments
					line = line.replaceFirst("//.+$", "").replaceFirst("#.+$",
							"").trim();

					if (line.length() == 0) {
						continue;
					}

					Matcher groupLineMatcher = groupLinePattern.matcher(line);

					// parse group line
					if (groupLineMatcher.find()) {
						group = groupLineMatcher.group(1);

						// TODO: parse "minify" option...

					}
					// parse asset item line
					else if (group != null) {
						Matcher assetLineMatcher = assetLinePattern
								.matcher(line);

						if (assetLineMatcher.find()) {

							String framework = assetLineMatcher.group(2);
							String path = assetLineMatcher.group(3);

							String assetURL = resourceService.getWebResource(
									framework, path).getPublicUrl();
							collection.addUrl(group, assetURL);
						}
					}
				}
			} finally {
				in.close();
			}
		} catch (IOException e) {
			throw new RuntimeException("Error reading config file", e);
		}

		return collection;
	}

	private String createKeyForAssetTypeCollection(AssetType type) {
		return AssetGroupService.class.getName() + ":"
				+ siteService.getResourceFolderName() + "@"
				+ type.name();
	}

}
