package ish.oncourse.utils;

import ish.math.Money;
import ish.oncourse.cayenne.ContactInterface;
import ish.oncourse.cayenne.InvoiceInterface;
import ish.oncourse.cayenne.PaymentInterface;
import ish.oncourse.model.Contact;

import java.util.Date;
import java.util.List;

public class ContactDelegator implements ContactInterface {

    private ContactDelegator() {
    }

    private Contact contact;

    @Override
    public String getEmail() {
        return contact.getEmailAddress();
    }

    @Override
    public String getFirstName() {
        return contact.getGivenName();
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName(boolean b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLastName() {
        return contact.getFamilyName();
    }

    @Override
    public String getMobilePhone() {
        return contact.getMobilePhoneNumber();
    }

    @Override
    public String getPostcode() {
        return contact.getPostcode();
    }

    @Override
    public String getStreet() {
        return contact.getStreet();
    }

    @Override
    public String getSuburb() {
        throw new UnsupportedOperationException();
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
    public Date getDateOfBirth() {
        return contact.getDateOfBirth();
    }

    @Override
    public Boolean getIsCompany() {
        return contact.getIsCompany();
    }

    @Override
    public String getState() {
        return contact.getState();
    }

    @Override
    public String getHomePhone() {
        return contact.getHomePhoneNumber();
    }

    @Override
    public String getFax() {
        return contact.getFaxNumber();
    }

    @Override
    public List<? extends InvoiceInterface> getOwingInvoices() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<? extends InvoiceInterface> getInvoices() {
        throw new UnsupportedOperationException();
    }

    public static ContactDelegator valueOf(Contact contact) {
        ContactDelegator result = new ContactDelegator();
        result.contact = contact;
        return result;
    }
}
