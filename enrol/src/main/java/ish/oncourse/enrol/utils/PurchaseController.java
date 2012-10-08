package ish.oncourse.enrol.utils;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.math.Money;
import ish.oncourse.enrol.services.invoice.IInvoiceProcessingService;
import ish.oncourse.model.*;
import ish.oncourse.services.discount.IDiscountService;
import ish.oncourse.services.voucher.IVoucherService;
import ish.oncourse.services.voucher.VoucherRedemptionHelper;
import ish.oncourse.util.FormatUtils;
import org.apache.log4j.Logger;

import java.text.Format;
import java.util.*;

/**
 * Controller class for purchase page in enrol.
 *
 * @author dzmitry
 */
public class PurchaseController {
	public static final Logger LOGGER = Logger.getLogger(PurchaseController.class);

	private PurchaseModel model;


	private IInvoiceProcessingService invoiceProcessingService;
	private IDiscountService discountService;
	private IVoucherService voucherService;

	private VoucherRedemptionHelper voucherRedemptionHelper = new VoucherRedemptionHelper();


	private Format moneyFormat = FormatUtils.chooseMoneyFormat(Money.ZERO);

	private State state = State.INIT;

	private boolean illegalModel = false;

	private boolean illegalState = false;


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
		moneyFormat = FormatUtils.chooseMoneyFormat(result);
		return result;
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

		for (CourseClass cc : model.getClasses()) {
				model.addEnrolment(createEnrolment(cc, model.getPayer().getStudent()));
		}

		for (Product product : model.getProducts()) {
			model.addProductItem(createProductItem(model.getPayer(), product));
		}

