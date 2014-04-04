package ish.oncourse.portal.pages;

import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.util.FormatUtils;
import org.apache.cayenne.Cayenne;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.text.DateFormat;
import java.text.Format;
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

    @Inject
    private IPortalService portalService;

    @Property
    private List<InvoiceLine> invoiceLines;

    @Property
    private InvoiceLine invoiceLine;

    Object onActivate(String id) {
        if (id != null && id.length() > 0 && id.matches("\\d+")) {
            long idLong = Long.parseLong(id);
            this.invoice = portalService.getInvoiceBy(idLong);
            return this.invoice == null ? pageNotFound : null;
        } else {
            return pageNotFound;
        }

    }

    @SetupRender
    void setupRender() {
        invoiceLines = invoice.getInvoiceLines();
   }


    public String getPhoneNumber() {

        Contact contact = invoice.getContact();

        if (contact.getMobilePhoneNumber() != null)
            return contact.getMobilePhoneNumber();
        else if (contact.getHomePhoneNumber() != null)
            return contact.getHomePhoneNumber();
        else if (contact.getBusinessPhoneNumber() != null)
            return contact.getBusinessPhoneNumber();
        else if (contact.getFaxNumber() != null)
            return contact.getFaxNumber();
        else
            return null;
    }


    public String getDate()
    {
        return String.format("%s ", new SimpleDateFormat(PortalUtils.DATE_FORMAT_dd_MMMMM_yyyy).format(invoice.getCreated()));
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

}