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
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.voucher.IVoucherService;
import ish.oncourse.services.voucher.VoucherRedemptionHelper;
import ish.oncourse.util.FormatUtils;
import ish.oncourse.util.payment.PaymentProcessController;
import ish.oncourse.util.payment.ProcessPaymentInvokable;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.Invokable;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.services.ParallelExecutor;

import java.text.Format;
import java.util.*;
import java.util.concurrent.Future;

/**
 * Controller class for purchase page in enrol.
 *
 * @author dzmitry
 */
public class PurchaseController {
	private static final Logger LOGGER = Logger.getLogger(PurchaseController.class);

	public static final String KEY_TEMPLATE_ILLEGAL_STATE = "illegal-state-%s";

	private PurchaseModel model;


	private IInvoiceProcessingService invoiceProcessingService;
	private IDiscountService discountService;
	private IVoucherService voucherService;
	private IConcessionsService concessionsService;
	private IStudentService studentService;
	private PreferenceController preferenceController;
	private ICayenneService cayenneService;
	private IPaymentGatewayServiceBuilder paymentGatewayServiceBuilder;

	private Messages messages;

	private VoucherRedemptionHelper voucherRedemptionHelper = new VoucherRedemptionHelper();


	private Format moneyFormat = FormatUtils.chooseMoneyFormat(Money.ZERO);

	private State state = State.INIT;

	private boolean illegalModel = false;

	private boolean illegalState = false;

	private ConcessionEditorController concessionEditorController;
	private AddContactController addContactController;
	private ContactEditorController contactEditorController;
	private PaymentEditorController paymentEditorController;

	private List<String> errors = new ArrayList<String>();

	/**
	 * @return the current state
	 */
	public synchronized State getState() {
		return state;
	}

