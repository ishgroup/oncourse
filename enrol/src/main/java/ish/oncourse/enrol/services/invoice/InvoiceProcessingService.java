/**
 * 
 */
package ish.oncourse.enrol.services.invoice;

import ish.math.Money;
import ish.oncourse.model.College;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.Student;
import ish.oncourse.services.discount.IDiscountService;

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

	@Inject
	private IDiscountService discountService;

	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.enrol.services.invoice.IInvoiceProcessingService#createInvoiceLineForEnrolment(ish.oncourse.model.Enrolment)
	 */
	public InvoiceLine createInvoiceLineForEnrolment(Enrolment enrolment) {
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

		setupDiscounts(enrolment, invoiceLine);

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
	public void setupDiscounts(Enrolment enrolment, InvoiceLine invoiceLine) {
		CourseClass courseClass = enrolment.getCourseClass();
		Money fee = courseClass.getFeeExGst();
		List<Discount> enrolmentDiscounts = discountService.getEnrolmentDiscounts(enrolment);

		invoiceLine.setDiscountEachExTax(discountService.discountValueForList(enrolmentDiscounts,
				fee));

		invoiceLine.setTaxEach(discountService.discountedValueForList(enrolmentDiscounts,
				courseClass.getFeeGst()));
	}

}
