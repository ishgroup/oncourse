package ish.oncourse.services.binary;

import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.College;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

public class BinaryDataService implements IBinaryDataService {

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;

	public BinaryInfo getBinaryInfo(String searchProperty, Object value) {
		College currentCollege = webSiteService.getCurrentCollege();
		ObjectContext sharedContext = cayenneService.sharedContext();
		Expression qualifier = ExpressionFactory.matchExp(
				BinaryInfo.COLLEGE_PROPERTY, currentCollege);
		if (BinaryInfo.ID_PK_COLUMN.equals(searchProperty)) {
			qualifier = qualifier.andExp(ExpressionFactory.matchExp(
					BinaryInfo.ID_PROPERTY, value));
		} else if (BinaryInfo.NAME_PROPERTY.equals(searchProperty)) {
			qualifier = qualifier.andExp(ExpressionFactory.matchExp(
					BinaryInfo.NAME_PROPERTY, value));
		}
		SelectQuery query = new SelectQuery(BinaryInfo.class, qualifier);
		List<BinaryInfo> listResult = sharedContext.performQuery(query);
		return !listResult.isEmpty() ? listResult.get(0) : null;
	}

}
