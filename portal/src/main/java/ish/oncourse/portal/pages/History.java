package ish.oncourse.portal.pages;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Invoice;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.util.FormatUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

public class History {

    @Inject
    private IAuthenticationService authenticationService;

    @Property
    private Contact contact;

    @Property
    private Invoice invoice;


    @Property
    private List<Invoice> invoices;

    @SetupRender
    void setupRender(){


    contact = authenticationService.getUser();
    invoices = contact.getInvoices();
    }



    public String getDate(Invoice invoice)
    {
        return String.format("%s ",
                FormatUtils.getDateFormat("EEEE h:mma").format(invoice.getInvoiceDate()));
    }
}
