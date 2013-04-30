package ish.oncourse.services.assetgroup;

import ish.oncourse.services.cache.CacheGroup;
import ish.oncourse.services.cache.CachedObjectProvider;
import ish.oncourse.services.cache.ICacheService;
import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.services.resource.PrivateResource;
import ish.oncourse.services.site.IWebSiteService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tapestry5.ioc.annotations.Inject;

public class AssetGroupService implements IAssetGroupService {

	private final Pattern groupLinePattern = Pattern.compile("^(.+):(.*)");

	@Inject
	private IResourceService resourceService;

	@Inject
	private IWebSiteService siteService;

	@Inject
	private ICacheService cacheService;
	
	public AssetGroupService() {
		
	}
	
	/**
	 * Non-public constructor used by JUnit tests.
	 */
	AssetGroupService(IResourceService resourceService) {
		this.resourceService = resourceService;
	}
	
	/**
	 * Returns collection of URL for asset type (css or js). 
	 */
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
	
	
	/**
	 * Creates a combined collection of a resource URLS from site and default configs. Used primary for caching purpose.
	 * @param asset type js or css.
	 * @return collection
	 */
	AssetGroupCollection createAssetGroupCollection(AssetType type) {

		List<PrivateResource> configs = resourceService.getConfigResources(type
				.name() + ".conf");

		AssetGroupCollection collection = new AssetGroupCollection();

		for (PrivateResource config : configs) {
			URL url = config.getPrivateUrl();

			try {
				try (BufferedReader in = new BufferedReader(new InputStreamReader(
						url.openStream()))) {

					String line;
					String group = null;
					while ((line = in.readLine()) != null) {

						// strip comments
						line = line.replaceFirst("//.*$", "")
								.replaceFirst("#.*$", "").trim();

						if (line.length() == 0) {
							continue;
						}

						Matcher groupLineMatcher = groupLinePattern
								.matcher(line);

						// parse group line
						if (groupLineMatcher.find()) {
							group = groupLineMatcher.group(1);
							// TODO: parse "minify" option...
						}
						// parse asset item line
						else if (group != null) {
							String assetURL = resourceService.getWebResource(line).getPublicUrl();
							collection.addUrl(group, assetURL);
						}
					}
				}
			} catch (IOException e) {
				throw new RuntimeException("Error reading config file", e);
			}
		}

		return collection;
	}

	private String createKeyForAssetTypeCollection(AssetType type) {
		return AssetGroupService.class.getName() + ":"
				+ siteService.getCurrentWebSite().getResourceFolderName() + "@" + type.name();
	}

}
