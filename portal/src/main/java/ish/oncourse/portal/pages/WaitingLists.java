package ish.oncourse.portal.pages;

import ish.oncourse.model.Contact;
import ish.oncourse.model.WaitingList;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

public class WaitingLists {

    @Inject
    private IAuthenticationService authenticationService;

    @Property
    private WaitingList waitingList;

    @Property
    private List<WaitingList> waitingLists;

    @Inject
    private ICayenneService cayenneService;

    @SetupRender
    void setupRender() {
        ObjectContext objectContext = cayenneService.sharedContext();
        Contact contact = authenticationService.getUser();
        if (contact != null && contact.getStudent() != null)
        {
            Expression expression = ExpressionFactory.matchExp(WaitingList.STUDENT_PROPERTY, contact.getStudent());
            SelectQuery selectQuery = new SelectQuery(WaitingList.class,expression);
            waitingLists = objectContext.performQuery(selectQuery);
        }
    }
}
