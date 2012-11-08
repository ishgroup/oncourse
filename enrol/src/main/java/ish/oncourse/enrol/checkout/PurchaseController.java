package ish.oncourse.enrol.checkout;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.math.Money;
import ish.oncourse.enrol.checkout.contact.*;
import ish.oncourse.enrol.checkout.payment.PaymentEditorController;
import ish.oncourse.enrol.checkout.payment.PaymentEditorDelegate;
import ish.oncourse.enrol.services.concessions.IConcessionsService;
import ish.oncourse.enrol.services.invoice.IInvoiceProcessingService;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.model.*;
import ish.oncourse.services.discount.IDiscountService;
import ish.oncourse.services.paymentexpress.IPaymentGatewayServiceBuilder;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.voucher.IVoucherService;
import ish.oncourse.services.voucher.VoucherRedemptionHelper;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.Messages;

import java.util.*;

import static ish.oncourse.enrol.checkout.PurchaseController.Action.*;
import static ish.oncourse.enrol.checkout.PurchaseController.State.*;
import static java.util.Arrays.asList;

/**
 * Controller class for purchase page in enrol.
 *
 * @author dzmitry
 */
public class PurchaseController {
	private static final Logger LOGGER = Logger.getLogger(PurchaseController.class);

	private PurchaseModel model;


	private IInvoiceProcessingService invoiceProcessingService;
	private IDiscountService discountService;
	private IVoucherService voucherService;
	private IConcessionsService concessionsService;
	private IStudentService studentService;
	private PreferenceController preferenceController;
	private ICayenneService cayenneService;
	private IPaymentGatewayServiceBuilder paymentGatewayServiceBuilder;
	private IWebSiteService webSiteService;
	private ITagService tagService;

	private Messages messages;


	private VoucherRedemptionHelper voucherRedemptionHelper = new VoucherRedemptionHelper();


	private State state = State.init;

	private boolean illegalModel = false;

	private boolean illegalState = false;

	private ConcessionEditorController concessionEditorController;
	private AddContactController addContactController;
	private ContactEditorController contactEditorController;
	private PaymentEditorController paymentEditorController;

	private Map<String, String> errors = new HashMap<String, String>();

	/**
	 * @return the current state
	 */
	public synchronized State getState() {
		return state;
	}

	synchronized void setState(State state) {
		this.state = state;
	}

	/**
	 * Check that invoice lines linked with the enrollments or productitems list applied some discounts
	 *
	 * @return true if any discount applied.
	 */
	public boolean isHasDiscount() {
		return !getTotalDiscountAmountIncTax().isZero();
	}

	/**
	 * Calculate total discount amount for all actual enrollments.
	 *
	 * @return total discount amount for all actual enrollments.
	 */
	public Money getTotalDiscountAmountIncTax() {
		Money result = Money.ZERO;
		for (Contact contact : getModel().getContacts()) {
			for (Enrolment enabledEnrolment : getModel().getEnabledEnrolments(contact)) {
				result = result.add(enabledEnrolment.getInvoiceLine().getDiscountTotalIncTax());
			}
			for (ProductItem enabledProductItem : getModel().getEnabledProductItems(contact)) {
				result = result.add(enabledProductItem.getInvoiceLine().getDiscountTotalIncTax());
			}
		}
		return result;
	}

	/**
	 * Calculate total (include GST) invoice amount for all actual enrollments.
	 *
	 * @return total invoice amount for all actual enrollments.
	 */
	public Money getTotalIncGst() {
		return model.updateTotalIncGst();
	}


	public Money getTotalPayment() {
		return new Money(model.getPayment().getAmount());
	}

	public Money getTotalVoucherPayments() {
		//TODO need functionality to recalculate the value
		return Money.ZERO;
	}

	public Money getPreviousOwing() {
		//TODO need functionality to recalculate the value for payer
		return Money.ZERO;
	}

	public Money getMinimumPayableNow() {
		return getTotalPayment();
	}


	/**
	 * Check that any discounts potentially can be applied for actual enrollments.
	 *
	 * @return true if some discounts can be applied for actual enrollments.
	 */
	public boolean hasSuitableClasses(StudentConcession studentConcession) {
		// TODO: port this method to some service(it is a part of DiscountService#isStudentElifible)
		for (CourseClass cc : model.getClasses()) {
			for (DiscountCourseClass dcc : cc.getDiscountCourseClasses()) {
				for (DiscountConcessionType dct : dcc.getDiscount().getDiscountConcessionTypes()) {
					if (studentConcession.getConcessionType().getId().equals(dct.getConcessionType().getId())
							&& (!Boolean.TRUE.equals(studentConcession.getConcessionType().getHasExpiryDate())
							|| (studentConcession.getExpiresOn() != null && studentConcession.getExpiresOn().after(new Date())))) {
						return true;
					}
				}
			}
		}
		return false;
	}


