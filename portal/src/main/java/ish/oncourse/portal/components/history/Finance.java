package ish.oncourse.portal.components.history;

import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.util.FormatUtils;
import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.text.Format;
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
    private IPortalService portalService;

    @Property
    private Contact contact;

    @Property
    private ArrayList<CayenneDataObject> items;

    @Property
    private CayenneDataObject item;

    @SetupRender
    void setupRender() {

        contact = portalService.getContact();

        items = new ArrayList<>();
        items.addAll(contact.getInvoices());
        items.addAll(portalService.getPayments());

        Ordering.orderList(items, Collections.singletonList(new Ordering(PaymentIn.CREATED_PROPERTY, SortOrder.DESCENDING)));

    }


    public String getDate() {

        return String.format("%s ", item instanceof Invoice ?
                        new SimpleDateFormat(PortalUtils.DATE_FORMAT_dd_MMMMM_yyyy).format(((Invoice) item).getCreated()) :
                        new SimpleDateFormat(PortalUtils.DATE_FORMAT_dd_MMMMM_yyyy).format(((PaymentIn) item).getCreated())
        );
    }


    public Money getAmount() {

        return item instanceof Invoice ? ((Invoice) item).getTotalGst() : ((PaymentIn) item).getAmount();
    }

    public Format moneyFormat(Money money)
    {
        return FormatUtils.chooseMoneyFormat(money);
    }


    public boolean isInvoice() {

        return item instanceof Invoice;
    }

    public long getId()
    {
        return item instanceof Invoice ? ((Invoice) item).getId(): ((PaymentIn) item).getId();
    }


    public Object getInvoiceNumber() {

        return ((Invoice)item).getInvoiceNumber() != null ? ((Invoice)item).getInvoiceNumber() : StringUtils.EMPTY;
    }

    public String getPaymentType() {
        return ((PaymentIn)item).getType().getDisplayName();
    }
}
