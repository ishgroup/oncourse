/**
 * 
 */
package ish.oncourse.enrol.services.invoice;

import java.math.BigDecimal;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.math.Money;
import ish.oncourse.model.College;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.InvoiceLineDiscount;
import ish.oncourse.model.Student;
import ish.oncourse.services.discount.IDiscountService;

/**
 * Implementation of {@link IInvoiceProcessingService}.
 * @author ksenia
 *
 */
public class InvoiceProcessingService implements IInvoiceProcessingService {
	
	@Inject
	private IDiscountService discountService;

	/**
	 * {@inheritDoc}
	 * @see ish.oncourse.enrol.services.invoice.IInvoiceProcessingService#createInvoiceLineForEnrolment(ish.oncourse.model.Enrolment)
	 */
	public InvoiceLine createInvoiceLineForEnrolment(Enrolment enrolment) {
		ObjectContext context = enrolment.getObjectContext();
		InvoiceLine invoiceLine = context.newObject(InvoiceLine.class);
		
		CourseClass courseClass = enrolment.getCourseClass();
		Student student= enrolment.getStudent();
		College college = enrolment.getCollege();
		
		invoiceLine.setTitle(student.getFullName() + " "
				+ courseClass.getCourse().getName());
		invoiceLine.setDescription(courseClass.getUniqueIdentifier() + " "
				+ courseClass.getCourse().getName());
		
		invoiceLine.setQuantity(BigDecimal.ONE);
		Money fee = courseClass.getFeeExGst();
		invoiceLine.setPriceEachExTax(fee);

		List<Discount> enrolmentDiscounts = discountService.getEnrolmentDiscounts(enrolment);
		for(Discount discount:enrolmentDiscounts){
			InvoiceLineDiscount invoiceLineDiscount = context.newObject(InvoiceLineDiscount.class);
			invoiceLineDiscount.setInvoiceLine(invoiceLine);
			invoiceLineDiscount.setDiscount(discount);
		}
		invoiceLine.setDiscountEachExTax(discountService.discountValueForList(enrolmentDiscounts, fee));
		
		invoiceLine.setTaxEach(discountService.discountedValueForList(enrolmentDiscounts, courseClass.getFeeGst()));
	
		invoiceLine.setCollege(college);
		
		return invoiceLine;
	}

	
}
