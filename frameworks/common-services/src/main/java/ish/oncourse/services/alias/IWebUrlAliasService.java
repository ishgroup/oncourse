package ish.oncourse.services.alias;

import ish.oncourse.model.WebUrlAlias;

public interface IWebUrlAliasService {
	WebUrlAlias getAliasByPath(String path);
}
