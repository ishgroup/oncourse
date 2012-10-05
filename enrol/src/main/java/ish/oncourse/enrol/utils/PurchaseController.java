package ish.oncourse.enrol.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.math.Money;
import ish.oncourse.enrol.services.invoice.IInvoiceProcessingService;
import ish.oncourse.model.College;
import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.Product;
import ish.oncourse.model.ProductItem;
import ish.oncourse.model.Student;
import ish.oncourse.model.Voucher;
import ish.oncourse.model.VoucherProduct;
import ish.oncourse.services.discount.IDiscountService;
import ish.oncourse.services.voucher.IVoucherService;
import ish.oncourse.services.voucher.VoucherRedemptionHelper;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.ObjectContext;

/**
 * Controller class for purchase page in enrol.
 * 
 * @author dzmitry
 */
public class PurchaseController {
	
	private ObjectContext context;
	private PurchaseModel model;
	
	private College college;
	
	private IInvoiceProcessingService invoiceProcessingService;
	private IDiscountService discountService;
	private IVoucherService voucherService;
	
	private VoucherRedemptionHelper voucherRedemptionHelper;
	
	private List<CourseClass> classesToEnrol;
	private List<Discount> discounts;
	private List<Product> products;
	
	private State state;
	
	public PurchaseController(IInvoiceProcessingService invoiceProcessingService, IDiscountService discountService, 
			IVoucherService voucherService, ObjectContext context, College college, Contact contact, List<CourseClass> classes, 
			List<Discount> discounts, List<Product> products) {
		this.context = context;
		this.college = (College) localizeObject(context, college);
		this.classesToEnrol = new ArrayList<CourseClass>(classes);
		this.discounts = new ArrayList<Discount>(discounts);
		this.products = new ArrayList<Product>(products);
		
		this.invoiceProcessingService = invoiceProcessingService;
		this.discountService = discountService;
		this.voucherService = voucherService;
		
		this.voucherRedemptionHelper = new VoucherRedemptionHelper();
		
		state = State.INIT;
		init((Contact) localizeObject(context, contact));
	}
	
	private CayenneDataObject localizeObject(ObjectContext context, CayenneDataObject objectForLocalize) {
		if (objectForLocalize.getObjectContext().equals(context)) {
			return objectForLocalize;
		} else {
			return (CayenneDataObject) context.localObject(objectForLocalize.getObjectId(), null);
		}
	}
	
	private void init(Contact contact) {
		if (state != State.INIT) {
			throw new IllegalStateException("Controller already initialized.");
		}
		if (contact == null) {
			throw new IllegalArgumentException("Contact cannot be null.");
		}
		
		this.model = new PurchaseModel();
		
		initPayment();
		voucherRedemptionHelper.setInvoice(model.getInvoice());

		model.addContact(contact);
		model.setPayer(contact);
		
		for (CourseClass cc : classesToEnrol) {
			if (!cc.isCancelled() || cc.isHasAvailableEnrolmentPlaces()) {
				model.addEnrolment(createEnrolment(cc, contact.getStudent(), discounts));
			}
		}
		
		for (Product product : products) {
			model.addProduct(createProduct(contact, product));
		}
		
		state = State.ACTIVE;
	}
	
