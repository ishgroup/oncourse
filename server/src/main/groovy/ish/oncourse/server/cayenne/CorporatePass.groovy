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

package ish.oncourse.server.cayenne


import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._CorporatePass

import javax.annotation.Nonnull
import javax.annotation.Nullable

/**
 * CorporatePass allows corporate users to enrol students or purchase items without paying immediately.
 * Using CorporatePass will create invoice in onCourse which can be paid later.
 */
@API
@QueueableEntity
class CorporatePass extends _CorporatePass implements Queueable {



	/**
	 * @return Returns the email of the invoice recipient
	 */
	@Nullable
	@API
	String getEmail() {
		if (getInvoiceEmail() == null) {
			return getContact().getEmail()
		}
		return getInvoiceEmail()
	}

	/**
	 * @return the date and time this record was created
	 */
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	/**
	 * @return expiry date of this corporate pass
	 */
	@API
	@Override
	Date getExpiryDate() {
		return super.getExpiryDate()
	}


	/**
	 * @return email to which invoice will be sent if this corporate pass is used
	 */
	@API
	@Override
	String getInvoiceEmail() {
		return super.getInvoiceEmail()
	}

	/**
	 * @return the date and time this record was modified
	 */
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * @return password code for this corporate pass
	 */
	@Nonnull
	@API
	@Override
	String getPassword() {
		return super.getPassword()
	}



	/**
	 * @return contact linked to this corporate pass
	 */
	@Nonnull
	@API
	@Override
	Contact getContact() {
		return super.getContact()
	}

	/**
	 * @return list of CorporatePassCourseClass relation records linked to this corporate pass, corporate pass can be used only when enrolling into classes from this list
	 */
	@Nonnull
	@API
	@Override
	List<CorporatePassCourseClass> getCorporatePassCourseClasses() {
		return super.getCorporatePassCourseClasses()
	}

	/**
	 * @return list of products linked to this corporate pass, corporate pass can be used only when purchasing products from this list
	 */
	@Nonnull
	@API
	@Override
	List<CorporatePassProduct> getCorporatePassProduct() {
		return super.getCorporatePassProduct()
	}

	/**
	 * @return list of invoices for purchases made using this corporate pass
	 */
	@Nonnull
	@API
	@Override
	List<Invoice> getInvoices() {
		return super.getInvoices()
	}

	/**
	 * @return list of classes linked to this corporate pass, corporate pass can be used only when enrolling into classes from this list
	 */
	@Nonnull
	@API
	@Override
	List<CourseClass> getValidClasses() {
		return super.getValidClasses()
	}

	@Override
	String getSummaryDescription() {
		Contact contact = getContact()
		return contact == null ? super.getSummaryDescription() : contact.getFullName()
	}
}
