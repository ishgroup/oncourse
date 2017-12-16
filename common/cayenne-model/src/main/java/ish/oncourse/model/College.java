package ish.oncourse.model;

import ish.oncourse.model.auto._College;
import ish.oncourse.utils.QueueableObjectUtils;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class College extends _College {
	private static final long serialVersionUID = 8618498696778850266L;
	public static final String REQUESTING_COLLEGE_ATTRIBUTE = "RequestingCollege";
	public static final String UNDEFINED_ANGEL_VERSION = "UNDEFINED";

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	public Set<String> getCollegeSiteStates() {
		Set<String> states = new HashSet<>();

		for (Site site : getSites()) {
			if (site.getState() != null) {
				states.add(site.getState());
			}
		}

		return states;
	}

	public List<ConcessionType> getActiveConcessionTypes() {
		List<ConcessionType> activeConcessionTypes = new ArrayList<>();
		for (ConcessionType concessionType : getConcessionTypes()) {
			if (Boolean.TRUE.equals(concessionType.getIsConcession()) && Boolean.TRUE.equals(concessionType.getIsEnabled())) {
				activeConcessionTypes.add(concessionType);
			}
		}
		return activeConcessionTypes;
	}

	/**
	 * Returns college sites which marked as web visible.
	 * @return list of sites
	 */
	public List<Site> getWebVisibleSites() {
		return ObjectSelect.query(Site.class).
				cacheStrategy(QueryCacheStrategy.SHARED_CACHE, Site.class.getSimpleName()).
				where(Site.COLLEGE.eq(this)).
				and(Site.IS_WEB_VISIBLE.isTrue()).
				orderBy(Site.NAME.asc()).
				select(getObjectContext());
	}
}
