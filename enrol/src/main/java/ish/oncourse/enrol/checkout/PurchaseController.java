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
import ish.util.InvoiceUtil;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.services.ParallelExecutor;

import java.util.*;

import static ish.oncourse.enrol.checkout.PurchaseController.Action.*;
import static ish.oncourse.enrol.checkout.PurchaseController.State.*;
import static ish.oncourse.services.preference.PreferenceController.ContactFiledsSet.enrolment;
import static java.util.Arrays.asList;

/**
 * Controller class for purchase page in enrol.
 *
 * @author dzmitry
 */
public class PurchaseController {
	private static final Logger LOGGER = Logger.getLogger(PurchaseController.class);


	public static final List<Action> COMMON_ACTIONS = Collections.unmodifiableList(Arrays.asList(
			enableEnrolment, enableProductItem,
			disableEnrolment, disableProductItem,
			setVoucherPrice, addVoucher,
			startConcessionEditor,Action.addContact));

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
    private Map<String, String> warnings = new HashMap<String, String>();

    private ParallelExecutor parallelExecutor;

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
	public synchronized boolean hasDiscount() {
		return !getTotalDiscountAmountIncTax().isZero();
	}

	/**
	 * Calculate total discount amount for all actual enrollments.
	 *
	 * @return total discount amount for all actual enrollments.
	 */
	public synchronized Money getTotalDiscountAmountIncTax() {
        return getModel().getTotalDiscountAmountIncTax();
	}

	public synchronized Money getTotalPayment() {
		return getModel().getInvoice().getTotalGst();
	}

	public synchronized Money getTotalVoucherPayments() {
		//TODO need functionality to recalculate the value
		return Money.ZERO;
	}

	public synchronized Money getPreviousOwing() {
		return model.getPreviousOwing();
	}

	public synchronized Money getMinimumPayableNow() {
		return getModel().getPayment().getAmount();
	}


