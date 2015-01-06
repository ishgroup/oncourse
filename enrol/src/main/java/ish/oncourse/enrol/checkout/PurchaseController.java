package ish.oncourse.enrol.checkout;

import ish.common.types.ApplicationStatus;
import ish.common.types.ConfirmationStatus;
import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentType;
import ish.common.types.ProductStatus;
import ish.common.types.ProductType;
import ish.math.Money;
import ish.oncourse.enrol.checkout.contact.AddContactController;
import ish.oncourse.enrol.checkout.contact.AddContactDelegate;
import ish.oncourse.enrol.checkout.contact.ContactCredentials;
import ish.oncourse.enrol.checkout.contact.ContactEditorDelegate;
import ish.oncourse.enrol.checkout.payment.PaymentEditorController;
import ish.oncourse.enrol.checkout.payment.PaymentEditorDelegate;
import ish.oncourse.enrol.services.concessions.IConcessionsService;
import ish.oncourse.enrol.services.invoice.IInvoiceProcessingService;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.model.*;
import ish.oncourse.services.application.IApplicationService;
import ish.oncourse.services.datalayer.IDataLayerFactory;
import ish.oncourse.services.discount.IDiscountService;
import ish.oncourse.services.payment.IPaymentService;
import ish.oncourse.services.paymentexpress.IPaymentGatewayServiceBuilder;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.voucher.IVoucherService;
import ish.oncourse.services.voucher.VoucherRedemptionHelper;
import ish.oncourse.util.InvoiceUtils;
import ish.util.InvoiceUtil;
import ish.util.ProductUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.services.ParallelExecutor;
import org.joda.time.DateTime;
import org.joda.time.Years;

import java.util.*;

import static ish.oncourse.enrol.checkout.PurchaseController.Action.*;
import static ish.oncourse.enrol.checkout.PurchaseController.State.*;
import static ish.oncourse.services.datalayer.DataLayerFactory.Cart;
import static java.util.Arrays.asList;

/**
 * Controller class for purchase page in enrol.
 *
 * @author dzmitry
 */
public class PurchaseController {

	protected static final Logger LOGGER = Logger.getLogger(PurchaseController.class);

    /**
     * If performing action is one from the list and current state is editPayment
     * we need perform backToEditCheckout action before the action.
     * see method PurchaseController.adjustState()
     */
	public static final List<Action> COMMON_ACTIONS = Collections.unmodifiableList(Arrays.asList(
			enableEnrolment, enableProductItem, enableApplication,
			disableEnrolment, disableProductItem, disableApplication,
			setVoucherPrice,
			startConcessionEditor, Action.addContact, addGuardian));

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
	private IPaymentService paymentService;
    private IDataLayerFactory dataLayerFactory;
	private IApplicationService applicationService;

	private VoucherRedemptionHelper voucherRedemptionHelper;
    private PurchaseModelValidator modelValidator;



	private State state = State.init;

	private boolean illegalModel = false;

	private boolean illegalState = false;

	private ConcessionEditorController concessionEditorController;
	private AddContactController addContactController;
	private ContactEditorDelegate contactEditorDelegate;
	private PaymentEditorController paymentEditorController;

    private ValidationResult validationResult;

	private ParallelExecutor parallelExecutor;

	/**
	 * @return the current state
	 */
	public  State getState() {
		return state;
	}

	 void setState(State state) {
		this.state = state;
	}

	/**
	 * Check that invoice lines linked with the enrollments or productitems list applied some discounts
	 *
	 * @return true if any discount applied.
	 */
	public  boolean hasDiscount() {
		return !getTotalDiscountAmountIncTax().isZero();
	}

	/**
	 * Calculate total discount amount for all actual enrollments.
	 *
	 * @return total discount amount for all actual enrollments.
	 */
	public  Money getTotalDiscountAmountIncTax() {
		return getModel().getTotalDiscountAmountIncTax();
	}

	public  Money getTotalPayment() {
		return getModel().getInvoice().getTotalGst();
	}

	public  Money getPreviousOwing() {
		return model.getPreviousOwing();
	}

	public  Money getMinimumPayableNow() {
		return getModel().getPayment().getAmount();
	}


