package ish.oncourse.portal.components.subscriptions;

import ish.oncourse.model.Contact;
import ish.oncourse.model.WaitingList;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.Cayenne;
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
    private IPortalService portalService;

    @Property
    private WaitingList waitingList;

    @Property
    private List<WaitingList> waitingLists;

    @Inject
    private ICayenneService cayenneService;

    @SetupRender
    void setupRender() {
        ObjectContext objectContext = cayenneService.sharedContext();
        Contact contact = portalService.getContact();
        if (contact != null && contact.getStudent() != null)
        {
            Expression expression = ExpressionFactory.matchExp(WaitingList.STUDENT_PROPERTY, contact.getStudent());
            SelectQuery selectQuery = new SelectQuery(WaitingList.class,expression);
            waitingLists = objectContext.performQuery(selectQuery);
        }
    }

    public void deleteWaitingListBy(int id)
    {
        ObjectContext objectContext = cayenneService.newContext();
        WaitingList waitingList = Cayenne.objectForPK(objectContext, WaitingList.class, id);
        objectContext.deleteObjects(waitingList);
        objectContext.commitChanges();
    }

}
