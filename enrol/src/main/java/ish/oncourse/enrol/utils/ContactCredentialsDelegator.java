package ish.oncourse.enrol.utils;

import ish.math.Money;
import ish.oncourse.cayenne.ContactInterface;
import ish.oncourse.cayenne.InvoiceInterface;
import ish.oncourse.cayenne.PaymentInterface;
import ish.oncourse.enrol.checkout.contact.ContactCredentials;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.List;

public class ContactCredentialsDelegator implements ContactInterface {

    private ContactCredentialsDelegator() {
    }

    private ContactCredentials contactCredentials;

    @Override
    public String getCalendarUrl() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getEmail() {
        return StringUtils.trimToNull(contactCredentials.getEmail());
    }

    @Override
    public String getFirstName() {
        return StringUtils.trimToNull(contactCredentials.getFirstName());
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName(boolean firstNameFirst) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLastName() {
        return StringUtils.trimToNull(contactCredentials.getLastName());
    }

    @Override
    public String getMobilePhone() {
        return null;
    }

    @Override
    public String getPostcode() {
        return null;
    }

    @Override
    public String getStreet() {
        return null;
    }

    @Override
    public String getSuburb() {
        return null;
    }

    @Override
    public Money getTotalOwing() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PaymentInterface> getPayments() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<? extends InvoiceInterface> getOwingInvoices() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<? extends InvoiceInterface> getInvoices() {
        throw new UnsupportedOperationException();
    }

	@Override
	public Date getDateOfBirth() {
		return null;
	}

    @Override
    public Boolean getIsCompany() {
        return null;
    }

    @Override
    public String getState() {
        return null;
    }

    @Override
    public String getHomePhone() {
        return null;
    }

    @Override
    public String getFax() {
        return null;
    }

    public static ContactCredentialsDelegator valueOf(ContactCredentials contactCredentials) {
        ContactCredentialsDelegator result = new ContactCredentialsDelegator();
        result.contactCredentials = contactCredentials;

        return result;
    }
}
