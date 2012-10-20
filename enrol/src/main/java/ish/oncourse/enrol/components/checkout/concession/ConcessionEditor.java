package ish.oncourse.enrol.components.checkout.concession;

import ish.oncourse.enrol.pages.Checkout;
import ish.oncourse.enrol.utils.ConcessionDelegate;
import ish.oncourse.enrol.utils.PurchaseController;
import ish.oncourse.enrol.utils.PurchaseController.Action;
import ish.oncourse.model.Student;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class ConcessionEditor {
	private static final Logger LOGGER = Logger.getLogger(ConcessionEditor.class);

	@Parameter(required = true)
	private ConcessionDelegate delegate;

	@InjectPage
	private Checkout checkout;

	@Inject
	private Request request;

	public Student getStudent() {
    	return delegate.getStudent();
    }


	public Object onActionFromCancelConcessionLink(Long contactId)
	{
		if (!request.isXHR())
			return null;
		checkout.getPurchaseController().performAction(new PurchaseController.ActionParameter(Action.CANCEL_CONCESSION_EDITOR));
		if (checkout.getCheckoutBlock() != null)
			return checkout.getCheckoutBlock();
		return null;
	}
}
