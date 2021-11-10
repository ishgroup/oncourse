/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.oncourse.cayenne;

import ish.math.Money;

import java.util.Date;
import java.util.List;

/**
 * Interface uniting server and client Contact entity
 */
public interface ContactInterface {
	String CALENDAR_URL_KEY = "calendarUrl";
	String EMAIL_KEY = "email";
	String FIRST_NAME_KEY = "firstName";
	String LAST_NAME_KEY = "lastName";
	String MOBILE_PHONE_KEY = "mobilePhone";
	String POSTCODE_KEY = "postcode";
	String STREET_KEY = "street";
	String SUBURB_KEY = "suburb";
	String TOTAL_OWING_KEY = "totalOwing";
	String PAYMENTS = "payments";
	String DISPLAYABLE_PAYMENTS = "displayablePayments";
	String STATE_KEY = "state";
	String PHONE_HOME_KEY = "homePhone";
	String FAX_KEY = "fax";
	String BIRTH_DATE_KEY = "birthDate";
	String CONTACT_TYPE_PROP = "contactType";

	String FULLNAME_FIRSTNAME_LASTNAME_PROP = "contactName";
	String FULLNAME_LASTNAME_FIRSTNAME_PROP = "full_name";

	String getEmail();

	String getFirstName();

	/**
	 * Convenience method, same as getName(false)
	 *
	 * @return contact name in a format it is most commonly used: 'Smith, John'
	 */
	String getName();

	/**
	 * a unified method returning the formated contact name
	 *
	 * @param firstNameFirst - true for "John Smith", false for "Smith, John"
	 * @return formatted name
	 */
	String getName(boolean firstNameFirst);

	String getLastName();

	String getMobilePhone();

	String getPostcode();

	String getStreet();

	String getSuburb();

	Money getTotalOwing();

	List<PaymentInterface> getPayments();

	List<? extends InvoiceInterface> getOwingInvoices();

	List<? extends InvoiceInterface> getInvoices();

	Date getDateOfBirth();

	Boolean getIsCompany();

	String getState();

	String getHomePhone();

	String getFax();
}
