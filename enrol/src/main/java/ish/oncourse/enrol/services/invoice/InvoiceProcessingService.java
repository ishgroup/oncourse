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

/**
 * Implementation of {@link IInvoiceProcessingService}.
 *
 * @author ksenia
 */
public class InvoiceProcessingService implements IInvoiceProcessingService {


	/**
	 * InvoiceLine title format:
	 * <Student FirstName> <Student LastName> <CourseCode>-<CourseClassCode> <CourseName>
	 */
	public static final String INVOICE_LINE_TITLE_TEMPALTE = "%s %s %s-%s %s";
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
	 * @see ish.oncourse.enrol.services.invoice.IInvoiceProcessingService#createInvoiceLineForEnrolment(Enrolment)
	 */
	public InvoiceLine createInvoiceLineForEnrolment(Enrolment enrolment) {
		ObjectContext context = enrolment.getObjectContext();
		InvoiceLine invoiceLine = context.newObject(InvoiceLine.class);

		CourseClass courseClass = enrolment.getCourseClass();
		Course course = courseClass.getCourse();
		Student student = enrolment.getStudent();
		Contact contact = student.getContact();
		College college = enrolment.getCollege();

		invoiceLine.setTitle(String.format(INVOICE_LINE_TITLE_TEMPALTE,
				contact.getGivenName(),
				contact.getFamilyName(),
				course.getCode(),
				courseClass.getCode(),
				course.getName()));
		invoiceLine.setDescription(courseClass.getUniqueIdentifier() + " "
				+ courseClass.getCourse().getName());

		invoiceLine.setQuantity(BigDecimal.ONE);
		Money fee = courseClass.getFeeExGst();
		invoiceLine.setCollege(college);

		Application application = applicationService.findOfferedApplicationBy(course, student);
		if (application != null && application.getFeeOverride() != null) {
			//Calculate enrolment fee (for enrolments whose courses has ENROLMENT_BY_APPLICATION type) as application.feeOverride if !=null.
			//Application.feeOverride doesn't need to combine with discounts.
			InvoiceUtil.fillInvoiceLine(invoiceLine, application.getFeeOverride(), Money.ZERO, courseClass.getTaxRate(), Money.ZERO);
		} else {
			invoiceLine.setPriceEachExTax(fee);
			InvoiceUtil.fillInvoiceLine(invoiceLine, invoiceLine.getPriceEachExTax(), Money.ZERO,
						courseClass.getTaxRate(), calculateTaxAdjustment(courseClass));
		}
		return invoiceLine;
	}

	@Override
	public InvoiceLine createInvoiceLineForVoucher(Voucher voucher, Contact contact) {
		return this.createInvoiceLineForVoucher(voucher, contact, voucher.getVoucherProduct().getPriceExTax());
	}

	@Override
	public InvoiceLine createInvoiceLineForVoucher(Voucher voucher, Contact contact, Money priceExTax) {
		return createInvoiceLineForProductItem(voucher, contact, priceExTax);

	}

	private InvoiceLine createInvoiceLineForProductItem(ProductItem productItem, Contact contact, Money priceExTax) {
		ObjectContext context = productItem.getObjectContext();

		Product product = productItem.getProduct();
		College college = productItem.getCollege();

		InvoiceLine invoiceLine = context.newObject(InvoiceLine.class);

		invoiceLine.setDescription(String.format("%s (%s %s)",
				contact.getFullName(), productItem.getProduct().getSku(), productItem.getProduct().getName()));

		invoiceLine.setTitle(String.format("%s %s", contact.getFullName(), product.getName()));

		invoiceLine.setQuantity(BigDecimal.ONE);
		InvoiceUtil.fillInvoiceLine(invoiceLine, priceExTax, Money.ZERO, product.getTaxRate(), product.getTaxAdjustment());
		productItem.setInvoiceLine(invoiceLine);

		invoiceLine.setCollege(college);

		return invoiceLine;
	}

	@Override
	public InvoiceLine createInvoiceLineForMembership(Membership membership, Contact contact) {
		return createInvoiceLineForProductItem(membership, contact, membership.getProduct().getPriceExTax());
	}

	@Override
	public InvoiceLine createInvoiceLineForArticle(Article article, Contact contact) {
		return createInvoiceLineForProductItem(article, contact, article.getProduct().getPriceExTax());
	}

	private Money calculateTaxAdjustment(final CourseClass courseClass) {
		return courseClass.getFeeIncGst().subtract(courseClass.getFeeExGst().multiply(courseClass.getTaxRate().add(BigDecimal.ONE)));
	}
}
