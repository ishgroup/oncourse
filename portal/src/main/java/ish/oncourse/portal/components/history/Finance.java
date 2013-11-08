package ish.oncourse.portal.components.history;

import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.portal.access.IAuthenticationService;
import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * User: artem
 * Date: 11/1/13
 * Time: 1:49 PM
 */
public class Finance {

    /**
     *CREATED - is common property for all
     * CayenneDataObject (including PaymentIn and Invoice)
     */
    private static final String CREATED = "created";

    @Inject
    private IAuthenticationService authenticationService;

    @Property
    private Contact contact;

    @Property
    private ArrayList<CayenneDataObject> items;

    @Property
    private CayenneDataObject item;




    private final static String FORMAT="dd MMMMM yyyy";

    DateFormat dateFormat = new SimpleDateFormat(FORMAT);

    @SetupRender
    void setupRender(){

        contact = authenticationService.getUser();

        items = new ArrayList<>();
        items.addAll(contact.getInvoices());
        items.addAll(contact.getPaymentsIn());

        Ordering.orderList(items, Collections.singletonList(new Ordering(CREATED, SortOrder.DESCENDING)));

    }



    public String getDate(CayenneDataObject item)

    {
        if(item instanceof Invoice)
            return String.format("%s ", dateFormat.format(((Invoice) item).getCreated()));

        return String.format("%s ", dateFormat.format(((PaymentIn) item).getCreated()));
    }



    public Money getAmount(CayenneDataObject item){

        if(item instanceof Invoice){

            return ((Invoice) item).getTotalGst();
        }

        return ((PaymentIn) item).getAmount();
    }




    public boolean isInvoice(CayenneDataObject item){

        if(item instanceof Invoice)
            return true;

        return  false;
    }


    public Object getInvoiceNumber(CayenneDataObject item){
        if(((Invoice) item).getInvoiceNumber()!=null)
            return ((Invoice) item).getInvoiceNumber();

        return "";
    }

    public long getId(CayenneDataObject item){

        if(item instanceof Invoice)
            return ((Invoice) item).getId();

        return ((PaymentIn) item).getId();
    }

    public String getPaymentType(CayenneDataObject item){


        return   ((PaymentIn) item).getType().getDisplayName();
    }
}
