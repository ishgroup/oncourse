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

import ish.common.types.ProductStatus
import ish.oncourse.API
import ish.oncourse.server.api.dao.PaymentInDao
import ish.util.RuntimeUtil
import org.apache.commons.lang3.StringUtils

import java.time.LocalDate
import java.time.Period

trait ContactTrait {

    abstract List<Membership> getMemberships()
    abstract LocalDate getBirthDate()
    abstract String getHomePhone()
    abstract String getMobilePhone()
    abstract String getWorkPhone()
    abstract String getStreet()
    abstract String getPostcode()
    abstract String getState()
    abstract String getSuburb()
    
    boolean hasMembership(MembershipProduct membership) {
        memberships.any { it.status == ProductStatus.ACTIVE && it.expiryDate > new Date() && it.product.id == membership.id  }

    }

    /**
     * Get the age in whole years
     *
     * @param self
     * @return the contact's age
     */
    @API
    Integer getAge() {
        if (getBirthDate() != null) {
            return Period.between(getBirthDate(), LocalDate.now()).years
        } else {
            return null
        }
    }

    Boolean isHasSavedCC() {
        PaymentInDao.getCreditCardId(this as Contact) != null
    }

    private static final String ADDRESS_COMPONENT_SEPARATOR = " ";

    /**
     * Get contact's phone numbers string.
     *
     * @param contact
     * @return phone numbers
     */
    String getPhones() {
        String result = "";
        if (getHomePhone() != null) {
            result = result + "H:" + getHomePhone()
        }
        if (getMobilePhone() != null) {
            result = result + (result.length() > 0 ? ", " : "") + "M:" + getMobilePhone()
        }
        if (getWorkPhone() != null) {
            result = result + (result.length() > 0 ? ", " : "") + "W:" + getWorkPhone()
        }
        return result;
    }

    /**
     * Provides formatted address (Australian format). should be used every time is required to ensure identical formatting everywhere.
     *
     * @return String address
     */
    @API
    String getAddress() {

        StringBuilder address = new StringBuilder()

        if (StringUtils.trimToNull(getStreet()) != null) {
            address.append(getStreet())
            address.append(RuntimeUtil.LINE_SEPARATOR)
        }

        if (StringUtils.trimToNull(getSuburb()) != null) {
            address.append(getSuburb())
        }
        if (StringUtils.trimToNull(getState()) != null) {
            if (address.length() > 0) {
                address.append(ADDRESS_COMPONENT_SEPARATOR)
            }
            address.append(getState())
        }
        if (StringUtils.trimToNull(getPostcode()) != null) {
            if (address.length() > 0) {
                address.append(ADDRESS_COMPONENT_SEPARATOR)
            }
            address.append(getPostcode().trim())
        }

        return address.toString()
    }
    
    
    static class ContactService {

        static Integer getAge(Contact contact) {
            contact.getAge()
        }
        static String getPhones(Contact contact) {
            contact.getPhones()
        }
        static String getAddress(Contact contact) {
            contact.getAddress()
        }
    }
}