		state = State.ACTIVE;
	}

	public boolean validateState(Action action)
	{
		switch (action) {
			case INIT:
				return state == State.INIT;
			case CHANGE_PAYER:
			case SET_VOUCHER_PRICE:
			case ADD_STUDENT:
			case ENABLE_ENROLMENT:
			case DISABLE_ENROLMENT:
			case ENABLE_PRODUCT_ITEM:
			case DISABLE_PRODUCT_ITEM:
			case ADD_CONCESSION:
			case REMOVE_CONCESSION:
			case ADD_DISCOUNT:
			case ADD_VOUCHER:
			case PROCEED_TO_PAYMENT:
				return state == State.ACTIVE;
			default:
				throw new IllegalArgumentException();
		}
	}

	public boolean validate(ActionParameter param)
	{
		switch (param.action) {
			case INIT:
				if (model.getPayer() == null)
					return false;
				if (model.getClasses().size() < 1 && model.getProducts().size() < 1)
					return false;
				for (CourseClass cc : model.getClasses()) {
					if (cc.isCancelled() || !cc.isHasAvailableEnrolmentPlaces()) {
						return false;
					}
				}
				break;
			case CHANGE_PAYER:
				Contact contact = param.getValue(Contact.class);
				return model.getContacts().contains(contact);
			case SET_VOUCHER_PRICE:
				break;
			case ADD_STUDENT:
				contact = param.getValue(Contact.class);
				if (model.getContacts().contains(contact))
					return false;
				break;
			case ENABLE_ENROLMENT:
				Enrolment enrolment = param.getValue(Enrolment.class);
				if (model.isEnrolmentEnabled(enrolment))
					return false;
				break;
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
				//todo we should adjust this code to exclude refilling parameter;
				String discountCode = param.getValue(String.class);
				Discount discount = discountService.getByCode(discountCode);
				param.setValue(discount);
				return discount != null;
			case ADD_VOUCHER:
				//todo we should adjust this code to exclude refilling parameter;
				String voucherCode = param.getValue(String.class);
				Voucher voucher = voucherService.getVoucherByCode(voucherCode);
				param.setValue(voucher);
				return voucher != null && voucher.canBeUsedBy(model.getPayer());
			case PROCEED_TO_PAYMENT:
				break;
			default:
				throw new IllegalArgumentException();
		}
		return true;
	}

	/**
	 * Single entry point to perform all actions. {@link Action} and {@link ActionParameter} values should be specified.
	 *
	 * @param param
	 */
	public void performAction(ActionParameter param) {

		illegalState = false;
		illegalModel = false;
		if (!validateState(param.action))
		{
			illegalState = true;
			return;
		}

		if (!validate(param))
		{
			illegalModel = true;
			return;
		}

		switch (param.action) {
			case INIT:
				init();
				break;
			case CHANGE_PAYER:
				changePayer(param.getValue(Contact.class));
				break;
			case ADD_STUDENT:
				addContact(param.getValue(Contact.class));
				break;
			case ENABLE_ENROLMENT:
				enableEnrolment(param.getValue(Enrolment.class));
				break;
			case DISABLE_ENROLMENT:
				disableEnrolment(param.getValue(Enrolment.class));
				break;
			case ENABLE_PRODUCT_ITEM:
				enableProduct(param.getValue(ProductItem.class));
				break;
			case DISABLE_PRODUCT_ITEM:
				disableProduct(param.getValue(ProductItem.class));
				break;
			case REMOVE_CONCESSION:
				concessionRemoved(param.getValue(Contact.class), param.getValue(ConcessionType.class));
				break;
			case ADD_CONCESSION:
				concessionAdded(param.getValue(StudentConcession.class));
				break;
			case ADD_DISCOUNT:
				addDiscount(param.getValue(Discount.class));
				break;
			case ADD_VOUCHER:
				addVoucher(param.getValue(Voucher.class));
				break;
			case PROCEED_TO_PAYMENT:
				state = State.FINALIZED;
				break;
			default:
				throw new IllegalArgumentException("Invalid action.");
		}
	}

	public PurchaseModel getModel() {
		return model;
	}

	private void changePayer(Contact contact) {

		Contact oldPayer = model.getPayer();
		model.setPayer(contact);

		for (ProductItem item : model.getEnabledProductItems(oldPayer)) {
			Product product = item.getProduct();
			model.removeProductItem(item);
			model.addProductItem(createProductItem(contact, product));
		}


	}

	private void addContact(Contact contact) {
		model.addContact(contact);
		for (CourseClass cc : model.getClasses()) {
			model.addEnrolment(createEnrolment(cc, contact.getStudent()));
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

	private void enableProduct(ProductItem product) {
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

		if (!enrolment.isDuplicated() && courseClass.isHasAvailableEnrolmentPlaces() && !courseClass.hasEnded()) {
			InvoiceLine invoiceLine = invoiceProcessingService.createInvoiceLineForEnrolment(enrolment, model.getDiscounts());
			invoiceLine.setInvoice(model.getInvoice());

			enrolment.setInvoiceLine(invoiceLine);
		}
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

	static enum State {
		INIT, ACTIVE, FINALIZED, ERROR_EMPTY_LIST;
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
		ADD_STUDENT(Contact.class),
		ENABLE_ENROLMENT(Enrolment.class),
		DISABLE_ENROLMENT(Enrolment.class),
		ENABLE_PRODUCT_ITEM(ProductItem.class),
		DISABLE_PRODUCT_ITEM(ProductItem.class),
		ADD_CONCESSION(StudentConcession.class),
		REMOVE_CONCESSION(ConcessionType.class, Contact.class),
		ADD_DISCOUNT(String.class,Discount.class),
		ADD_VOUCHER(String.class,Voucher.class),
		PROCEED_TO_PAYMENT();

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

			throw new IllegalArgumentException("Value type doesn't match action type.");
		}

		public <T> T getValue(Class<T> valueType) {
			for (Class<?> clazz : values.keySet()) {
				if (valueType.isAssignableFrom(clazz)) {
					return (T) values.get(clazz);
				}
			}

			throw new IllegalArgumentException("Requested value type doesn't match existing value.");
		}
	}

}
