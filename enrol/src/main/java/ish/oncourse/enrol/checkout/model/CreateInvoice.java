package ish.oncourse.enrol.checkout.model;

import ish.common.types.PaymentSource;
import ish.math.Money;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.WebSite;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class CreateInvoice {
	private ObjectContext objectContext;
	private College college;
	private Contact contact;
	private WebSite webSite;

	public Invoice create() {
		Invoice invoice = objectContext.newObject(Invoice.class);
		// fill the invoice with default values
		invoice.setInvoiceDate(DateUtils.setHours(new Date(), 12));
		invoice.setAmountOwing(Money.ZERO);
		invoice.setDateDue(new Date());
		invoice.setSource(PaymentSource.SOURCE_WEB);
		invoice.setCollege(college);
		invoice.setContact(contact);
		invoice.setWebSite(webSite);
		return invoice;
	}

	public static CreateInvoice valueOf(College college, Contact contact, WebSite webSite, ObjectContext objectContext) {
		CreateInvoice result = new CreateInvoice();
		result.college = college;
		result.contact = contact;
		result.webSite = webSite;
		result.objectContext = objectContext;
		return result;
	}

}
