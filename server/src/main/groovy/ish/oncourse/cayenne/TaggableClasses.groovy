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
package ish.oncourse.cayenne

import ish.oncourse.API
import org.apache.cayenne.ExtendedEnumeration

@API
enum TaggableClasses implements Serializable, ExtendedEnumeration {
	@API
	COURSE(1),
	@API
	COURSE_CLASS(2),
	@API
	STUDENT(3),
	@API
	TUTOR(4),
	@API
	REPORT(6),
	@API
	ATTACHMENT_INFO(7),
	@API
	CONTACT(8),
	@API
	SITE(9),
	@API
	ROOM(10),
	@API
	DOCUMENT(12),
	@API
	APPLICATION(13),
	@API
	ENROLMENT(14),
	@API
	PAYSLIP(15),
	@API
	WAITING_LIST(16),
	@API
	ASSESSMENT(17),
	@API
	LEAD(18),
	@API
	INVOICE(19),
	@API
	VOUCHER_PRODUCT(20),
	@API
	ARTICLE_PRODUCT(21),
	@API
	MEMBERSHIP_PRODUCT(22),
	@API
	VOUCHER(23),
	@API
	ARTICLE(24),
	@API
	MEMBERSHIP(25),
	@API
	PRODUCT_ITEM(26),
	@API
	FACULTY(27);


	private Integer persistentValue

	TaggableClasses(Integer value) {
		this.persistentValue = value
	}

	Integer getDatabaseValue() {
		return this.persistentValue
	}

}
