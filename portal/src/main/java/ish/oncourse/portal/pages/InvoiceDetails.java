package ish.oncourse.portal.pages;

import ish.math.Money;
import ish.oncourse.model.*;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.CayenneDataObject;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * User: artem
 * Date: 11/1/13
 * Time: 10:40 AM
 */
public class InvoiceDetails {

    @InjectPage
    private PageNotFound pageNotFound;

    @Property
    private Invoice invoice;

    @Inject
    private ICayenneService cayenneService;

    @Inject
    private IAuthenticationService authenticationService;

    @Property
    @Inject
    private PreferenceController controller;

    @Property
    private List<InvoiceLine> invoiceLines;

    @Property
    private InvoiceLine invoiceLine;

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
            this.invoice = Cayenne.objectForPK(cayenneService.newNonReplicatingContext(), Invoice.class, idLong);
            return null;
        } else {
            return pageNotFound;
        }

    }

    @SetupRender
    void setupRender(){


        invoiceLines=invoice.getInvoiceLines();


    }


     public String getPhoneNumber(){

         Contact contact = invoice.getContact();

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


    public String getDate()

    {

       return String.format("%s ", dateFormat.format(invoice.getCreated()));

    }





    public Money getTotalAmount(){

            Money summ = Money.ZERO;

            for (InvoiceLine invoiceLine : invoice.getInvoiceLines()) {
                summ = summ.add(invoiceLine.getPriceTotalExTax());
            }
            return summ;
    }

       public Money getPaidAmount(){

           Money summ = Money.ZERO;
           summ = getTotalAmount().subtract(invoice.getAmountOwing());
           return summ;

       }
}