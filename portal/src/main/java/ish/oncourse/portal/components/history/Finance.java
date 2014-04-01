package ish.oncourse.portal.components.history;

import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.portal.access.IAuthenticationService;
import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.lang.StringUtils;
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

    @Inject
    private IAuthenticationService authenticationService;

    @Property
    private Contact contact;

    @Property
    private ArrayList<CayenneDataObject> items;

    @Property
    private CayenneDataObject item;


    private final static String FORMAT = "dd MMMMM yyyy";

    DateFormat dateFormat = new SimpleDateFormat(FORMAT);

    @SetupRender
    void setupRender() {

        contact = authenticationService.getUser();

        items = new ArrayList<>();
        items.addAll(contact.getInvoices());
        items.addAll(contact.getPaymentsIn());

        Ordering.orderList(items, Collections.singletonList(new Ordering(PaymentIn.CREATED_PROPERTY, SortOrder.DESCENDING)));

    }


    public String getDate(CayenneDataObject item) {

        return String.format("%s ", item instanceof Invoice ?
                        new SimpleDateFormat(FORMAT).format(((Invoice) item).getCreated()) :
                        new SimpleDateFormat(FORMAT).format(((PaymentIn) item).getCreated())
        );
    }


    public Money getAmount(CayenneDataObject item) {

        return item instanceof Invoice ? ((Invoice) item).getTotalGst() : ((PaymentIn) item).getAmount();
    }


    public boolean isInvoice(CayenneDataObject item) {

        return item instanceof Invoice;
    }


    public Object getInvoiceNumber(Invoice item) {

        return item.getInvoiceNumber() != null ? item.getInvoiceNumber() : StringUtils.EMPTY;
    }

    public String getPaymentType(PaymentIn item) {
        return item.getType().getDisplayName();
    }
}
