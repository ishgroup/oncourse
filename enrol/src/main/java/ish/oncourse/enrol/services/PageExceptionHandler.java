package ish.oncourse.enrol.services;

import ish.oncourse.enrol.checkout.PurchaseController;
import org.apache.tapestry5.ioc.util.ExceptionUtils;
import org.apache.tapestry5.services.Request;

import static ish.oncourse.enrol.services.Constants.*;

public class PageExceptionHandler {
	private static final java.lang.String METHOD_POST = "POST";

	//input parameters
	private Request request;
	private PurchaseController purchaseController;
	private Throwable cause;

	//results
	private boolean expired = false;
	private boolean redirect = false;
	private boolean unexpected = false;


	public void handle() {

		/**
		 * purchaseController controller can be null only when session was expired.
		 */
		if (purchaseController == null) {
			expired = true;
		} else if (isPOSTAndWrongState() || isXHRAndWrongState()) {
			purchaseController.addError(PurchaseController.Message.illegalState);
			redirect = true;
		} else //in other cases we have illegal behavior.
		{
			unexpected = true;
		}

	}

	/**
	 * The method returns true when an user uses two tab to process the purchase and one of these tabs shows html form
	 * but PurchaseController already got another state.
	 */
	private boolean isPOSTAndWrongState() {

		NullPointerException exception = ExceptionUtils.findCause(cause, NullPointerException.class);
		return exception != null &&
				request.getMethod().equals(METHOD_POST) &&
				((request.getParameterNames().contains(COMPONENT_submitContact) && !purchaseController.isAddContact() && !purchaseController.isEditContact())
						||
						(request.getParameterNames().contains(COMPONENT_paymentSubmit) && !purchaseController.isEditPayment() && !purchaseController.isEditCorporatePass()));
	}

	/**
	 * The method returns true when an user uses two tabs to process the purchase and one of these tabs the user does XHR request but
	 * PurchaseController already got another state.
	 */
	private boolean isXHRAndWrongState() {
		NullPointerException exception = ExceptionUtils.findCause(cause, NullPointerException.class);
		return exception != null && request.isXHR() && request.getPath().toLowerCase().contains(EVENT_changePayerEvent.toLowerCase());
	}

	public PurchaseController getPurchaseController() {
		return purchaseController;
	}

	public void setPurchaseController(PurchaseController purchaseController) {
		this.purchaseController = purchaseController;
	}

	public boolean isExpired() {
		return expired;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public Request getRequest() {
		return request;
	}

	public void setCause(Throwable cause) {
		this.cause = cause;
	}

	public Throwable getCause() {
		return cause;
	}

	public boolean isRedirect() {
		return redirect;
	}

	public boolean isUnexpected() {
		return unexpected;
	}
}
