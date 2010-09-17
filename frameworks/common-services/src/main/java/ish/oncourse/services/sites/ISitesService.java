package ish.oncourse.services.sites;

import ish.oncourse.model.Site;

public interface ISitesService {
	Site getSite(String searchProperty, Object value);
}