	/**
	 * Single entry point to perform all actions. {@link Action} and {@link ActionParameter} values should be specified.
	 */
    @Deprecated //we need to use performAction(APurchaseAction purchaseAction, Action action)
	public  void performAction(ActionParameter param) {
       performAction(param.action.createAction(this, param), param.action);
	}

    public  void performAction(APurchaseAction purchaseAction, Action action)
    {
        illegalState = false;
        illegalModel = false;
        validationResult.clear();

        adjustState(action);
        if (!state.allowedActions.contains(action)) {
            addError(Message.illegalState);
            illegalState = true;
            return;
        }

        purchaseAction.setController(this);
        if (purchaseAction.action())
            actionSuccess();
        else
            illegalModel = true;
    }

	private void actionSuccess() {
		if (isEditCheckout() || isEditPayment() || isEditCorporatePass()) {
			//the code needs to recalcalute money values for payment and discount for all actions on checkout page and payment editor
			updateTotalIncGst();
			updateTotalDiscountAmountIncTax();
		}
	}

	/**
	 * The method is used to adjust current state when user uses browser action like back,forward
	 *
	 * @return true state was changed.
	 */
	public  boolean adjustState(Action action) {
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


	 void recalculateEnrolmentInvoiceLines() {

		for (Contact contact : model.getContacts()) {
			for (Enrolment enrolment : model.getEnabledEnrolments(contact)) {
				List<InvoiceLine> invoiceLines = new ArrayList<>(enrolment.getInvoiceLines());
				for (InvoiceLine invoiceLine : invoiceLines) {
					invoiceLine.setEnrolment(null);
				}
				model.getObjectContext().deleteObjects(invoiceLines);

				InvoiceLine newInvoiceLine = invoiceProcessingService.createInvoiceLineForEnrolment(enrolment, model.getDiscounts());
				newInvoiceLine.setInvoice(model.getInvoice());
				newInvoiceLine.setEnrolment(enrolment);
			}
		}
	}


	/**
	 * Creates the new {@link Enrolment} entity for the given courseClass and
	 * Student.
	 */
	 Enrolment createEnrolment(CourseClass courseClass, Student student) {
		Enrolment enrolment = new Enrolment();
		enrolment.setStatus(EnrolmentStatus.NEW);
		enrolment.setSource(PaymentSource.SOURCE_WEB);
		model.getObjectContext().registerNewObject(enrolment);
		enrolment.setCollege(student.getCollege());
		enrolment.setStudent(student);
		enrolment.setCourseClass(courseClass);
		return enrolment;
	}


	 ProductItem createProductItem(Contact contact, Product product) {
		if (product instanceof VoucherProduct) {
			VoucherProduct vp = (VoucherProduct) product;
			return voucherService.createVoucher(vp, contact);
		} else if (product instanceof MembershipProduct) {
			MembershipProduct mp = (MembershipProduct) product;
			Membership membership = getModel().getObjectContext().newObject(Membership.class);
			membership.setCollege(mp.getCollege());
			membership.setContact(contact);
			membership.setExpiryDate(ProductUtil.calculateExpiryDate(new Date(), mp.getExpiryType(), mp.getExpiryDays()));
			membership.setProduct(mp);
			membership.setStatus(ProductStatus.NEW);
			return membership;
		} else if (product instanceof ArticleProduct) {
			ArticleProduct ap = (ArticleProduct) product;
			Article article = getModel().getObjectContext().newObject(Article.class);
			article.setCollege(ap.getCollege());
			article.setContact(contact);
			article.setProduct(ap);
			article.setStatus(ProductStatus.NEW);
			return article;
		} else {
			throw new IllegalArgumentException("Unsupported product type.");
		}
	}

	Application createApplication(Student student, Course course) {
		Application application = getModel().getObjectContext().newObject(Application.class);
		application.setCollege(student.getCollege());
		application.setStudent(student);
		application.setCourse(course);
		application.setStatus(ApplicationStatus.NEW);
		application.setSource(PaymentSource.SOURCE_WEB);
		application.setConfirmationStatus(ConfirmationStatus.NOT_SENT);
		return application;
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

	public  boolean isIllegalModel() {
		return illegalModel;
	}

	public  boolean isIllegalState() {
		return illegalState;
	}

	public  boolean isEditCheckout() {
		return state == editCheckout;
	}

	public  boolean isEditContact() {
		return state == editContact;
	}

	public  boolean isEditConcession() {
		return state == State.editConcession;
	}

	public  boolean isAddContact() {
		return state == State.addContact;
	}

	public  boolean isActiveConcessionTypes() {
		return concessionsService.hasActiveConcessionTypes();
	}

	public  boolean isEditPayment() {
		return state == editPayment;
	}

	public  boolean isEditCorporatePass() {
		return state == editCorporatePass;
	}


	//return true when the payment process has a result.
	public  boolean isPaymentResult() {
		return state == paymentResult;
	}

	public  boolean isPaymentProgress() {
		return state == paymentProgress;
	}

	//return true when the payment process was finished
	public  boolean isFinished() {
		if (state == paymentResult) {
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
	public  boolean hasPreviousOwing() {
		Money owing = getPreviousOwing();
		return getModel().getPayer() != null && owing.isGreaterThan(Money.ZERO);
	}

	/**
	 * returns true when current payer can get credit
	 */
	public  boolean isCreditAvailable() {
		Money owing = getPreviousOwing();
		return getModel().getPayer() != null && owing.isLessThan(Money.ZERO);
	}

	public  boolean isApplyPrevOwing() {
		return getModel().isApplyPrevOwing();
	}

	/**
	 * the method retuns true if payer alread is set. the method used to define show reset button for contact editor dialog
	 */
	public  boolean isPayerInitialized() {
		return getModel().getPayer() != null;
	}


	public  ConcessionDelegate getConcessionDelegate() {
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

    public IDataLayerFactory getDataLayerFactory() {
        return dataLayerFactory;
    }

    public void setDataLayerFactory(IDataLayerFactory dataLayerFactory) {
        this.dataLayerFactory = dataLayerFactory;
    }


    public PreferenceController getPreferenceController() {
		return preferenceController;
	}

	public void setPreferenceController(PreferenceController preferenceController) {
		this.preferenceController = preferenceController;
	}

	public  ContactEditorDelegate getContactEditorDelegate() {
		return contactEditorDelegate;
	}

    public  void setContactEditorDelegate(ContactEditorDelegate contactEditorDelegate) {
        this.contactEditorDelegate = contactEditorDelegate;
    }


	public Messages getMessages() {
		return validationResult.getMessages();
	}

	public  boolean isNeedConcessionReminder() {
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

	public void setCayenneService(ICayenneService cayenneService) {
		this.cayenneService = cayenneService;
	}

	public void setPaymentGatewayServiceBuilder(IPaymentGatewayServiceBuilder paymentGatewayServiceBuilder) {
		this.paymentGatewayServiceBuilder = paymentGatewayServiceBuilder;
	}

	public  PaymentEditorDelegate getPaymentEditorDelegate() {
		return paymentEditorController;
	}

	public  void setPaymentEditorController(PaymentEditorController paymentEditorController) {
		this.paymentEditorController = paymentEditorController;
	}

    public void setVoucherRedemptionHelper(VoucherRedemptionHelper voucherRedemptionHelper)
    {
        this.voucherRedemptionHelper = voucherRedemptionHelper;
    }

	public  VoucherRedemptionHelper getVoucherRedemptionHelper() {
		return voucherRedemptionHelper;
	}

	public  void setConcessionEditorController(ConcessionEditorController concessionEditorController) {
		this.concessionEditorController = concessionEditorController;
	}

	 void setAddContactController(AddContactController addContactController) {
		this.addContactController = addContactController;
	}

	public  AddContactDelegate getAddContactDelegate() {
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

	public IApplicationService getApplicationService() {
		return applicationService;
	}

	public void setApplicationService(IApplicationService applicationService) {
		this.applicationService = applicationService;
	}

	public  void addError(Message message, Object... params) {
		validationResult.addError(message, params);
	}

	public  Map<String, String> getErrors() {
		return validationResult.getErrors();
	}

	public  Map<String, String> getWarnings() {
		return validationResult.getWarnings();
	}

	public  void addWarning(Message message, Object... params) {
        validationResult.addWarning(message, params);
	}

	public  void setErrors(Map<String, String> errors) {
        validationResult.setErrors(errors);
	}

	public  boolean isPaymentState() {
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

	public  void refreshPrevOwingStatus() {
		if (this.hasPreviousOwing())
			this.getModel().setApplyPrevOwing(true);
		else if (this.isCreditAvailable())
			this.getModel().setApplyPrevOwing(false);
	}

	public  List<Discount> getDiscounts() {
		return this.getModel().getDiscounts();
	}

    private void updateVoucherPayments() {
        voucherRedemptionHelper.clear();

        if (getState() != State.editCorporatePass) {

            for (Voucher voucher : getModel().getSelectedVouchers()) {
                voucherRedemptionHelper.addVoucher(voucher, voucher.getValueRemaining());
            }

            voucherRedemptionHelper.addInvoiceLines(getModel().getInvoice().getInvoiceLines());

            if (isApplyPrevOwing())
                voucherRedemptionHelper.addPreviousOwingInvoices(InvoiceUtils.getOwingInvoices(getModel().getPayer()));

            voucherRedemptionHelper.processAgainstInvoices();

            getModel().setVoucherPayments(voucherRedemptionHelper.getPayments());
        }
    }

	public Money updateTotalIncGst() {
        updateVoucherPayments();

		Money result = Money.ZERO;
		for (Contact contact : getModel().getContacts()) {
			for (Enrolment enabledEnrolment : getModel().getEnabledEnrolments(contact)) {
				for (InvoiceLine invoiceLine : enabledEnrolment.getInvoiceLines()) {
					result = result.add(invoiceLine.getPriceTotalIncTax().subtract(invoiceLine.getDiscountTotalIncTax()));
				}
			}
			for (ProductItem enabledProductItem : getModel().getEnabledProductItems(contact)) {
				InvoiceLine invoiceLine = enabledProductItem.getInvoiceLine();
				result = result.add(invoiceLine.getPriceTotalIncTax().subtract(invoiceLine.getDiscountTotalIncTax()));
			}
		}

		if (isApplyPrevOwing()) {
			Money previousOwing = getPreviousOwing();
			result = result.add(previousOwing);
		}


		if (isEditCorporatePass())
			result = Money.ZERO;
		else
			result = (result.isLessThan(Money.ZERO) ? Money.ZERO : result);

        List<PaymentIn> paymentIns = getModel().getVoucherPayments();
        for (PaymentIn paymentIn : paymentIns) {
            result = result.subtract(paymentIn.getAmount());
            result = (result.isLessThan(Money.ZERO) ? Money.ZERO : result);
        }

		/**
		 * we need to set payment type to internal when amount is zero because admin application
		 * billing logic uses this type to define which payments were sent to DPS and which were not sent.
		 */
		if (result.isZero())
			getModel().getPayment().setType(PaymentType.INTERNAL);
		else
			getModel().getPayment().setType(PaymentType.CREDIT_CARD);



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
				for (InvoiceLine invoiceLine : enabledEnrolment.getInvoiceLines()) {
					totalDiscountAmountIncTax = totalDiscountAmountIncTax.add(invoiceLine.getDiscountTotalIncTax());
				}
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

	public boolean isCorporatePassPaymentEnabled() {
		return preferenceController.isCorporatePassPaymentEnabled();
	}

	public boolean isCreditCardPaymentEnabled() {
		return preferenceController.isCreditCardPaymentEnabled();
	}

	public IPaymentService getPaymentService() {
		return paymentService;
	}

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	public void cloneModel() {
		  CloneModelHelper helper = new CloneModelHelper();
		  helper.setObjectContext(cayenneService.newContext());
		  helper.setPurchaseController(this);
		  helper.cloneModel();
    }

	public boolean hasAvailableEnrolmentPlaces(Enrolment enrolment) {
		CourseClass courseClass = enrolment.getCourseClass();

		SelectQuery query = new SelectQuery(Enrolment.class);
		query.setQualifier(ExpressionFactory.matchExp(Enrolment.COURSE_CLASS_PROPERTY, courseClass).andExp(ExpressionFactory.inExp(Enrolment.STATUS_PROPERTY, EnrolmentStatus.IN_TRANSACTION, EnrolmentStatus.SUCCESS)));
		List<Enrolment> activeEnrolments = getModel().getObjectContext().performQuery(query);

		List<Enrolment> currentEnrolments = model.getAllEnableEnrolmentBy(courseClass);
		int count = (currentEnrolments.contains(enrolment) ? currentEnrolments.size() : currentEnrolments.size() + 1) + activeEnrolments.size();

		return courseClass.getMaximumPlaces() >= count;


	}

    public PurchaseModelValidator getModelValidator() {
        return modelValidator;
    }

    public void setModelValidator(PurchaseModelValidator modelValidator) {
        this.modelValidator = modelValidator;
    }

    public boolean needGuardianFor(Contact contact) {

        if (contact.getDateOfBirth() != null) {
            Integer age = Years.yearsBetween(new DateTime(contact.getDateOfBirth().getTime()),
                    new DateTime(new Date().getTime())).getYears();
            if (preferenceController.isCollectParentDetails() &&
                    preferenceController.getContactAgeWhenNeedParent() > age) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return can be null
     */
    public Contact getGuardianFor(Contact contact)
    {
        ContactRelation contactRelation = getModel().getGuardianRelationFor(contact);
        return (contactRelation != null ? getModel().getGuardianRelationFor(contact).getFromContact(): null);
    }

    public ContactRelationType getGuardianRelationType()
    {
        Expression expression = ExpressionFactory.matchExp(ContactRelationType.COLLEGE_PROPERTY, model.getCollege());
        expression = expression.andExp(ExpressionFactory.matchExp(ContactRelationType.ANGEL_ID_PROPERTY, PurchaseModel.ANGEL_ID_ContactRelationType_ParentOrGuardian));
        SelectQuery selectQuery = new SelectQuery(ContactRelationType.class, expression);
        List<ContactRelationType> types = (List<ContactRelationType>) getModel().getObjectContext().performQuery(selectQuery);
        return types.isEmpty() ? null: types.get(0);
    }

    public <T extends Persistent> T reloadObject(T object)
    {
        SelectQuery selectQuery = new SelectQuery(Cayenne.getObjEntity(object),
                ExpressionFactory.matchExp(object));
        selectQuery.setCacheStrategy(QueryCacheStrategy.NO_CACHE);
        return (T) Cayenne.objectForQuery(getModel().getObjectContext(), selectQuery);
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(ValidationResult validationResult) {
        this.validationResult = validationResult;
    }

    public Cart getCart()
    {
        LinkedList list = new LinkedList();
        list.addAll(getModel().getAllEnabledEnrolments());
        list.addAll(getModel().getAllEnabledProductItems());

        if (list.isEmpty())
            return null;

        Cart cart = dataLayerFactory.build(list);
        if (isFinished() &&
                getPaymentEditorDelegate() != null &&
                getPaymentEditorDelegate().isPaymentSuccess())
        {
            cart.id = getModel().getInvoice().getId().toString();
        }
        return cart;
    }

	public void setConfirmationStatus(ConfirmationStatus confirmationStatus) {
		if (getModel().getPayment() != null)
		{
			getModel().getPayment().setConfirmationStatus(confirmationStatus);
		}
		
		// no invoice when applications only 
		if (getModel().getInvoice() != null) {
			getModel().getInvoice().setConfirmationStatus(confirmationStatus);
		}
		
		for (Enrolment enrolment : getModel().getAllEnabledEnrolments()) {
			enrolment.setConfirmationStatus(confirmationStatus);
		}
		
		for (ProductItem productItem : getModel().getAllEnabledProductItems()) {
			if (ProductType.VOUCHER.getDatabaseValue() == productItem.getType()) {
				productItem.setConfirmationStatus(confirmationStatus);
			}
		}
		
		for (Application application : getModel().getAllEnabledApplications()) {
			application.setConfirmationStatus(confirmationStatus);
		}
	}

	public static enum State {
		init(Action.init, Action.addContact),
		editCheckout(COMMON_ACTIONS, addCode, selectVoucher, deselectVoucher, removeDiscount, proceedToPayment, addCourseClass, addProduct),
		editConcession(addConcession, removeConcession, cancelConcessionEditor),
		addContact(Action.addContact, cancelAddContact, addPersonPayer, addCompanyPayer, cancelAddPayer, addGuardian, cancelAddGuardian),
		editContact(Action.addContact, cancelAddContact, addPersonPayer, addCompanyPayer, cancelAddPayer, addGuardian, cancelAddGuardian),
		editPayment(makePayment, backToEditCheckout, addCode, selectVoucher, deselectVoucher, creditAccess, owingApply, changePayer, addPersonPayer, addCompanyPayer, selectCorporatePassEditor),
		editCorporatePass(makePayment, backToEditCheckout, addCorporatePass, selectCardEditor),
		paymentProgress(showPaymentResult),
		paymentResult(proceedToPayment, showPaymentResult);

		private List<Action> allowedActions;


		State(List<Action> commonAction, Action... allowedActions) {
			this.allowedActions = new ArrayList<>(commonAction);
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
		enableProductItem(ActionEnableProductItem.class, ProductItem.class, Money.class),
		disableProductItem(ActionDisableProductItem.class, ProductItem.class),
		enableApplication(ActionEnableApplication.class, Application.class),
		disableApplication(ActionDisableApplication.class, Application.class),
		addContact(ActionAddContact.class, ContactCredentials.class),
		addConcession(ActionAddConcession.class, StudentConcession.class),
		removeConcession(ActionRemoveConcession.class, ConcessionType.class, Contact.class),
        addCode(ActionAddCode.class, String.class),
		removeDiscount(ActionRemoveDiscount.class, String.class, Discount.class),
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
		addCourseClass(ActionAddCourseClass.class, CourseClass.class),
		addProduct(ActionAddProduct.class, Product.class),
		addPersonPayer(ActionAddPersonPayer.class, Contact.class),
		addCompanyPayer(ActionAddCompanyPayer.class, Contact.class),
		addCorporatePass(ActionAddCorporatePass.class, String.class),
		selectCardEditor(ActionSelectCardEditor.class),
		selectCorporatePassEditor(ActionSelectCorporatePassEditor.class),
        selectVoucher(ActionSelectVoucher.class, Long.class),
        deselectVoucher(ActionDeselectVoucher.class, Long.class),
        addGuardian(ActionAddGuardian.class, Contact.class),
        cancelAddGuardian(ActionCancelAddGuardian.class);

		private Class<? extends APurchaseAction> actionClass;
		private List<Class<?>> paramTypes;

		private Action(Class<? extends APurchaseAction> actionClass, Class<?>... paramType) {
			this.actionClass = actionClass;
			this.paramTypes = new ArrayList<>(asList(paramType));
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
			values = new HashMap<>();
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

		public boolean hasValues() {
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
		dpsHasNotFinishedProcessPreviousPayment,
		codeEmpty,
		classAlreadyAdded,
		productAlreadyAdded,
		itemsWasAddedFromShoppingBasket,
		discountAlreadyAdded,
		corporatePassNotFound,
		corporatePassInvalidCourseClass,
		corporatePassAdded,
		corporatePassShouldBeEntered,
		noEnabledPaymentMethods,
		corporatePassNotEnabled,
		creditCardPaymentNotEnabled,
		duplicatedMembership,
		enterVoucherPrice,
		noPlacesLeft,
        incorrectCode,
        voucherAlreadyAdded,
        voucherWrongPayer,
        voucherAlreadyBeingUsed,
        voucherRedeemNotAllow,
		ageRequirementsOverMaximum,
		ageRequirementsUnderMinimum,
        payerWasChangedToGuardian,
        contactNeedsGuardian,
        guardianAgeIsWrong,
        payerSetAsGuardian,
        courseClassCancelled,
        courseClassIsNotVisible,
		applicationReceived,
		applicationAlreadyApplyed,
		applicationAlreadyInTransaction;

		public String getMessage(Messages messages, Object... params) {
			return messages.format(String.format("message-%s", name()), params);
		}

	}


}
