/**
 *
 */
package ish.oncourse.enrol.services.invoice;

import ish.math.Money;
import ish.oncourse.model.*;
import ish.oncourse.services.application.IApplicationService;
import ish.oncourse.services.discount.IDiscountService;
import ish.util.InvoiceUtil;
import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

/**
 * Implementation of {@link IInvoiceProcessingService}.
 *
 * @author ksenia
 */
public class InvoiceProcessingService implements IInvoiceProcessingService {
	
	private static final String DATE_FORMAT = "dd-MM-yyyy h:mm a z";
	private final IDiscountService discountService;
	
	@Inject
	private IApplicationService applicationService;
	
	@Inject
	public InvoiceProcessingService(IDiscountService discountService) {
		super();
		this.discountService = discountService;
	}

	/**
	 * @return the discountService
	 */
	public IDiscountService takeDiscountService() {
		return discountService;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see ish.oncourse.enrol.services.invoice.IInvoiceProcessingService#createInvoiceLineForEnrolment(Enrolment, Tax)
	 */
	public InvoiceLine createInvoiceLineForEnrolment(Enrolment enrolment, Tax taxOverride) {
		
		ObjectContext context = enrolment.getObjectContext();
		InvoiceLine invoiceLine = context.newObject(InvoiceLine.class);

		CourseClass courseClass = enrolment.getCourseClass();
		Course course = courseClass.getCourse();
		Student student = enrolment.getStudent();
		Contact contact = student.getContact();
		College college = enrolment.getCollege();

		invoiceLine.setTitle(String.format("%s %s", contact.getFullName(), courseClass.getUniqueIdentifier()));
		if (courseClass.getStartDateTime() == null) {
			invoiceLine.setDescription(courseClass.getCourse().getName());
		} else {
			SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
			format.setTimeZone(enrolment.getCourseClass().getClassTimeZone());
			invoiceLine.setDescription(String.format("%s starting on %s", courseClass.getCourse().getName(), format.format(enrolment.getCourseClass().getStartDateTime())));
		}

		invoiceLine.setQuantity(BigDecimal.ONE);
		Money fee = courseClass.getFeeExGst();
		invoiceLine.setCollege(college);

		Application application = applicationService.findOfferedApplicationBy(course, student);
		BigDecimal taxRate =  taxOverride == null ? courseClass.getTaxRate() : taxOverride.getRate();
		if (application != null && application.getFeeOverride() != null) {
			//Calculate enrolment fee (for enrolments whose courses has ENROLMENT_BY_APPLICATION type) as application.feeOverride if !=null.
			//Application.feeOverride doesn't need to combine with discounts.
			InvoiceUtil.fillInvoiceLine(invoiceLine, application.getFeeOverride(), Money.ZERO, taxRate, Money.ZERO);
		} else {
			invoiceLine.setPriceEachExTax(fee);
			InvoiceUtil.fillInvoiceLine(invoiceLine, invoiceLine.getPriceEachExTax(), Money.ZERO,
					taxRate, taxOverride == null ? calculateTaxAdjustment(courseClass) : Money.ZERO);
		}
		return invoiceLine;
	}

	@Override
	public InvoiceLine createInvoiceLineForVoucher(Voucher voucher, Contact contact,  Tax taxOverride) {
		return this.createInvoiceLineForVoucher(voucher, contact, voucher.getVoucherProduct().getPriceExTax(), taxOverride);
	}

	@Override
	public InvoiceLine createInvoiceLineForVoucher(Voucher voucher, Contact contact, Money priceExTax, Tax taxOverride) {
		return createInvoiceLineForProductItem(voucher, contact, priceExTax, taxOverride);

	}

	private InvoiceLine createInvoiceLineForProductItem(ProductItem productItem, Contact contact, Money priceExTax, Tax taxOverride) {
		ObjectContext context = productItem.getObjectContext();

		Product product = productItem.getProduct();
		College college = productItem.getCollege();

		InvoiceLine invoiceLine = context.newObject(InvoiceLine.class);

		invoiceLine.setDescription(String.format("%s (%s %s)",
				contact.getFullName(), productItem.getProduct().getSku(), productItem.getProduct().getName()));

		invoiceLine.setTitle(String.format("%s %s", contact.getFullName(), product.getName()));

		invoiceLine.setQuantity(BigDecimal.ONE);

		if (taxOverride == null) {
			InvoiceUtil.fillInvoiceLine(invoiceLine, priceExTax, Money.ZERO, product.getTaxRate(), product.getTaxAdjustment());
		} else {
			InvoiceUtil.fillInvoiceLine(invoiceLine, priceExTax, Money.ZERO, taxOverride.getRate(), Money.ZERO);
		}
		productItem.setInvoiceLine(invoiceLine);

		invoiceLine.setCollege(college);

		return invoiceLine;
	}

	@Override
	public InvoiceLine createInvoiceLineForMembership(Membership membership, Contact contact,  Tax taxOverride) {
		return createInvoiceLineForProductItem(membership, contact, membership.getProduct().getPriceExTax(), taxOverride);
	}

	@Override
	public InvoiceLine createInvoiceLineForArticle(Article article, Contact contact,  Tax taxOverride) {
		return createInvoiceLineForProductItem(article, contact, article.getProduct().getPriceExTax(), taxOverride);
	}

	private Money calculateTaxAdjustment(final CourseClass courseClass) {
		return courseClass.getFeeIncGst(null).subtract(courseClass.getFeeExGst().multiply(courseClass.getTaxRate().add(BigDecimal.ONE)));
	}
}
