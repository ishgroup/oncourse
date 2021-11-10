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

package ish.oncourse.server.entity.mixins

import groovy.time.TimeCategory
import groovy.transform.CompileDynamic
import ish.math.Money
import ish.oncourse.API
import ish.oncourse.server.cayenne.*
import ish.oncourse.server.license.LicenseService
import ish.oncourse.server.print.proxy.PrintableStatementLine
import ish.util.ContactUtils
import ish.util.UrlUtil
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect

import java.text.ParseException
import java.time.LocalDate

import static ish.oncourse.server.entity.mixins.MixinHelper.getService

@CompileDynamic
class ContactMixin {

	private static final int DEFAULT_PORTAL_URL_SIGNATURE_TIMEOUT = 7
	private static final String PORTAL_USI_TARGET = "USI"


    /**
     * For this contact, find all the statement lines (invoices, credit notes, payments)
     * and then filter them between the date range.
     *
     * @param self
     * @param from the start datetime to find lines
     * @param to the end datetime
     * @return a list of all statement lines, not sorted
     */
	static List<PrintableStatementLine> getStatementLines(Contact self, LocalDate from, LocalDate to) {
		def financialItems = ContactUtils.getFinancialItems(self, PrintableStatementLine)

        //TODO: sort the output by date
		return ExpressionFactory.betweenExp(PrintableStatementLine.DATE, from, to).filterObjects(financialItems)
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
	static getPortalLink(Contact self, def target, def timeout = null) {

		Date expiryDate = parseExpiryDate(timeout)
		String hashSalt = getService(LicenseService).getSecurity_key()
		if (PORTAL_USI_TARGET.equals(target)) {
			return UrlUtil.createPortalUsiLink(self.uniqueCode, expiryDate, hashSalt)
		} else {
			String path = parsePortalTarget(self, target)
			return UrlUtil.createSignedPortalUrl(path, expiryDate, hashSalt)
		}
	}

	private static Date parseExpiryDate(def timeout) {
		if (timeout) {
			switch (timeout.class) {
				case String:
					try {
						return Date.parse('yyyy-MM-dd', timeout)
					} catch (ParseException e) {
						return use(TimeCategory) { new Date() + DEFAULT_PORTAL_URL_SIGNATURE_TIMEOUT.days }
					}
				case Date:
					return timeout
				case Integer:
					return use(TimeCategory) { new Date() + ((Integer)timeout).days }
				default:
					throw new IllegalArgumentException("Can not interpret 'validUntil' parameter to Date value")
			}

		} else {
			return use(TimeCategory) { new Date() + DEFAULT_PORTAL_URL_SIGNATURE_TIMEOUT.days }
		}
	}

	private static String parsePortalTarget(Contact self, def target) {
		StringBuilder path = new StringBuilder()
		if (target) {
			switch (target.class) {
				case String:
					path.append(target)
					break
				case CourseClass:
					path.append("class/$target.willowId")
					break
				case Enrolment:
					path.append("class/$target.courseClass.willowId")
					break
				case Invoice:
					path.append("invoicedetails/$target.willowId")
					break
				case Document:
					path.append("resource/$target.fileUUID")
					break
				default:
					throw new IllegalArgumentException("Can not interpret 'target' parameter to url path")
			}
		} else {
			throw new IllegalArgumentException("Path cannot be null.")
		}
		return path.append("?contactId=$self.uniqueCode").toString()
	}

	/**
	 *
	 * Generates URL to portal login page.
	 *
	 * @return portal login page URL
	 */
	@API
	static String getPortalLoginURL(Contact self) {
		"$UrlUtil.PORTAL_URL/login${self.email ? "?e=$self.email" : ''}"
	}

	/**
	*
	* Get unbalanced invoices
	*
	* @return unbalanced invoices
	*/
	@API
	static List<Invoice> getUnbalancedInvoices(Contact self) {
		return ObjectSelect.query(Invoice)
				.where(Invoice.CONTACT.eq(self))
				.and(Invoice.AMOUNT_OWING.ne(Money.ZERO))
				.orderBy(Invoice.DATE_DUE.asc())
				.select(self.context)
	}


}
