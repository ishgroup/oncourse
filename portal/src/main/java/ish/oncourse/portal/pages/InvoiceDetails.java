package ish.oncourse.portal.pages;

import ish.math.Money;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceDueDate;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.portal.services.GetContactPhone;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.util.FormatUtils;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.apache.cayenne.query.QueryCacheStrategy.SHARED_CACHE;

/**
 * User: artem
 * Date: 11/1/13
 * Time: 10:40 AM
 */
public class InvoiceDetails {

    @InjectPage
    private Index indexPage;

    @Property
    private Invoice invoice;

    @Inject
    private ICayenneService cayenneService;

    @Property
    @Inject
    private PreferenceController controller;

    @Inject
    private IPortalService portalService;

    @Property
    private List<InvoiceLine> invoiceLines;

    @Property
    private InvoiceLine invoiceLine;

    @Property
    private List<InvoiceDueDate> invoiceDueDates;

    @Property
    private InvoiceDueDate invoiceDueDate;

    @Inject
    private Messages messages;

    Object onActivate(String id) {
        if (id != null && id.length() > 0 && id.matches("\\d+")) {
            long idLong = Long.parseLong(id);
            this.invoice = portalService.getInvoiceBy(idLong);
            if (invoice != null) {
                invoiceDueDates =
                        ObjectSelect.query(InvoiceDueDate.class)
                                .where(InvoiceDueDate.INVOICE.eq(this.invoice))
                                .orderBy(InvoiceDueDate.DUE_DATE.asc())
								.cacheStrategy(SHARED_CACHE, InvoiceDueDate.class.getSimpleName())
								.select(cayenneService.sharedContext());
                return null;
            } else {
                return indexPage;
            }
        } else {
            return indexPage;
        }

    }

    @SetupRender
    void setupRender() {
        invoiceLines = invoice.getInvoiceLines();
   }


    public String getPhoneNumber() {
        return GetContactPhone.valueOf(invoice.getContact()).get();
    }


    public String getDate()
    {

        Date created = invoice.getCreated();
        /**
         * we added created != null condition to exlude NPE when some old payment has null value in create field.
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

    public Money getTotalTax() {
        return invoice.getTotalGst().subtract(invoice.getTotalExGst());
    }

    public Money getPaidAmount() {
        return invoice.getTotalGst().subtract(invoice.getAmountOwing());
    }

    public Format moneyFormat(Money money)
    {
        return FormatUtils.chooseMoneyFormat(money);
    }

    public Format dateFormat() {
        return new SimpleDateFormat(PortalUtils.DATE_FORMAT_dd_MMMMM_yyyy);
    }
}