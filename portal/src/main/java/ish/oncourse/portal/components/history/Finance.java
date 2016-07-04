package ish.oncourse.portal.components.history;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.portal.services.IPortalService;
import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: artem
 * Date: 11/1/13
 * Time: 1:49 PM
 */
public class Finance {

	@Inject
	private IPortalService portalService;

	@Property
	private Contact contact;

	@Property
	private List<CayenneDataObject> items;

	@Property
	private CayenneDataObject item;

	@Inject
	private Messages messages;

	@SetupRender
	void setupRender() {

		contact = portalService.getContact();

		items = new ArrayList<>();
		items.addAll(contact.getInvoices());
		items.addAll(portalService.getPayments());

		Ordering.orderList(items, Collections.singletonList(new Ordering(PaymentIn.CREATED_PROPERTY, SortOrder.DESCENDING)));

	}

	public boolean isInvoice() {
		return item instanceof Invoice;
	}

	public Invoice getInvoice() {
		return (Invoice) item;
	}

	public PaymentIn getPaymentIn() {
		return (PaymentIn) item;
	}
}
