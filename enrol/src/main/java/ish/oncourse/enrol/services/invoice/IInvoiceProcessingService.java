package ish.oncourse.enrol.services.invoice;

import ish.math.Money;
import ish.oncourse.model.*;

import java.util.List;

/**
 * The service for manipulating with invoice-related services: Invoice, InvoiceLine
 * @author ksenia
 *
 */
public interface IInvoiceProcessingService {
	/**
	 * Creates the {@link InvoiceLine} object for the given enrolment.
	 * 
	 * The rules of filling the invoiceLine prices:
	 * <br/>
	 * Assumptions: <ul>
	 * 					<li>courseClass=enrolment.courseClass;</li> 
	 * 					<li>student=enrolment.student;</li>
	 *  				<li>fee=courseClass.feeExGst;</li>
	 *  				<li>tax=courseClass.feeGst;</li>
	 *  				<li>discountRate - the discount rate of the best combination 
	 *  					of discounts applicable to courseClass for student</li> 
	 *  			</ul>
	 *  
	 * Rules:<ul>
	 * 			<li>incoiceLine.priceEachExTax=fee;</li> 
	 * 			<li>incoiceLine.discountEachExTax=fee*discountRate;</li> 
	 * 			<li>incoiceLine.taxEach=tax-tax*discountRate;
	 * 					(actually, we set the tax of discounted fee to the invoiceLine.taxEach, 
	 * 						see angel/client/ish.oncourse.cayenne.InvoiceLine#recalculateTaxEach()[189])</li>
	 * 		</ul>	
	 *   
	 * @param enrolment the given enrolment
	 * @return invoiceLine object created
	 */
	InvoiceLine createInvoiceLineForEnrolment(Enrolment enrolment, Tax taxOverride);
	
	/**
	 * Creates the {@link InvoiceLine} object for the given voucher.
	 * 
	 * @param voucher
	 * @param contact
	 * @return invoiceLine object created
	 */
	InvoiceLine createInvoiceLineForVoucher(Voucher voucher, Contact contact,  Tax taxOverride);
    InvoiceLine createInvoiceLineForVoucher(Voucher voucher, Contact contact, Money price,  Tax taxOverride);
	

    /**
     *
     * @param membership
     * @param contact
     * @return
     */
    InvoiceLine createInvoiceLineForMembership(Membership membership, Contact contact,  Tax taxOverride);

	/**
	 *
	 * @param article
	 * @param contact
	 * @return
	 */
	InvoiceLine createInvoiceLineForArticle(Article article, Contact contact,  Tax taxOverride);
}
