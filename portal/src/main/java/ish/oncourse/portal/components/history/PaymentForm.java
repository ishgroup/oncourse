package ish.oncourse.portal.components.history;

import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceDueDate;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Created by akoiro on 20/05/2016.
 */
public class PaymentForm {
	private static final Logger LOGGER = LogManager.getLogger(PaymentForm.class);

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IPortalService portalService;

	@Inject
	private PreferenceController preferenceController;

	@Property
	private boolean isCreditCardPaymentEnabled;

	@Property
	private Long payerId;

	@Property
	private Money balance;
	@Property
	private Money overdue;

	@SetupRender
	public void setupRender() {
		isCreditCardPaymentEnabled = preferenceController.isCreditCardPaymentEnabled();
		Contact payer = portalService.getContact();
		payerId = payer.getId();

		overdue = payer.getInvoices().stream()
				.map(Invoice::getOverdue)
				.reduce(Money.ZERO, Money::add);
		balance = payer.getInvoices().stream()
				.map(Invoice::getAmountOwing)
				.reduce(Money.ZERO, Money::add);

	}


}

