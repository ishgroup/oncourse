package ish.oncourse.services.sites;

import java.util.Date;

import ish.oncourse.model.Site;

public interface ISitesService {
	Site getSite(String searchProperty, Object value);
	Date getLatestModifiedDate();
}
