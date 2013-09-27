package ish.oncourse.enrol.services.invoice;

import java.util.List;

import ish.oncourse.model.*;

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
	 * @param actualPromotions the actual promotions list
	 * @return invoiceLine object created
	 */
	InvoiceLine createInvoiceLineForEnrolment(Enrolment enrolment, List<Discount> actualPromotions);
	
	/**
	 * Creates the {@link InvoiceLine} object for the given voucher.
	 * 
	 * @param voucher
	 * @param payer
	 * @return invoiceLine object created
	 */
	InvoiceLine createInvoiceLineForVoucher(Voucher voucher, Contact payer);
	
	/**
	 * Sets the discounted values to the given invoiceLine for the given enrolment.
	 * @param enrolment
	 * @param invoiceLine
	 * @param actualPromotions 
	 */
	void setupDiscounts(Enrolment enrolment, InvoiceLine invoiceLine, List<Discount> actualPromotions);

    /**
     *
     * @param membership
     * @param payer
     * @return
     */
    InvoiceLine createInvoiceLineForMembership(Membership membership, Contact payer);
}
