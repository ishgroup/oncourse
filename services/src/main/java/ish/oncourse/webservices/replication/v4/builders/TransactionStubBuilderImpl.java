package ish.oncourse.webservices.replication.v4.builders;

import ish.oncourse.model.*;
import ish.oncourse.webservices.replication.services.SupportedVersions;
import ish.oncourse.webservices.util.GenericReplicationStub;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.inject.Inject;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.query.ObjectIdQuery;

/**
 * @author anton
 */
public class TransactionStubBuilderImpl implements ITransactionStubBuilder {

	private final IWillowStubBuilder builder;

	@Inject
	public TransactionStubBuilderImpl(IWillowStubBuilder builder) {
		super();
		this.builder = builder;
	}

	/**
	 * Convert paymentIn object to soap stub
	 * 
	 * @param payments paymentIn objects
	 * @return soap stub
	 */
	public Set<GenericReplicationStub> createPaymentInTransaction(List<PaymentIn> payments, final SupportedVersions version) {

		Set<GenericReplicationStub> paymentRelated = new LinkedHashSet<GenericReplicationStub>(20);

		for (PaymentIn paymentIn : payments) {
			
			addRelatedStub(paymentRelated, paymentIn, version);
			for (VoucherPaymentIn voucherPaymentIn : paymentIn.getVoucherPaymentIns()) {
				if (voucherPaymentIn.getInvoiceLine() == null) {
					//money voucher
					addRelatedStub(paymentRelated, voucherPaymentIn, version);
					addRelatedStub(paymentRelated, voucherPaymentIn.getVoucher(), version);
				}
			}

			for (PaymentInLine paymentLine : paymentIn.getPaymentInLines()) {

				addRelatedStub(paymentRelated, paymentLine, version);

				Invoice invoice = paymentLine.getInvoice();
				addRelatedStub(paymentRelated, invoice, version);

				for (InvoiceLine invoiceLine : invoice.getInvoiceLines()) {

					for (ProductItem item : invoiceLine.getProductItems()) {
						addRelatedStub(paymentRelated, item, version);
					}

					for (VoucherPaymentIn vp : invoiceLine.getVoucherPaymentsIn()) {
						addRelatedStub(paymentRelated, vp, version);
						addRelatedStub(paymentRelated, vp.getVoucher(), version);
					}

					addRelatedStub(paymentRelated, invoiceLine, version);

					Enrolment enrol = invoiceLine.getEnrolment();

					if (enrol != null) {
						addRelatedStub(paymentRelated, enrol, version);

						CourseClass courseClass = enrol.getCourseClass();
						addRelatedStub(paymentRelated, courseClass, version);
						addRelatedStub(paymentRelated, courseClass.getCourse(), version);

						Room room = courseClass.getRoom();
						addRelatedStub(paymentRelated, room, version);

						if (room != null) {
							addRelatedStub(paymentRelated, room.getSite(), version);
						}

						addRelatedStub(paymentRelated, enrol.getStudent(), version);
						addRelatedStub(paymentRelated, enrol.getStudent().getContact(), version);
						
						if (enrol.getStudent().getContact() != null) {
							addRelatedStub(paymentRelated, enrol.getStudent().getContact().getTutor(), version);
						}
					}

					for (InvoiceLineDiscount lineDiscount : invoiceLine.getInvoiceLineDiscounts()) {
						addRelatedStub(paymentRelated, lineDiscount, version);
						addRelatedStub(paymentRelated, lineDiscount.getDiscount(), version);
					}
				}
			}

			addRelatedStub(paymentRelated, paymentIn.getContact(), version);
			
			if (paymentIn.getContact() != null) {
				addRelatedStub(paymentRelated, paymentIn.getContact().getStudent(), version);
				addRelatedStub(paymentRelated, paymentIn.getContact().getTutor(), version);
			}
		}
		
		return paymentRelated;
	}

	public Set<GenericReplicationStub> createRefundTransaction(PaymentOut paymentOut, final SupportedVersions version) {

		Set<GenericReplicationStub> paymentOutRelated = new LinkedHashSet<GenericReplicationStub>(20);
		addRelatedStub(paymentOutRelated, paymentOut, version);
		addRelatedStub(paymentOutRelated, paymentOut.getContact(), version);
		addRelatedStub(paymentOutRelated, paymentOut.getContact().getStudent(), version);
		addRelatedStub(paymentOutRelated, paymentOut.getContact().getTutor(), version);

		return paymentOutRelated;
	}

	/**
	 * Updates list with replication stub, if only it wasn't replicated to willow.
	 * 
	 * @param enrlRelated
	 * @param relatedEntity
	 */
	private void addRelatedStub(Set<GenericReplicationStub> enrlRelated, Queueable relatedEntity, final SupportedVersions version) {
		if (relatedEntity != null) {
			enrlRelated.add(builder.convert(relatedEntity, version));
		}
	}
}