	/**
	 * Single entry point to perform all actions. {@link Action} and {@link ActionParameter} values should be specified.
	 */
	public void performAction(ActionParameter param) {

		illegalState = false;
		illegalModel = false;
		errors.clear();

		if (!state.allowedActions.contains(param.action)) {
			addError(Error.illegalState);
			illegalState = true;
			return;
		}

		APurchaseAction action = param.action.createAction(this, param);
		if (!action.action()) {
			illegalModel = true;
		}
	}


	public PurchaseModel getModel() {
		return model;
	}


	/**
	 * @param fillRequiredProperties if true we show only required properties where value is null
	 */
	void prepareContactEditor(Contact contact, boolean fillRequiredProperties) {
		contactEditorController = new ContactEditorController();
		contactEditorController.setPurchaseController(this);
		contactEditorController.setContact(contact);
		contactEditorController.setObjectContext(contact.getObjectContext());
		if (!contact.getObjectId().isTemporary() && fillRequiredProperties)
			contactEditorController.setFillRequiredProperties(fillRequiredProperties);
	}

	void addContactToModel(Contact contact) {
		contact = getModel().localizeObject(contact);
		model.addContact(contact);
		//add the first contact
		if (getModel().getPayer() == null) {
			ActionChangePayer actionChangePayer = changePayer.createAction(this);
			actionChangePayer.setContact(contact);
			actionChangePayer.action();
		}
		for (CourseClass cc : model.getClasses()) {
			Enrolment enrolment = createEnrolment(cc, contact.getStudent());
			model.addEnrolment(enrolment);

			ActionEnableEnrolment action = enableEnrolment.createAction(this);
			action.setEnrolment(enrolment);
			action.action();
		}

	}


	void recalculateEnrolmentInvoiceLines() {

		for (Contact contact : model.getContacts()) {
			for (Enrolment enrolment : model.getEnabledEnrolments(contact)) {
				InvoiceLine oldInvoiceLine = enrolment.getInvoiceLine();
				enrolment.setInvoiceLine(null);
				model.getObjectContext().deleteObject(oldInvoiceLine);

				InvoiceLine newInvoiceLine = invoiceProcessingService.createInvoiceLineForEnrolment(enrolment, model.getDiscounts());
				newInvoiceLine.setInvoice(model.getInvoice());
				enrolment.setInvoiceLine(newInvoiceLine);
			}
		}
	}


	/**
	 * Creates the new {@link Enrolment} entity for the given courseClass and
	 * Student.
	 */
	private Enrolment createEnrolment(CourseClass courseClass, Student student) {
		Enrolment enrolment = model.getObjectContext().newObject(Enrolment.class);
		enrolment.setStatus(EnrolmentStatus.IN_TRANSACTION);
		enrolment.setSource(PaymentSource.SOURCE_WEB);

		enrolment.setCollege(student.getCollege());
		enrolment.setStudent(student);
		enrolment.setCourseClass(courseClass);
		return enrolment;
	}


	ProductItem createProductItem(Contact contact, Product product) {
		if (product instanceof VoucherProduct) {
			VoucherProduct vp = (VoucherProduct) product;
			Voucher voucher = voucherService.createVoucher(vp, contact, vp.getPriceExTax());
			InvoiceLine il = invoiceProcessingService.createInvoiceLineForVoucher(voucher, contact);

			il.setInvoice(model.getInvoice());
			voucher.setInvoiceLine(il);

			return voucher;
		} else {
			throw new IllegalArgumentException("Unsupported product type.");
		}
	}

	public IInvoiceProcessingService getInvoiceProcessingService() {
		return invoiceProcessingService;
	}

	public void setInvoiceProcessingService(IInvoiceProcessingService invoiceProcessingService) {
		this.invoiceProcessingService = invoiceProcessingService;
	}

	public IDiscountService getDiscountService() {
		return discountService;
	}

	public void setDiscountService(IDiscountService discountService) {
		this.discountService = discountService;
	}

	public IVoucherService getVoucherService() {
		return voucherService;
	}

