package ish.oncourse.services.sites;

import java.util.Date;
import java.util.List;

import ish.oncourse.model.Site;

public interface ISitesService {
	Site getSite(String searchProperty, Object value);

	Date getLatestModifiedDate();

	List<Site> loadByIds(List<Long> ids);
}
