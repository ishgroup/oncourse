/**
 * 
 */
package ish.oncourse.enrol.services.invoice;

import ish.math.Money;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.RealDiscountsPolicy;
import ish.oncourse.model.Student;
import ish.oncourse.model.Voucher;
import ish.oncourse.services.discount.IDiscountService;
import ish.util.InvoiceUtil;

import java.math.BigDecimal;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Implementation of {@link IInvoiceProcessingService}.
 * 
 * @author ksenia
 * 
 */
public class InvoiceProcessingService implements IInvoiceProcessingService {

	private final IDiscountService discountService;
	
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
	 * @see ish.oncourse.enrol.services.invoice.IInvoiceProcessingService#createInvoiceLineForEnrolment(ish.oncourse.model.Enrolment)
	 */
	public InvoiceLine createInvoiceLineForEnrolment(Enrolment enrolment, List<Discount> actualPromotions) {
		ObjectContext context = enrolment.getObjectContext();
		InvoiceLine invoiceLine = context.newObject(InvoiceLine.class);

		CourseClass courseClass = enrolment.getCourseClass();
		Student student = enrolment.getStudent();
		College college = enrolment.getCollege();

		invoiceLine.setTitle(student.getFullName() + " " + courseClass.getCourse().getName());
		invoiceLine.setDescription(courseClass.getUniqueIdentifier() + " "
				+ courseClass.getCourse().getName());

		invoiceLine.setQuantity(BigDecimal.ONE);
		Money fee = courseClass.getFeeExGst();
		invoiceLine.setPriceEachExTax(fee);

		setupDiscounts(enrolment, invoiceLine, actualPromotions);

		invoiceLine.setCollege(college);

		return invoiceLine;
	}
	
	@Override
	public InvoiceLine createInvoiceLineForVoucher(Voucher voucher, Contact payer) {
		ObjectContext context = voucher.getObjectContext();
		InvoiceLine invoiceLine = context.newObject(InvoiceLine.class);
		
		College college = voucher.getCollege();
		
		invoiceLine.setDescription(String.format("Voucher %s", voucher.getProduct().getDescription()));
        invoiceLine.setTitle(String.format("%s %s", payer.getFullName(), voucher.getProduct().getName()));
        invoiceLine.setPriceEachExTax(voucher.getRedemptionValue());
        invoiceLine.setDiscountEachExTax(Money.ZERO);
        invoiceLine.setTaxEach(Money.ZERO);
        invoiceLine.setQuantity(BigDecimal.ONE);
        voucher.setInvoiceLine(invoiceLine);
        
        invoiceLine.setCollege(college);
        
        return invoiceLine;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.enrol.services.invoice.IInvoiceProcessingService#setupDiscounts(ish.oncourse.model.Enrolment,
	 *      ish.oncourse.model.InvoiceLine)
	 */
	public void setupDiscounts(Enrolment enrolment, InvoiceLine invoiceLine, List<Discount> actualPromotions) {
		CourseClass courseClass = enrolment.getCourseClass();
		List<Discount> enrolmentDiscounts = enrolment.getCourseClass().getDiscountsToApply(
				new RealDiscountsPolicy(actualPromotions, enrolment.getStudent()));
		InvoiceUtil.fillInvoiceLine(invoiceLine, invoiceLine.getPriceEachExTax(), courseClass.getDiscountAmountExTax(enrolmentDiscounts), 
			courseClass.getTaxRate(), calculateTaxAdjustment(courseClass));
	}
	
	private Money calculateTaxAdjustment(final CourseClass courseClass) {
		return courseClass.getFeeIncGst().subtract(courseClass.getFeeExGst().multiply(courseClass.getTaxRate().add(BigDecimal.ONE)));
	}

}
