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
import ish.oncourse.function.GetContactFullName
import ish.oncourse.server.api.dao.PaymentInDao
import ish.util.RuntimeUtil
import org.apache.commons.lang3.StringUtils

import javax.annotation.Nullable
import java.time.LocalDate
import java.time.Period

trait ContactTrait {

    abstract List<Membership> getMemberships()
    abstract LocalDate getBirthDate()

    @Deprecated
    String getFull_name() {
        GetContactFullName.valueOf(this as Contact, true).get()
    }

    String getFullName(Boolean firstNameFirst) {
        GetContactFullName.valueOf(this as Contact, firstNameFirst).get()
    }


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
    public String getPhones(Contact contact) {
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
    @API
    String getAddress(Contact contact) {

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
