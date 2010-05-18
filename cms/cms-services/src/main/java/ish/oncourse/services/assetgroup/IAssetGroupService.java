package ish.oncourse.services.assetgroup;

import java.util.Collection;

public interface IAssetGroupService {

	Collection<String> getAssetGroupUrls(AssetType type, String group);
}
