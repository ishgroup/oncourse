package ish.oncourse.enrol.services;

import ish.oncourse.enrol.checkout.PurchaseController;
import org.apache.tapestry5.ioc.util.ExceptionUtils;
import org.apache.tapestry5.services.Request;

import static ish.oncourse.enrol.services.Constants.*;

public class PageExceptionHandler {
	private static final java.lang.String METHOD_POST = "POST";
	
	private static final String ADD_CONTACT_PATH = "/checkout.addcontact.addcontactform";
	private static final String EDIT_CONTACT_PATH = "/checkout.contacteditor.contacteditorform";

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
				(isWrongForSubmitContact()
						||
						(request.getParameterNames().contains(COMPONENT_paymentSubmit) && !purchaseController.isEditPayment() && !purchaseController.isEditCorporatePass()));
	}

	/**
	 * Check that state matches correct edit form, because 
	 * two states available for submitContact action: AddContact and EditContact,
	 * we can delineate it using request pass, see use case
	 * 
	 * user have two or more browser tabs on different steps: AddContact and EditContact,
	 * purchaseController state is EditContact
	 * 
	 * - do submitContact from AddContactForm (path is '/checkout.addcontact.addcontactform') - wrong, need redirection
	 * - do submitContact from EditContactForm (path is '/checkout.contacteditor.contacteditorform') - correct
	 *
	 * @return true if state is wrong
	 */
	private boolean isWrongForSubmitContact() {
		boolean result = request.getParameterNames().contains(COMPONENT_submitContact);
		if (result) {
			result = (!purchaseController.isAddContact() && !purchaseController.isEditContact()) 
							|| (purchaseController.isAddContact() && !request.getPath().equals(ADD_CONTACT_PATH))
							|| (purchaseController.isEditContact() && !request.getPath().equals(EDIT_CONTACT_PATH));
	}
		return result;
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
