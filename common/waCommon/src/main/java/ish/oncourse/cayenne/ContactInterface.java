/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cayenne;

import ish.math.Money;

import java.util.List;

/**
 * Interface uniting server and client Contact entity
 * 
 * @author ldeck, marcin
 */
public interface ContactInterface {
	public static final String CALENDAR_URL_KEY = "calendarUrl";
	public static final String EMAIL_KEY = "email";
	public static final String FIRST_NAME_KEY = "firstName";
	public static final String LAST_NAME_KEY = "lastName";
	public static final String MOBILE_PHONE_KEY = "mobilePhone";
	public static final String POSTCODE_KEY = "postcode";
	public static final String STREET_KEY = "street";
	public static final String SUBURB_KEY = "suburb";
	public static final String TOTAL_OWING_KEY = "totalOwing";
	public static final String PAYMENTS = "payments";
	public static final String DISPLAYABLE_PAYMENTS = "displayablePayments";

	public static final String FULLNAME_FIRSTNAME_LASTNAME_PROP = "contactName";
	public static final String FULLNAME_LASTNAME_FIRSTNAME_PROP = "full_name";

	public String getCalendarUrl();

	public String getEmail();

	public String getFirstName();

	/**
	 * Convenience method, same as getName(false)
	 * 
	 * @return contact name in a format it is most commonly used: 'Smith, John'
	 */
	public String getName();

	/**
	 * a unified method returning the formated contact name
	 * 
	 * @param firstNameFirst - true for "John Smith", false for "Smith, John"
	 * @return formatted name
	 */
	public String getName(boolean firstNameFirst);

	public String getLastName();

	public String getMobilePhone();

	public String getPostcode();

	public String getStreet();

	public String getSuburb();

	public Money getTotalOwing();

	public List<PaymentInterface> getPayments();

	public List<? extends InvoiceInterface> getOwingInvoices();

	public List<? extends InvoiceInterface> getInvoices();
}
