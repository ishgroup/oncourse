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
package ish.oncourse.entity.services;

import ish.messaging.IContact;
import ish.util.RuntimeUtil;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.Period;

/**
 */
public class ContactService {

	private static final String ADDRESS_COMPONENT_SEPARATOR = " ";

	/**
	 * @return the age of the contact based on birthdate. null if birthdate is not set.
	 */
	public Integer getAge(IContact contact) {
		if (contact.getBirthDate() != null) {
			return Period.between(contact.getBirthDate(), LocalDate.now()).getYears();
		} else {
			return null;
		}
	}

	/**
	 * Get contact's phone numbers string.
	 *
	 * @param contact
	 * @return phone numbers
	 */
	public String getPhones(IContact contact) {
		String result = "";
		if (contact.getHomePhone() != null) {
			result = result + "H:" + contact.getHomePhone();
		}
		if (contact.getMobilePhone() != null) {
			result = result + (result.length() > 0 ? ", " : "") + "M:" + contact.getMobilePhone();
		}
		if (contact.getWorkPhone() != null) {
			result = result + (result.length() > 0 ? ", " : "") + "W:" + contact.getWorkPhone();
		}
		return result;
	}

	/**
	 * Provides formatted address (Australian format). should be used every time is required to ensure identical formatting everywhere.
	 *
	 * @return String address
	 */
	public String getAddress(IContact contact) {

		StringBuilder address = new StringBuilder();

		if (StringUtils.trimToNull(contact.getStreet()) != null) {
			address.append(contact.getStreet());
			address.append(RuntimeUtil.LINE_SEPARATOR);
		}

		if (StringUtils.trimToNull(contact.getSuburb()) != null) {
			address.append(contact.getSuburb());
		}
		if (StringUtils.trimToNull(contact.getState()) != null) {
			if (address.length() > 0) {
				address.append(ADDRESS_COMPONENT_SEPARATOR);
			}
			address.append(contact.getState());
		}
		if (StringUtils.trimToNull(contact.getPostcode()) != null) {
			if (address.length() > 0) {
				address.append(ADDRESS_COMPONENT_SEPARATOR);
			}
			address.append(contact.getPostcode().trim());
		}

		return address.toString();
	}

}
