package ish.oncourse.portal.components.history;

import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.portal.services.IPortalService;
import org.apache.cayenne.BaseDataObject;
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
	private List<BaseDataObject> items;

	@Property
	private BaseDataObject item;

	@Inject
	private Messages messages;
	
	private Money balance = Money.ZERO;

	@SetupRender
	void setupRender() {

		contact = portalService.getContact();

		items = new ArrayList<>();
		items.addAll(contact.getInvoices());
		items.addAll(portalService.getPaymentIns());
		items.addAll(portalService.getPaymentOuts());

		Ordering.orderList(items, Collections.singletonList(new Ordering(PaymentIn.CREATED_PROPERTY, SortOrder.ASCENDING)));
	}

	public boolean isInvoice() {
		return item instanceof Invoice;
	}

	public boolean isPaymentIn() {
		return item instanceof PaymentIn;
	}

	public boolean isPaymentOut() {
		return item instanceof PaymentOut;
	}

	public Invoice getInvoice() {
		return (Invoice) item;
	}

	public PaymentIn getPaymentIn() {
		return (PaymentIn) item;
	}

	public PaymentOut getPaymentOut() {
		return (PaymentOut) item;
	}
	
	public Money getBalance() {
		if (isInvoice()) {
			balance = balance.add(getInvoice().getTotalGst());
		} else if (isPaymentIn()) {
			balance = balance.subtract(getPaymentIn().getAmount());
		} else if (isPaymentOut()) {
			balance = balance.add(getPaymentOut().getTotalAmount());
		}
		return balance;
	}
}
