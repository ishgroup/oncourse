package ish.oncourse.cms.alias;

import ish.oncourse.model.WebUrlAlias;

public interface IWebAliasWriteService {
	void removeAlias(final WebUrlAlias alias);
	WebUrlAlias create(Long nodeId, String urlPath);
}
