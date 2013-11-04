package ish.oncourse.portal.pages;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.Cayenne;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * User: artem
 * Date: 11/4/13
 * Time: 9:23 AM
 */
public class PaymentDetails {

    @Property
    private PaymentIn payment;

    @Inject
    private ICayenneService cayenneService;

    @InjectPage
    private PageNotFound pageNotFound;

    private final static String FORMAT="dd MMMMM yyyy";

    private DateFormat dateFormat = new SimpleDateFormat(FORMAT);


    Object onActivate(String id) {
        if (id != null && id.length() > 0 && id.matches("\\d+"))
        {
            long idLong = Long.parseLong(id);
            /**
             * We need to use not shared cayenne context to be sure that we get actual data
             * for all related objects of the course class (sessions, room, sites).
             * It is important when we define timezone for start and end time.
             */
            this.payment = Cayenne.objectForPK(cayenneService.newNonReplicatingContext(), PaymentIn.class, idLong);
            return null;
        } else {
            return pageNotFound;
        }

    }

    public String getDate()

    {

        return String.format("%s ", dateFormat.format(payment.getCreated()));

    }


    public String getPhoneNumber(){

        Contact contact = payment.getContact();

        if(contact.getMobilePhoneNumber()!=null)
            return contact.getMobilePhoneNumber();
        else if(contact.getHomePhoneNumber()!=null)
            return contact.getHomePhoneNumber();
        else if(contact.getBusinessPhoneNumber()!=null)
            return contact.getBusinessPhoneNumber();
        else if(contact.getFaxNumber()!=null)
            return contact.getFaxNumber();
        else
            return null;
    }

}
