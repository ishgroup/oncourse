package ish.oncourse.portal.services.discussion;

import ish.oncourse.model.Contact;
import ish.oncourse.model.DiscussionCommentContact;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;

public class DiscussionServiceImpl implements IDiscussionService {

	@Inject
	private ICayenneService cayenneService;
	
	@Override
	public Integer getNumberOfNewMessages(Contact c) {
		Expression expr = ExpressionFactory.matchExp(DiscussionCommentContact.CONTACT_PROPERTY, c);
		expr = expr.andExp(ExpressionFactory.matchExp(DiscussionCommentContact.IS_NEW_PROPERTY, true));
		SelectQuery q = new SelectQuery(DiscussionCommentContact.class, expr);
		q.setFetchingDataRows(true);
		return cayenneService.sharedContext().performQuery(q).size();
	}
}