	/**
	 * Single entry point to perform all actions. {@link Action} and {@link ActionParameter} values should be specified.
	 * 
	 * @param action
	 * @param param
	 */
	public void performAction(ActionParameter param) {
		if (state != State.ACTIVE) {
			throw new IllegalArgumentException("Controller is " + ((state == State.INIT) ? "not ready yet." : "already finalized."));
		}
		
		switch (param.action) {
		case CHANGE_PAYER:
			changePayer(param.getValue(Contact.class));
			break;
		case ADD_STUDENT:
			addStudent(param.getValue(Contact.class));
			break;
		case ENABLE_ENROLMENT:
			enableEnrolment(param.getValue(Enrolment.class));
			break;
		case DISABLE_ENROLMENT:
			disableEnrolment(param.getValue(Enrolment.class));
			break;
		case ENABLE_PRODUCT:
			enableProduct(param.getValue(ProductItem.class));
			break;
		case DISABLE_PRODUCT:
			disableProduct(param.getValue(ProductItem.class));
			break;
		case ADD_CONCESSION:
			concessionAdded();
			break;
		case ADD_PROMOCODE:
			addPromoCode(param.getValue(String.class));
			break;
		case ADD_VOUCHER_CODE:
			addVoucherCode(param.getValue(String.class));
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
		if (model.getContacts().contains(contact)) {
			
			List<ProductItem> newProductItems = new ArrayList<ProductItem>();
			
			for (ProductItem item : model.getEnabledProducts(model.getPayer())) {
				Product product = item.getProduct();
				removeProductItem(item);
				newProductItems.add(createProduct(contact, product));
			}
			
			model.setPayer(contact);
			
			for (ProductItem item : newProductItems) {
				model.addProduct(item);
			}
		}
	}
	
	private void addStudent(Contact contact) {
		model.addContact(contact);
		for (CourseClass cc : classesToEnrol) {
			model.addEnrolment(createEnrolment(cc, contact.getStudent(), discounts));
		}
	}
	
	private void enableEnrolment(Enrolment enrolment) {
		model.enableEnrolment(enrolment);
		if (enrolment.getInvoiceLine() == null) {
			InvoiceLine il = invoiceProcessingService.createInvoiceLineForEnrolment(enrolment, discounts);
			il.setInvoice(model.getInvoice());
			enrolment.setInvoiceLine(il);
		}
	}
	
	private void disableEnrolment(Enrolment enrolment) {
		model.disableEnrolment(enrolment);
		
		if (enrolment.getInvoiceLine() != null) {
			InvoiceLine il = enrolment.getInvoiceLine();
			enrolment.setInvoiceLine(null);
			context.deleteObject(il);
		}
	}
	
	private void enableProduct(ProductItem product) {
		model.enableProduct(product);
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
	}
	
	private void disableProduct(ProductItem product) {
		model.disableProduct(product);
		
		if (product instanceof Voucher) {
			Voucher voucher = (Voucher) product;
			InvoiceLine il = voucher.getInvoiceLine();
			voucher.setInvoiceLine(null);
			context.deleteObject(il);
		} else {
			throw new IllegalArgumentException("Unsupported product type.");
		}
	}
	
	private void concessionAdded() {
		recalculateEnrolmentInvoiceLines();
	}
	
	private void addPromoCode(String promocode) {
		Discount discount = discountService.getByCode(promocode);
		if (discount != null) {
			this.discounts.add(discount);
		}
		recalculateEnrolmentInvoiceLines();
	}
	
	private void addVoucherCode(String code) {
		Voucher voucher = voucherService.getVoucherByCode(code);
		if (voucher.canBeUsedBy(model.getPayer())) {
			voucherRedemptionHelper.addVoucher(voucher);
			voucherRedemptionHelper.calculateVouchersRedeemPayment();
			
			model.clearVoucherPayments();
			model.addVoucherPayments(voucherRedemptionHelper.getPayments());
		}
	}
	
	private void recalculateEnrolmentInvoiceLines() {
		
		for (Contact contact : model.getContacts()) {
			for (Enrolment enrolment : model.getEnabledEnrolments(contact)) {
				InvoiceLine oldInvoiceLine = enrolment.getInvoiceLine();
				enrolment.setInvoiceLine(null);
				context.deleteObject(oldInvoiceLine);
				
				InvoiceLine newInvoiceLine = invoiceProcessingService.createInvoiceLineForEnrolment(enrolment, discounts);
				newInvoiceLine.setInvoice(model.getInvoice());
				enrolment.setInvoiceLine(newInvoiceLine);
			}
		}
	}
	
	private void removeProductItem(ProductItem item) {
		InvoiceLine invoiceLineToDelete = item.getInvoiceLine();
		
		context.deleteObject(item);
		context.deleteObject(invoiceLineToDelete);
		
		model.removeProduct(item);
	}
	
    /**
     * Creates and initializes the set of payment/enrolment-related entities:
     * <ul>
     * <li> {@link PaymentIn} - one payment entity is created for all the
     * selected courses and contacts</li>
     * <li> {@link Invoice} - one invoice entity is created for all the selected
     * courses and contacts</li>
     * <li> {@link Enrolment} - separate enrolment entity is created for the each
     * element of the cartesian product of the selected courses and contacts</li>
     * <li> {@link InvoiceLine} - the new entity is created for each enrolment if
     * the class is enrolable, linked to the invoice entity.</li>
     * </ul>
     */
    private void initPayment() {
        // College college = (College) context.localObject(currentCollege.getObjectId(), currentCollege);

    	PaymentIn payment;
    	
	    payment = context.newObject(PaymentIn.class);
	    payment.setStatus(PaymentStatus.NEW);
	    payment.setSource(PaymentSource.SOURCE_WEB);
	    payment.setCollege(college);
	    
	    Invoice invoice;
	
	    invoice = context.newObject(Invoice.class);
	    // fill the invoice with default values
	    invoice.setInvoiceDate(new Date());
	    invoice.setAmountOwing(BigDecimal.ZERO);
	    invoice.setDateDue(new Date());
	    invoice.setSource(PaymentSource.SOURCE_WEB);
	    invoice.setCollege(college);
	    
	    model.setInvoice(invoice);
	    model.setPayment(payment);
    }
	
    /**
     * Creates the new {@link Enrolment} entity for the given courseClass and
     * Student.
     *
     * @param courseClass
     * @param student
     * @return
     */
    private Enrolment createEnrolment(CourseClass courseClass, Student student, List<Discount> discounts) {
        Enrolment enrolment = context.newObject(Enrolment.class);
        enrolment.setStatus(EnrolmentStatus.IN_TRANSACTION);
        enrolment.setSource(PaymentSource.SOURCE_WEB);

        enrolment.setCollege(student.getCollege());
        enrolment.setStudent(student);
        enrolment.setCourseClass(courseClass);

        if (!enrolment.isDuplicated() && courseClass.isHasAvailableEnrolmentPlaces() && !courseClass.hasEnded()) {
            InvoiceLine invoiceLine = invoiceProcessingService.createInvoiceLineForEnrolment(enrolment, discounts);
            invoiceLine.setInvoice(model.getInvoice());

            enrolment.setInvoiceLine(invoiceLine);
        }
        return enrolment;
    }
    
    private ProductItem createProduct(Contact contact, Product product) {
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
    
	private static enum State {
		INIT, ACTIVE, FINALIZED
	}
	
	/**
	 * Enumeration of all actions controller can perform.
	 * 
	 * @author dzmitry
	 */
	public static enum Action {
		CHANGE_PAYER(Contact.class),
		SET_VOUCHER_PRICE(Money.class),
		ADD_STUDENT(Contact.class),
		ENABLE_ENROLMENT(Enrolment.class),
		DISABLE_ENROLMENT(Enrolment.class),
		ENABLE_PRODUCT(ProductItem.class),
		DISABLE_PRODUCT(ProductItem.class),
		ADD_CONCESSION(),
		REMOVE_CONCESSION(ConcessionType.class, Contact.class),
		ADD_PROMOCODE(String.class),
		ADD_VOUCHER_CODE(String.class),
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