	/**
	 * Check that any discounts potentially can be applied for actual enrollments.
	 *
	 * @return true if some discounts can be applied for actual enrollments.
	 */
	public synchronized boolean hasSuitableClasses(StudentConcession studentConcession) {
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
	public synchronized void performAction(ActionParameter param) {

		illegalState = false;
		illegalModel = false;
		errors.clear();
        warnings.clear();


		adjustState(param.action);
		if (!state.allowedActions.contains(param.action)) {
			addError(Message.illegalState);
			illegalState = true;
			return;
		}

		APurchaseAction action = param.action.createAction(this, param);
		if (action.action())
            actionSuccess();
        else
            illegalModel = true;
	}

    private void actionSuccess() {
        if (isEditCheckout() || isEditPayment() || isEditCorporatePass())
        {
            //the code needs to recalcalute money values for payment and discount for all actions on checkout page and payment editor
            updateTotalIncGst();
            updateTotalDiscountAmountIncTax();

            //we need commit all changes which were made on editPayment page otherwise we can get incorrect contact relations (in particular invoices)
            if (getState() == editPayment || getState() == editCorporatePass)
                getModel().getObjectContext().commitChanges();

        }
    }

    /**
	 * The method is used to adjust current state when user uses browser action like back,forward
	 * @return  true state was changed.
	 */
	public synchronized boolean adjustState(Action action) {
		if (!COMMON_ACTIONS.contains(action))
			return true;
		switch (state) {
			case editPayment:
			case editCorporatePass:
			case paymentProgress:
            case paymentResult:
				ActionParameter parameter = new ActionParameter(backToEditCheckout);
				parameter.setValue(action);
				ActionBackToEditCheckout actionBackToEditCheckout = backToEditCheckout.createAction(this, parameter);
				return actionBackToEditCheckout.action();
			case init:
			case addContact:
			case editConcession:
			case editContact:
			case editCheckout:
				return false;
			default:
				throw new IllegalArgumentException();
		}
	}

	public PurchaseModel getModel() {
		return model;
	}


	/**
	 * @param fillRequiredProperties if true we show only required properties where value is null
	 */
    synchronized void prepareContactEditor(Contact contact, boolean fillRequiredProperties,
                              Action addAction,
                              Action cancelAction) {
		contactEditorController = new ContactEditorController();
		contactEditorController.setPurchaseController(this);
		contactEditorController.setContact(contact);
		contactEditorController.setObjectContext(contact.getObjectContext());
		contactEditorController.setContactFiledsSet(enrolment);
        contactEditorController.setAddAction(addAction);
        contactEditorController.setCancelAction(cancelAction);
		if (!contact.getObjectId().isTemporary() && fillRequiredProperties)
			contactEditorController.setFillRequiredProperties(fillRequiredProperties);
	}

    synchronized void recalculateEnrolmentInvoiceLines() {

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
    synchronized Enrolment createEnrolment(CourseClass courseClass, Student student) {
		Enrolment enrolment = model.getObjectContext().newObject(Enrolment.class);
		enrolment.setStatus(EnrolmentStatus.NEW);
		enrolment.setSource(PaymentSource.SOURCE_WEB);

		enrolment.setCollege(student.getCollege());
		enrolment.setStudent(student);
		enrolment.setCourseClass(courseClass);
		return enrolment;
	}


	synchronized ProductItem createProductItem(Contact contact, Product product) {
		if (product instanceof VoucherProduct) {
			VoucherProduct vp = (VoucherProduct) product;
			return voucherService.createVoucher(vp, contact, vp.getPriceExTax());
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

	public synchronized boolean isIllegalModel() {
		return illegalModel;
	}

	public synchronized boolean isIllegalState() {
		return illegalState;
	}

	public synchronized boolean isEditCheckout() {
		return state == editCheckout;
	}

	public synchronized boolean isEditContact() {
		return state == editContact;
	}

	public synchronized boolean isEditConcession() {
		return state == State.editConcession;
	}

	public synchronized boolean isAddContact() {
		return state == State.addContact;
	}

	public synchronized boolean isActiveConcessionTypes() {
		return concessionsService.hasActiveConcessionTypes();
	}

	public synchronized boolean isEditPayment() {
		return state == editPayment;
	}

    public synchronized boolean isEditCorporatePass() {
        return state == editCorporatePass;
    }


    //return true when the payment process has a result.
	public synchronized boolean isPaymentResult() {
		return state == paymentResult;
	}

    public synchronized boolean isPaymentProgress() {
        return state == paymentProgress;
    }

    //return true when the payment process was finished
	public synchronized boolean isFinished() {
		if (state == paymentResult)
		{
			if (getModel().getCorporatePass() != null)
				return true;
			else if (paymentEditorController != null)
				return paymentEditorController.isProcessFinished();
			return illegalModel;
		}
		return false;
	}

    /**
     * returns true when current payer can get credit
     */
    public synchronized boolean hasPreviousOwing()
    {
        Money owing = getPreviousOwing();
        return getModel().getPayer() != null && owing.isGreaterThan(Money.ZERO);
    }

    /**
     * returns true when current payer can get credit
     */
    public synchronized boolean isCreditAvailable()
    {
		Money owing = getPreviousOwing();
        return getModel().getPayer() != null && owing.isLessThan(Money.ZERO);
    }

    public synchronized boolean isApplyPrevOwing() {
        return getModel().isApplyPrevOwing();
    }

    /**
     * the method retuns true if payer alread is set. the method used to define show reset button for contact editor dialog
     */
    public synchronized boolean isPayerInitialized()
    {
        return getModel().getPayer() != null;
    }


	public synchronized ConcessionDelegate getConcessionDelegate() {
		return concessionEditorController;
	}

	public IConcessionsService getConcessionsService() {
		return concessionsService;
	}

	public void setConcessionsService(IConcessionsService concessionsService) {
		this.concessionsService = concessionsService;
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

	public synchronized ContactEditorDelegate getContactEditorDelegate() {
		return contactEditorController;
	}

	synchronized void resetContactEditorController()
	{
	 	this.contactEditorController = null;
	}


	public Messages getMessages() {
		return messages;
	}

	public synchronized boolean isNeedConcessionReminder() {
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
		return true;
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

	public synchronized PaymentEditorDelegate getPaymentEditorDelegate() {
		return paymentEditorController;
	}

    public synchronized void setPaymentEditorController(PaymentEditorController paymentEditorController) {
        this.paymentEditorController = paymentEditorController;
    }


    public synchronized VoucherRedemptionHelper getVoucherRedemptionHelper() {
		return voucherRedemptionHelper;
	}

	public synchronized void setConcessionEditorController(ConcessionEditorController concessionEditorController) {
		this.concessionEditorController = concessionEditorController;
	}

	synchronized void setAddContactController(AddContactController addContactController) {
		this.addContactController = addContactController;
	}

    public synchronized AddContactDelegate getAddContactDelegate() {
        return addContactController;
    }


    public ICayenneService getCayenneService() {
		return cayenneService;
	}

	public IPaymentGatewayServiceBuilder getPaymentGatewayServiceBuilder() {
		return paymentGatewayServiceBuilder;
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


	public synchronized void addError(Message message, Object... params) {
		errors.put(message.name(), message.getMessage(messages, params));
	}

    public synchronized Map<String, String> getErrors() {
        return errors;
    }

    public synchronized Map<String, String> getWarnings() {
        return warnings;
    }

    public synchronized void addWarning(Message message, Object... params) {
        warnings.put(message.name(), message.getMessage(messages, params));
    }

    public synchronized void setErrors(Map<String,String> errors) {
		this.errors.clear();
		this.errors.putAll(errors);
	}

	public synchronized boolean isPaymentState() {
		return (state == State.editPayment ||
                state == State.paymentResult ||
                state == State.paymentProgress ||
                state == State.editCorporatePass
        );
	}

    public void setParallelExecutor(ParallelExecutor parallelExecutor) {

       this.parallelExecutor = parallelExecutor;
    }

    public ParallelExecutor getParallelExecutor() {
        return parallelExecutor;
    }

    public synchronized void refreshPrevOwingStatus() {
        if (this.hasPreviousOwing())
            this.getModel().setApplyPrevOwing(true);
        else if (this.isCreditAvailable())
            this.getModel().setApplyPrevOwing(false);
    }

	public synchronized List<Discount> getDiscounts()
	{
		return this.getModel().getDiscounts();
	}

    public Money updateTotalIncGst() {
        Money result = Money.ZERO;
        for (Contact contact : getModel().getContacts()) {
            for (Enrolment enabledEnrolment : getModel().getEnabledEnrolments(contact)) {
                InvoiceLine invoiceLine = enabledEnrolment.getInvoiceLine();
                result = result.add(invoiceLine.getPriceTotalIncTax().subtract(invoiceLine.getDiscountTotalIncTax()));
            }
            for (ProductItem enabledProductItem : getModel().getEnabledProductItems(contact)) {
                InvoiceLine invoiceLine = enabledProductItem.getInvoiceLine();
                result = result.add(invoiceLine.getPriceTotalIncTax().subtract(invoiceLine.getDiscountTotalIncTax()));
            }
        }

        if (isApplyPrevOwing())
        {
            Money previousOwing = getPreviousOwing();
            result = result.add(previousOwing);
        }


        if (isEditCorporatePass())
            result = Money.ZERO;
        else
            result = (result.isLessThan(Money.ZERO) ? Money.ZERO : result);

		getModel().getPayment().setAmount(result);
		getModel().getPayment().getPaymentInLines().get(0).setAmount(result);

        Money totalGst = InvoiceUtil.sumInvoiceLines(getModel().getInvoice().getInvoiceLines(), true);
        Money totalExGst = InvoiceUtil.sumInvoiceLines(getModel().getInvoice().getInvoiceLines(), false);
        getModel().getInvoice().setTotalExGst(totalExGst);
        getModel().getInvoice().setTotalGst(totalGst);
        getModel().getInvoice().setCorporatePassUsed(getModel().getCorporatePass());
        return result;
    }


    public void updateTotalDiscountAmountIncTax() {
        Money totalDiscountAmountIncTax = Money.ZERO;
        for (Contact contact : getModel().getContacts()) {
            for (Enrolment enabledEnrolment : getModel().getEnabledEnrolments(contact)) {
                totalDiscountAmountIncTax = totalDiscountAmountIncTax.add(enabledEnrolment.getInvoiceLine().getDiscountTotalIncTax());
            }
            for (ProductItem enabledProductItem : getModel().getEnabledProductItems(contact)) {
                totalDiscountAmountIncTax = totalDiscountAmountIncTax.add(enabledProductItem.getInvoiceLine().getDiscountTotalIncTax());
            }
        }
        getModel().setTotalDiscountAmountIncTax(totalDiscountAmountIncTax);
    }

    String getClassName(CourseClass courseClass) {
        return String.format("%s (%s-%s)", courseClass.getCourse().getName(), courseClass.getCourse().getCode(), courseClass.getCode());
    }


    public static enum State {
		init(Action.init, Action.addContact),
		editCheckout(COMMON_ACTIONS,addDiscount,removeDiscount, proceedToPayment, addCourseClass),
		editConcession(addConcession, removeConcession, cancelConcessionEditor),
		addContact(Action.addContact, addPayer, cancelAddContact,cancelAddPayer),
		editContact(Action.addContact, addPayer, cancelAddContact,cancelAddPayer),
		editPayment(makePayment, backToEditCheckout,addDiscount, creditAccess, owingApply, changePayer, addPayer,selectCorporatePassEditor),
        editCorporatePass(makePayment, backToEditCheckout, addCorporatePass, selectCardEditor),
        paymentProgress(showPaymentResult),
		paymentResult(proceedToPayment,showPaymentResult);

		private List<Action> allowedActions;


        State(List<Action> commonAction,Action...   allowedActions)
        {
            this.allowedActions = new ArrayList<Action>(commonAction);
            Collections.addAll(this.allowedActions, allowedActions);
        }

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
		changePayer(ActionChangePayer.class, Contact.class, State.class),
		setVoucherPrice(ActionSetVoucherPrice.class, Money.class),
		enableEnrolment(ActionEnableEnrolment.class, Enrolment.class),
		disableEnrolment(ActionDisableEnrolment.class, Enrolment.class),
		enableProductItem(ActionEnableProductItem.class, ProductItem.class),
		disableProductItem(ActionDisableProductItem.class, ProductItem.class),
		addContact(ActionAddContact.class, ContactCredentials.class),
		addConcession(ActionAddConcession.class, StudentConcession.class),
		removeConcession(ActionRemoveConcession.class, ConcessionType.class, Contact.class),
		addDiscount(ActionAddDiscount.class, String.class, Discount.class),
        removeDiscount(ActionRemoveDiscount.class, String.class, Discount.class),
		addVoucher(ActionAddVoucher.class, String.class, Voucher.class),
		startConcessionEditor(ActionStartConcessionEditor.class, Contact.class),
		cancelConcessionEditor(ActionCancelConcessionEditor.class, Contact.class),
		cancelAddContact(ActionCancelAddContact.class),
        cancelAddPayer(ActionCancelAddPayer.class),
		creditAccess(ActionCreditAccess.class, String.class),
		owingApply(ActionOwingApply.class),
		proceedToPayment(ActionProceedToPayment.class),
		makePayment(ActionMakePayment.class),
        showPaymentResult(ActionShowPaymentResult.class),
		backToEditCheckout(ActionBackToEditCheckout.class),
        addCourseClass(ActionAddCourseClass.class,CourseClass.class),
        addPayer(ActionAddPayer.class, Contact.class),
        addCorporatePass(ActionAddCorporatePass.class, String.class),
        selectCardEditor(ActionSelectCardEditor.class),
        selectCorporatePassEditor(ActionSelectCorporatePassEditor.class);

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

        public boolean hasValues()
        {
            return !values.isEmpty();
        }
	}

	public static enum Message {
		noSelectedItemForPurchase,
        noEnabledItemForPurchase,
		contactAlreadyAdded,
		discountNotFound,
		creditAccessPasswordIsWrong,
        passwordShouldBeSpecified,
		duplicatedEnrolment,
		noCourseClassPlaces,
		courseClassEnded,
		sessionExpired,
		payerNotDefined,
		illegalState,
		voucherNotMatch,
		voucherExpired,
		voucherRedeemed,
		voucherLockedAnotherUser,
		concessionAlreadyAdded,
        payerHadUnfinishedPayment,
		codeEmpty,
        classAlreadyAdded,
        productAlreadyAdded,
        itemsWasAddedFromShoppingBasket,
		discountAlreadyAdded,
        corporatePassNotFound,
        corporatePassInvalidCourseClass,
        corporatePassAdded,
        corporatePassShouldBeEntered;

		public String getMessage(Messages messages, Object... params) {
			return messages.format(String.format("message-%s", name()), params);
		}

	}


}