	public void setVoucherService(IVoucherService voucherService) {
		this.voucherService = voucherService;
	}

	public void setModel(PurchaseModel model) {
		this.model = model;
	}

	public boolean isIllegalModel() {
		return illegalModel;
	}

	public boolean isIllegalState() {
		return illegalState;
	}

	public boolean isEditCheckout() {
		return state == editCheckout;
	}

	public boolean isEditContact() {
		return state == editContact;
	}

	public boolean isEditConcession() {
		return state == State.editConcession;
	}

	public boolean isAddContact() {
		return state == State.addContact;
	}

	public boolean isActiveConcessionTypes() {
		return concessionsService.hasActiveConcessionTypes();
	}

	public boolean isEditPayment() {
		return state == editPayment;
	}

	//return true when the payment process has a result.
	public boolean isFinalized() {
		return state == finalized;
	}

	//return true when the payment process was finished
	public boolean isFinished()
	{
		return state == finalized && paymentEditorController != null &&
				paymentEditorController.getPaymentProcessController().isProcessFinished();
	}


	public ConcessionDelegate getConcessionDelegate() {
		return concessionEditorController;
	}

	public IConcessionsService getConcessionsService() {
		return concessionsService;
	}

	public void setConcessionsService(IConcessionsService concessionsService) {
		this.concessionsService = concessionsService;
	}

	public AddContactDelegate getAddContactDelegate() {
		return addContactController;
	}

	public void setStudentService(IStudentService studentService) {
		this.studentService = studentService;
	}

	public IStudentService getStudentService() {
		return studentService;
	}

	public PreferenceController getPreferenceController() {
		return preferenceController;
	}

	public void setPreferenceController(PreferenceController preferenceController) {
		this.preferenceController = preferenceController;
	}

	public ContactEditorDelegate getContactEditorDelegate() {
		return contactEditorController;
	}

	public Map<String, String> getErrors() {
		return errors;
	}

	public Messages getMessages() {
		return messages;
	}

	public boolean isNeedConcessionReminder() {
		if (getPreferenceController().getFeatureConcessionsInEnrolment()) {
			for (Contact contact : model.getContacts())
				if (contact.getStudent().getStudentConcessions().isEmpty()) {
					List<Enrolment> enrolments = model.getEnabledEnrolments(contact);
					for (Enrolment enrolment : enrolments) {
						if (!enrolment.getCourseClass().getConcessionDiscounts().isEmpty()) {
							return true;
						}
					}
				}
		}
		return false;
	}

	public void setMessages(Messages messages) {
		this.messages = messages;
	}

	public void setCayenneService(ICayenneService cayenneService) {
		this.cayenneService = cayenneService;
	}

	public void setPaymentGatewayServiceBuilder(IPaymentGatewayServiceBuilder paymentGatewayServiceBuilder) {
		this.paymentGatewayServiceBuilder = paymentGatewayServiceBuilder;
	}

	public PaymentEditorDelegate getPaymentEditorDelegate() {
		return paymentEditorController;
	}

	public VoucherRedemptionHelper getVoucherRedemptionHelper() {
		return voucherRedemptionHelper;
	}

	public void setConcessionEditorController(ConcessionEditorController concessionEditorController) {
		this.concessionEditorController = concessionEditorController;
	}

	public void setAddContactController(AddContactController addContactController) {
		this.addContactController = addContactController;
	}

	public ICayenneService getCayenneService() {
		return cayenneService;
	}

	public IPaymentGatewayServiceBuilder getPaymentGatewayServiceBuilder() {
		return paymentGatewayServiceBuilder;
	}

	public void setPaymentEditorController(PaymentEditorController paymentEditorController) {
		this.paymentEditorController = paymentEditorController;
	}

	public IWebSiteService getWebSiteService() {
		return webSiteService;
	}

	public void setWebSiteService(IWebSiteService webSiteService) {
		this.webSiteService = webSiteService;
	}

	public ITagService getTagService() {
		return tagService;
	}

	public void setTagService(ITagService tagService) {
		this.tagService = tagService;
	}

	public void addError(Error error, Object... params) {
		errors.put(error.name(), error.getMessage(messages, params));
	}

	public static enum State {
		init(Action.init, Action.startAddContact),
		editCheckout(changePayer,
				enableEnrolment, enableProductItem,
				disableEnrolment, disableProductItem,
				setVoucherPrice, addDiscount, addVoucher,
				startConcessionEditor, startAddContact,
				owingApply, creditAccess, proceedToPayment),
		editConcession(addConcession, removeConcession, cancelConcessionEditor),
		addContact(Action.addContact, cancelAddContact),
		editContact(Action.addContact, cancelAddContact),
		editPayment(changePayer, showPaymentResult),
		finalized(proceedToPayment);

