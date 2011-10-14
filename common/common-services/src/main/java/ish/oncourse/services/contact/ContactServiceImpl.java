package ish.oncourse.services.contact;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.model.Contact;
import ish.oncourse.services.persistence.ICayenneService;

public class ContactServiceImpl implements IContactService {

	@Inject
	private ICayenneService cayenneService;

	@Override
	public Contact findByUniqueCode(String uniqueCode) {

		SelectQuery q = new SelectQuery(Contact.class);
		q.andQualifier(ExpressionFactory.matchExp(Contact.UNIQUE_CODE_PROPERTY, uniqueCode));

		return (Contact) Cayenne.objectForQuery(cayenneService.sharedContext(), q);
	}
}
