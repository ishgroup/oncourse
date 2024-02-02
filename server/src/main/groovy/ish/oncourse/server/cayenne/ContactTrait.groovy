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

import com.google.inject.Inject
import ish.common.types.ProductStatus
import ish.oncourse.API
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.dao.PaymentInDao
import ish.oncourse.server.license.LicenseService
import ish.util.RuntimeUtil
import ish.util.UrlUtil
import org.apache.commons.lang3.StringUtils

import java.text.ParseException
import java.time.LocalDate
import java.time.Period

trait ContactTrait {

    private static final int DEFAULT_PORTAL_URL_SIGNATURE_TIMEOUT = 7
    private static final String PORTAL_USI_TARGET = "USI"

    abstract List<Membership> getMemberships()
    abstract LocalDate getBirthDate()
    abstract String getHomePhone()
    abstract String getMobilePhone()
    abstract String getWorkPhone()
    abstract String getStreet()
    abstract String getPostcode()
    abstract String getState()
    abstract String getSuburb()
    abstract String getUniqueCode()

    @Inject
    private LicenseService licenseService

    @Inject
    private PreferenceController preferenceController
    
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


    /**
     *
     * Generates signed URL granting access to specific page in portal.
     * For example following statement
     * restrictedPortalURL(courseClass, '2016-01-11')
     * if executed on 1 Jan 2016 will yield the following URL:
     * https://www.skillsoncourse.com.au/portal/class/1531?valid=20160111&key=k9_S8uk68W5PoCvq5lSUp70sqQY
     *
     *
     * Target - the specific object on the basis of which url to certain portal page will be assembled.
     *
     * Available objects are:
     * ```
     * courseClass - link to class page
     * enrolment - link to class page
     * invoice - link to one invoice details
     * document - link to download file directly
     * "USI" string - link to USI details entering page in portal
     * "someOtherPagePath" string - link to page defined by customer (for example: 'timetable', 'results', 'resources', 'subscriptions' and other
     * ```
     * Timeout - acceptable for different parameter types:
     * ```
     *  java.util.Date - date after which URL will expire and no longer be valid,<
     *  java.lang.String - string representation of date (format is 'yyyy-MM-dd') after which URL will expire and no longer be valid,
     *  java.lang.Integer - number of days after which URL will expire and no longer be valid.
     * ```
     * If timeout == null or not defined then url link will be valid for 7 days.
     *
     * @param target object on the basis of which url to certain portal page will be assembled
     * @param date or time after which the generated URL will expire
     *
     * @return signed portal url to certain page (target)
     */
    @API
    String getPortalLink(def target, def timeout = null) {

        Date expiryDate = parseExpiryDate(timeout)
        String hashSalt = licenseService.getSecurity_key()
        if (PORTAL_USI_TARGET.equals(target)) {
            return UrlUtil.createPortalUsiLink(preferenceController, this.uniqueCode, expiryDate, hashSalt)
        } else {
            String path = parsePortalTarget(target)
            return UrlUtil.createSignedPortalUrl(preferenceController, path, expiryDate, hashSalt)
        }
    }

    /**
     * @see ish.oncourse.server.entity.mixins.ContactMixin#getPortalLoginURL(ish.oncourse.server.cayenne.Contact)
     * May be used in other message templates
     * @return
     */
    String getPortalUrl(){
        return preferenceController.getPortalUrl()
    }

    private Date parseExpiryDate(Object timeout) {

        if (timeout) {
            switch (timeout.class) {
                case String:
                    try {
                        return Date.parse('yyyy-MM-dd', (String)timeout)
                    } catch (ParseException e) {
                        return new Date().plus(DEFAULT_PORTAL_URL_SIGNATURE_TIMEOUT)
                    }
                case Date:
                    return (Date) timeout
                case Integer:
                    return new Date().plus((Integer)timeout)
                default:
                    throw new IllegalArgumentException("Can not interpret 'validUntil' parameter to Date value")
            }

        } else {
            return new Date().plus(DEFAULT_PORTAL_URL_SIGNATURE_TIMEOUT)
        }
    }

    private String parsePortalTarget(Object target) {
        StringBuilder path = new StringBuilder()
        if (target) {
            switch (target.class) {
                case String:
                    path.append(target)
                    break
                case CourseClass:
                    path.append("class/" + ((CourseClass)target).willowId)
                    break
                case Enrolment:
                    path.append("class/" + ((Enrolment)target).courseClass.willowId)
                    break
                case Invoice:
                    path.append("invoicedetails/" + ((Invoice)target).willowId)
                    break
                case Document:
                    path.append("resource/" + ((Document)target).fileUUID)
                    break
                default:
                    throw new IllegalArgumentException("Can not interpret 'target' parameter to url path")
            }
        } else {
            throw new IllegalArgumentException("Path cannot be null.")
        }
        return path.append("?contactId=" + this.uniqueCode).toString()
    }

}
