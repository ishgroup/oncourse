package ish.oncourse.services.alias;

import ish.oncourse.model.WebUrlAlias;

import java.util.List;

public interface IWebUrlAliasService {
	WebUrlAlias getAliasByPath(String path);
    WebUrlAlias findById(Long willowId);

    List<WebUrlAlias> getRedirects();
}