	public boolean isErrorEmptyState() {
		return State.ERROR_EMPTY_LIST.equals(getState());
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
	 * @return the moneyFormat
	 */
	public Format getMoneyFormat() {
		return moneyFormat;
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
		moneyFormat = FormatUtils.chooseMoneyFormat(result);
		return result;
	}

	/**
	 * Calculate total (include GST) invoice amount for all actual enrollments.
	 *
	 * @return total invoice amount for all actual enrollments.
	 */
	public Money getTotalIncGst() {
		Money result = model.updateTotalIncGst();
		moneyFormat = FormatUtils.chooseMoneyFormat(result);
		return result;
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

	private void init() {

		voucherRedemptionHelper.setInvoice(model.getInvoice());
		performAction(new ActionParameter(Action.START_ADD_CONTACT));
	}

	public boolean validateState(Action action) {
		switch (action) {
			case INIT:
				return state == State.INIT;
			case CHANGE_PAYER:
			case SET_VOUCHER_PRICE:
			case ENABLE_ENROLMENT:
			case DISABLE_ENROLMENT:
			case ENABLE_PRODUCT_ITEM:
			case DISABLE_PRODUCT_ITEM:
			case ADD_DISCOUNT:
			case ADD_VOUCHER:
			case PROCEED_TO_PAYMENT:
			case START_CONCESSION_EDITOR:
				return state == State.EDIT_CHECKOUT;
			case ADD_CONCESSION:
			case REMOVE_CONCESSION:
			case CANCEL_CONCESSION_EDITOR:
				return state == State.EDIT_CONCESSION;
			case START_ADD_CONTACT:
				return state == State.INIT || state == State.EDIT_CHECKOUT;
			case CANCEL_ADD_CONTACT:
			case ADD_CONTACT:
				return state == State.ADD_CONTACT || state == State.EDIT_CONTACT;
			case CREDIT_ACCESS:
				return state == State.EDIT_CHECKOUT;
			case OWING_APPLY:
				return state == State.EDIT_CHECKOUT;
			default:
				throw new IllegalArgumentException();
		}
	}

	private boolean validateENABLE_ENROLMENT(Enrolment enrolment) {
		/**
		 * TODO add this check when we try to enable enrolment
		 * if (!enrolment.isDuplicated() && courseClass.isHasAvailableEnrolmentPlaces() && !courseClass.hasEnded()) {
		 */
		if (model.isEnrolmentEnabled(enrolment))
			return false;
		return true;
	}

	private boolean validateADD_CONTACT(ActionParameter param) {
		if (state == State.ADD_CONTACT) {
			ContactCredentials contactCredentials = param.getValue(ContactCredentials.class);

			//todo contact already exists
			if (getModel().containsContactWith(contactCredentials)) {
				return false;
			}
			ContactCredentialsEncoder contactCredentialsEncoder = new ContactCredentialsEncoder();
			contactCredentialsEncoder.setContactCredentials(contactCredentials);
			contactCredentialsEncoder.setPurchaseController(this);
			contactCredentialsEncoder.encode();
			Contact contact = contactCredentialsEncoder.getContact();
			param.setValue(contact);
			return true;
		} else return state == State.EDIT_CONTACT;
	}

	private boolean validateINIT() {
		if (model.getPayer() != null)
			return false;
		if (model.getContacts().size() > 0)
			return false;
		if (model.getClasses().size() < 1 && model.getProducts().size() < 1)
			return false;
		return true;
	}

	public boolean validate(ActionParameter param) {
		if (param.errors != null && param.getErrors().size() > 0) {
			errors.add(String.format("Invalid param:  State=%s; Action=%s.", state.name(), param.action.name()));
			errors.addAll(param.errors);
			return false;
		}
		switch (param.action) {
			case INIT:
				return validateINIT();
			case CHANGE_PAYER:
				Contact contact = param.getValue(Contact.class);
				return model.getContacts().contains(contact);
			case SET_VOUCHER_PRICE:
				break;
			case ENABLE_ENROLMENT:
				Enrolment enrolment = param.getValue(Enrolment.class);
				return validateENABLE_ENROLMENT(enrolment);
			case DISABLE_ENROLMENT:
				enrolment = param.getValue(Enrolment.class);
				if (!model.isEnrolmentEnabled(enrolment))
					return false;
				break;
			case ENABLE_PRODUCT_ITEM:
				ProductItem productItem = param.getValue(ProductItem.class);
				if (model.isProductItemEnabled(productItem))
					return false;
				break;
			case DISABLE_PRODUCT_ITEM:
				productItem = param.getValue(ProductItem.class);
				if (!model.isProductItemEnabled(productItem))
					return false;
				break;
			case ADD_CONCESSION:
				break;
			case REMOVE_CONCESSION:
				break;
			case ADD_DISCOUNT:
				String discountCode = param.getValue(String.class);
				Discount discount = discountService.getByCode(discountCode);
				if (discount == null)
					return false;
				else {
					param.setValue(discount);
					return true;
				}
			case ADD_VOUCHER:
				String voucherCode = param.getValue(String.class);
				Voucher voucher = voucherService.getVoucherByCode(voucherCode);
				param.setValue(voucher);
				return voucher != null && voucher.canBeUsedBy(model.getPayer());
			case START_CONCESSION_EDITOR:
			case CANCEL_CONCESSION_EDITOR:
			case START_ADD_CONTACT:
			case CANCEL_ADD_CONTACT:
				break;
			case ADD_CONTACT:
				return validateADD_CONTACT(param);
			case CREDIT_ACCESS:
				return validateCREDIT_ACCESS(param);
			case PROCEED_TO_PAYMENT:
				validateProceedToPayment();
				return true;
			default:
				throw new IllegalArgumentException();
		}
		return true;
	}

	private void validateProceedToPayment() {
		//todo
	}

	private void proceedToPayment() {

		model.prepareToMakePayment();
		model.getObjectContext().commitChanges();

		PaymentProcessController paymentProcessController = new PaymentProcessController();
		paymentProcessController.setObjectContext(getModel().getObjectContext());
		paymentProcessController.setPaymentIn(getModel().getPayment());
		paymentProcessController.setCayenneService(cayenneService);
		paymentProcessController.setPaymentGatewayService(paymentGatewayServiceBuilder.buildService());
		paymentProcessController.setParallelExecutor(new ParallelExecutor() {
			@Override
			public <T> Future<T> invoke(Invokable<T> invocable) {
				if (invocable instanceof ProcessPaymentInvokable)
					invocable.invoke();
				return null;
			}

			@Override
			public <T> T invoke(Class<T> proxyType, Invokable<T> invocable) {
				return null;
			}
		});
		paymentProcessController.processAction(PaymentProcessController.PaymentAction.INIT_PAYMENT);
		paymentEditorController = new PaymentEditorController();
		paymentEditorController.setPaymentProcessController(paymentProcessController);
		paymentEditorController.setPurchaseController(this);
		state = State.EDIT_PAYMENT;
	}

	private boolean validateCREDIT_ACCESS(ActionParameter param) {
		errors.add(messages.get(String.format(KEY_TEMPLATE_ILLEGAL_STATE, param.action.name())));
		return false;
	}

	/**
	 * Single entry point to perform all actions. {@link Action} and {@link ActionParameter} values should be specified.
	 *
	 * @param param
	 */
	public void performAction(ActionParameter param) {

		illegalState = false;
		illegalModel = false;
		errors.clear();

		if (!validateState(param.action)) {
			errors.add(String.format("Invalid state:  State=%s; Action=%s.", state.name(), param.action.name()));
			illegalState = true;
			return;
		}

		if (!validate(param)) {
			illegalModel = true;
			return;
		}

		switch (param.action) {
			case INIT:
				init();
				break;
			case CHANGE_PAYER:
				changePayer(getModel().localizeObject(param.getValue(Contact.class)));
				break;
			case ADD_CONTACT:
				addContact(param.getValue(Contact.class));
				break;
			case ENABLE_ENROLMENT:
				enableEnrolment(param.getValue(Enrolment.class));
				break;
			case DISABLE_ENROLMENT:
				disableEnrolment(param.getValue(Enrolment.class));
				break;
			case ENABLE_PRODUCT_ITEM:
				enableProductItem(param.getValue(ProductItem.class));
				break;
			case DISABLE_PRODUCT_ITEM:
				disableProduct(param.getValue(ProductItem.class));
				break;
			case REMOVE_CONCESSION:
				concessionRemoved(param.getValue(Contact.class), param.getValue(ConcessionType.class));
				break;
			case ADD_CONCESSION:
				concessionAdded(getModel().localizeObject(param.getValue(StudentConcession.class)));
				break;
			case ADD_DISCOUNT:
				addDiscount(getModel().localizeObject(param.getValue(Discount.class)));
				break;
			case ADD_VOUCHER:
				addVoucher(getModel().localizeObject(param.getValue(Voucher.class)));
				break;
			case START_CONCESSION_EDITOR:
				startConcessionEditor(param.getValue(Contact.class));
				break;
			case CANCEL_CONCESSION_EDITOR:
				concessionEditorController = null;
				state = State.EDIT_CHECKOUT;
				break;
			case START_ADD_CONTACT:
				startAddContact();
				break;
			case CANCEL_ADD_CONTACT:
				addContactController = null;
				concessionEditorController = null;
				state = State.EDIT_CHECKOUT;
				break;
			case OWING_APPLY:
				state = State.EDIT_CHECKOUT;
				break;
			case CREDIT_ACCESS:
				state = State.EDIT_CHECKOUT;
				break;
			case PROCEED_TO_PAYMENT:
				proceedToPayment();
				break;
			default:
				throw new IllegalArgumentException("Invalid action.");
		}
	}

	private void startAddContact() {

		addContactController = new AddContactController();
		addContactController.setPurchaseController(this);
		state = State.ADD_CONTACT;
	}

	private void startConcessionEditor(Contact value) {
		concessionEditorController = new ConcessionEditorController();
		concessionEditorController.setObjectContext(this.getModel().getObjectContext().createChildContext());
		concessionEditorController.setContact((Contact) concessionEditorController.getObjectContext().localObject(value.getObjectId(), null));
		concessionEditorController.setPurchaseController(this);
		state = State.EDIT_CONCESSION;
	}

	public PurchaseModel getModel() {
		return model;
	}

	private void changePayer(Contact contact) {
		Contact oldPayer = model.getPayer();

		if (oldPayer != null) {
			model.removeAllProductItems(contact);
		}

		model.setPayer(contact);

		for (Product product : model.getProducts()) {
			ProductItem productItem = createProductItem(contact, product);
			model.addProductItem(productItem);
			if (true) //todo validation should be added
				enableProductItem(productItem);
		}

	}

	private void addContact(Contact contact) {
		addContactController = null;
		contactEditorController = null;


		if (state.equals(State.ADD_CONTACT)) {
			boolean isAllRequiredFieldFilled = new ContactFieldHelper(preferenceController).isAllRequiredFieldFilled(contact);
			if (contact.getObjectId().isTemporary() || !isAllRequiredFieldFilled) {
				prepareContactEditor(contact, !isAllRequiredFieldFilled);
				state = State.EDIT_CONTACT;
			} else {
				addContactToModel(contact);
				state = State.EDIT_CHECKOUT;
			}
		} else if (state.equals(State.EDIT_CONTACT)) {
			addContactToModel(contact);
			state = State.EDIT_CHECKOUT;
		} else
			throw new IllegalStateException();
	}

	/**
	 * @param fillRequiredProperties if true we show only required properties where value is null
	 */
	private void prepareContactEditor(Contact contact, boolean fillRequiredProperties) {
		contactEditorController = new ContactEditorController();
		contactEditorController.setPurchaseController(this);
		contactEditorController.setContact(contact);
		contactEditorController.setObjectContext(contact.getObjectContext());
		if (!contact.getObjectId().isTemporary() && fillRequiredProperties)
			contactEditorController.setFillRequiredProperties(fillRequiredProperties);
	}

	private void addContactToModel(Contact contact) {
		contact = getModel().localizeObject(contact);
		model.addContact(contact);
		//add the first contact
		if (getModel().getPayer() == null) {
			changePayer(contact);
		}
		for (CourseClass cc : model.getClasses()) {
			Enrolment enrolment = createEnrolment(cc, contact.getStudent());
			model.addEnrolment(enrolment);
			if (validateENABLE_ENROLMENT(enrolment))
				enableEnrolment(enrolment);
		}

	}

	private void enableEnrolment(Enrolment enrolment) {
		model.enableEnrolment(enrolment);
		InvoiceLine il = invoiceProcessingService.createInvoiceLineForEnrolment(enrolment, model.getDiscounts());
		il.setInvoice(model.getInvoice());
		enrolment.setInvoiceLine(il);
	}

	private void disableEnrolment(Enrolment enrolment) {
		model.disableEnrolment(enrolment);

	}

	private void enableProductItem(ProductItem product) {
		if (product instanceof Voucher) {
			Voucher voucher = (Voucher) product;
			if (voucher.getInvoiceLine() == null) {
				InvoiceLine il = invoiceProcessingService.createInvoiceLineForVoucher(voucher, model.getPayer());
				il.setInvoice(model.getInvoice());
				voucher.setInvoiceLine(il);
			}
		} else {
			throw new IllegalArgumentException("Unsupported product type.");
		}
		model.enableProductItem(product);
	}

	private void disableProduct(ProductItem product) {
		if (product instanceof Voucher) {
			model.disableProductItem(product);
		} else {
			throw new IllegalArgumentException("Unsupported product type.");
		}
	}

	private void concessionAdded(StudentConcession studentConcession) {
		getModel().addConcession(studentConcession);
		recalculateEnrolmentInvoiceLines();
		state = State.EDIT_CHECKOUT;
	}

	private void concessionRemoved(Contact contact, ConcessionType concessionType) {
		for (StudentConcession sc : contact.getStudent().getStudentConcessions()) {
			if (sc.getConcessionType().equals(concessionType)) {
				model.getObjectContext().deleteObject(sc);
				break;
			}
		}
		getModel().removeConcession(contact, concessionType);
		recalculateEnrolmentInvoiceLines();
		state = State.EDIT_CHECKOUT;
	}

	private void addDiscount(Discount discount) {
		model.addDiscount(discount);
		recalculateEnrolmentInvoiceLines();
	}

	private void addVoucher(Voucher voucher) {
		voucherRedemptionHelper.addVoucher(voucher);
		voucherRedemptionHelper.calculateVouchersRedeemPayment();
		model.clearVoucherPayments();
		model.addVoucherPayments(voucherRedemptionHelper.getPayments());
	}

	private void recalculateEnrolmentInvoiceLines() {

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
	 *
	 * @param courseClass
	 * @param student
	 * @return
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


	private ProductItem createProductItem(Contact contact, Product product) {
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
		return state == State.EDIT_CHECKOUT;
	}

	public boolean isEditContact() {
		return state == State.EDIT_CONTACT;
	}

	public boolean isEditConcession() {
		return state == State.EDIT_CONCESSION;
	}

	public boolean isAddContact() {
		return state == State.ADD_CONTACT;
	}

	public boolean isActiveConcessionTypes() {
		return concessionsService.hasActiveConcessionTypes();
	}

	public boolean isEditPayment() {
		return state == State.EDIT_PAYMENT;
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

	public List<String> getErrors() {
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
						return !enrolment.getCourseClass().getConcessionDiscounts().isEmpty();
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


	static enum State {
		INIT, EDIT_CHECKOUT, ERROR_EMPTY_LIST, EDIT_CONCESSION, ADD_CONTACT, EDIT_CONTACT, EDIT_PAYMENT, RESULT_PAYMENT, FINALIZED
	}

	/**
	 * Enumeration of all actions controller can perform.
	 *
	 * @author dzmitry
	 */
	public static enum Action {
		INIT,
		CHANGE_PAYER(Contact.class),
		SET_VOUCHER_PRICE(Money.class),
		ADD_CONTACT(ContactCredentials.class),
		ENABLE_ENROLMENT(Enrolment.class),
		DISABLE_ENROLMENT(Enrolment.class),
		ENABLE_PRODUCT_ITEM(ProductItem.class),
		DISABLE_PRODUCT_ITEM(ProductItem.class),
		ADD_CONCESSION(StudentConcession.class),
		REMOVE_CONCESSION(ConcessionType.class, Contact.class),
		ADD_DISCOUNT(String.class, Discount.class),
		ADD_VOUCHER(String.class, Voucher.class),
		START_CONCESSION_EDITOR(Contact.class),
		CANCEL_CONCESSION_EDITOR(Contact.class),
		START_ADD_CONTACT(),
		CANCEL_ADD_CONTACT(),
		CREDIT_ACCESS(String.class),
		OWING_APPLY,
		PROCEED_TO_PAYMENT;

		private List<Class<?>> paramTypes;

		private Action(Class<?>... paramType) {
			this.paramTypes = new ArrayList<Class<?>>(Arrays.asList(paramType));
		}

		public Collection<Class<?>> getActionParamType() {
			return Collections.unmodifiableCollection(paramTypes);
		}
	}

	/**
	 * Storage class for {@link PurchaseController}'s actions parameter values.
	 *
	 * @author dzmitry
	 */
	public static class ActionParameter {

		private Action action;

		private List<String> errors;

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

		public List<String> getErrors() {
			return errors;
		}

		public void setErrors(List<String> errors) {
			this.errors = errors;
		}
	}


}
