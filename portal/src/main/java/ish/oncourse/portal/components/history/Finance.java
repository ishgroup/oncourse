package ish.oncourse.portal.components.history;

import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.Queueable;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.util.FormatUtils;
import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

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

    @Inject
    private Messages messages;

    @SetupRender
    void setupRender() {

        contact = portalService.getContact();

        items = new ArrayList<>();
        items.addAll(contact.getInvoices());
        items.addAll(portalService.getPayments());

        Ordering.orderList(items, Collections.singletonList(new Ordering(PaymentIn.CREATED_PROPERTY, SortOrder.DESCENDING)));

    }


    public String getDate() {


        Date created =((Queueable) item).getCreated();
        /**
         * we added created == null condition to exlude NPE when some old Queueable has null value in create field.
         * If the field has null value we show "(not set)" string
         * TODO The condition should be deleted after 21309 will be closed
         */
        if (created != null)
        {
            return String.format("%s ", new SimpleDateFormat(PortalUtils.DATE_FORMAT_dd_MMMMM_yyyy).format(created));
        }
        else
        {
            return messages.get(PortalUtils.MESSAGE_KEY_notSet);
        }
    }

    public boolean isNew()
    {
        return portalService.isNew(item);
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
        return ((Queueable) item).getId();
    }


    public Object getInvoiceNumber() {

        return ((Invoice)item).getInvoiceNumber() != null ? ((Invoice)item).getInvoiceNumber() : StringUtils.EMPTY;
    }

    public String getPaymentType() {
        return ((PaymentIn)item).getType().getDisplayName();
    }
}
