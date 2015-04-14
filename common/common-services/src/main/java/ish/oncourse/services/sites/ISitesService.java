package ish.oncourse.services.sites;

import ish.oncourse.model.Site;

import java.util.Date;
import java.util.List;

public interface ISitesService {
	Site getSite(String searchProperty, Object value);

	Date getLatestModifiedDate();

	List<Site> loadByIds(List<Long> ids);
}
