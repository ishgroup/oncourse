package ish.oncourse.enrol.services.concessions;

import ish.oncourse.model.College;
import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.List;

import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

public class ConcessionsService implements IConcessionsService {

	private static final Logger LOGGER = Logger
			.getLogger(ConcessionsService.class);
	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICayenneService cayenneService;

	public List<ConcessionType> getActiveConcessionTypes() {
		College currentCollege = webSiteService.getCurrentCollege();
		SelectQuery query = new SelectQuery(ConcessionType.class,
				ExpressionFactory
						.matchExp(ConcessionType.COLLEGE_PROPERTY,
								currentCollege)
						.andExp(ExpressionFactory.matchExp(
								ConcessionType.IS_ENABLED_PROPERTY, true))
						.andExp(ExpressionFactory.matchExp(
								ConcessionType.IS_CONCESSION_PROPERTY, true)));
		return cayenneService.sharedContext().performQuery(query);
	}

}
