package ish.oncourse.services.alias;

import ish.oncourse.model.WebUrlAlias;

public interface IWebUrlAliasReadService {
	WebUrlAlias getAliasByPath(String path);

	WebUrlAlias getAliasById(String id);
}
