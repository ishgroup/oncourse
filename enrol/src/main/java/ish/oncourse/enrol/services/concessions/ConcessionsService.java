package ish.oncourse.enrol.services.concessions;

import ish.oncourse.model.College;
import ish.oncourse.model.ConcessionType;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.EJBQLQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

public class ConcessionsService implements IConcessionsService {

	private static final Logger logger = LogManager.getLogger();
	
	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICayenneService cayenneService;

	public boolean hasActiveConcessionTypes() {
		College currentCollege = webSiteService.getCurrentCollege();
		Expression qualifier = ExpressionFactory.matchExp(ConcessionType.COLLEGE_PROPERTY, currentCollege)
				.andExp(ExpressionFactory.matchExp(ConcessionType.IS_ENABLED_PROPERTY, true))
				.andExp(ExpressionFactory.matchExp(ConcessionType.IS_CONCESSION_PROPERTY, true));
		ObjectContext sharedContext = cayenneService.sharedContext();
		return ((Long) sharedContext.performQuery(
				new EJBQLQuery("select count(ct) from ConcessionType ct where " + qualifier.toEJBQL("ct"))).get(0)) > 0;
	}

}
