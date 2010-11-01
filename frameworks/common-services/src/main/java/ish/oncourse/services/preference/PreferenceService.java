package ish.oncourse.services.preference;

import java.util.List;

import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.model.College;
import ish.oncourse.model.Preference;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

public class PreferenceService implements IPreferenceService {
	private static final Logger LOGGER = Logger
			.getLogger(PreferenceService.class);
	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICayenneService cayenneService;

	public Preference getPreferenceByKey(String key) {
		College currentCollege = webSiteService.getCurrentCollege();
		SelectQuery query = new SelectQuery(Preference.class, ExpressionFactory
				.matchExp(Preference.COLLEGE_PROPERTY, currentCollege).andExp(
						ExpressionFactory.matchExp(Preference.NAME_PROPERTY,
								key)));
		List<Preference> results = cayenneService.sharedContext().performQuery(
				query);
		return results.isEmpty() ? null : results.get(0);
	}

}