		private List<Action> allowedActions;


		State(Action... allowedActions) {
			this.allowedActions = Arrays.asList(allowedActions);
		}

		public List<Action> getAllowedActions() {
			return allowedActions;
		}
	}


	/**
	 * Enumeration of all actions controller can perform.
	 *
	 * @author dzmitry
	 */
	public static enum Action {
		init(ActionInit.class),
		changePayer(ActionChangePayer.class, Contact.class),
		setVoucherPrice(ActionSetVoucherPrice.class, Money.class),
		enableEnrolment(ActionEnableEnrolment.class, Enrolment.class),
		disableEnrolment(ActionDisableEnrolment.class, Enrolment.class),
		enableProductItem(ActionEnableProductItem.class, ProductItem.class),
		disableProductItem(ActionDisableProductItem.class, ProductItem.class),
		addContact(ActionAddContact.class, ContactCredentials.class),
		addConcession(ActionAddConcession.class, StudentConcession.class),
		removeConcession(ActionRemoveConcession.class, ConcessionType.class, Contact.class),
		addDiscount(ActionAddDiscount.class, String.class, Discount.class),
		addVoucher(ActionAddVoucher.class, String.class, Voucher.class),
		startConcessionEditor(ActionStartConcessionEditor.class, Contact.class),
		cancelConcessionEditor(ActionCancelConcessionEditor.class, Contact.class),
		startAddContact(ActionStartAddContact.class),
		cancelAddContact(ActionCancelAddContact.class),
		creditAccess(ActionCreditAccess.class, String.class),
		owingApply(ActionOwingApply.class),
		proceedToPayment(ActionProceedToPayment.class),
		showPaymentResult(ActionShowPaymentResult.class);

		private Class<? extends APurchaseAction> actionClass;
		private List<Class<?>> paramTypes;

		private Action(Class<? extends APurchaseAction> actionClass, Class<?>... paramType) {
			this.actionClass = actionClass;
			this.paramTypes = new ArrayList<Class<?>>(asList(paramType));
		}

		public Collection<Class<?>> getActionParamTypes() {
			return Collections.unmodifiableCollection(paramTypes);
		}

		public <A extends APurchaseAction> A createAction(PurchaseController controller) {
			try {
				A action = (A) actionClass.getConstructor().newInstance();
				action.setController(controller);
				return action;
			} catch (Throwable e) {
				throw new IllegalArgumentException(e);
			}
		}

		public <A extends APurchaseAction> A createAction(PurchaseController controller, ActionParameter actionParameter) {
			A action = createAction(controller);
			action.setParameter(actionParameter);
			return action;
		}

	}

	/**
	 * Storage class for {@link PurchaseController}'s actions parameter values.
	 *
	 * @author dzmitry
	 */
	public static class ActionParameter {

		private Action action;

		private Map<String, String> errors;

		private Map<Class<?>, Object> values;

		public ActionParameter(Action action) {
			this.action = action;
			values = new HashMap<Class<?>, Object>();
		}

		public <T> void setValue(T value) {
			for (Class<?> clazz : action.paramTypes) {
				if (clazz.isAssignableFrom(value.getClass())) {
					values.put(value.getClass(), value);
					return;
				}
			}
			values.put(value.getClass(), value);
		}

		public <T> T getValue(Class<T> valueType) {
			for (Class<?> clazz : values.keySet()) {
				if (valueType.isAssignableFrom(clazz)) {
					return (T) values.get(clazz);
				}
			}

			throw new IllegalArgumentException("Requested value type doesn't match existing value.");
		}

		public Map<String, String> getErrors() {
			return errors;
		}

		public void setErrors(Map<String, String> errors) {
			this.errors = errors;
		}
	}

	public static enum Error {
		noSelectedItemForPurchase,
		contactAlreadyAdded,
		voucherNotFound,
		voucherCannotBeUsed,
		discountNotFound,
		creditAccessPasswordIsWrong,
		duplicatedEnrolment,
		noCourseClassPlaces,
		courseClassEnded,
		sessionExpired,
		payerNotDefined,
		illegalState;

		public String getMessage(Messages messages, Object... params) {
			return messages.format(String.format("error-%s", name()), params);
		}

	}


}
