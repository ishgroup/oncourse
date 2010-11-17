package ish.oncourse.services.alias;

import java.util.List;

import ish.oncourse.model.WebUrlAlias;

public interface IWebUrlAliasService {
	WebUrlAlias getAliasByPath(String path);
	List<WebUrlAlias> loadByIds(Object... ids);
}
