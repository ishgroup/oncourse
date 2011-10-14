package ish.oncourse.webservices.replication.builders;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.InvoiceLineDiscount;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.model.Queueable;
import ish.oncourse.model.Room;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.inject.Inject;

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
	 * @param paymentIn paymentIn object
	 * @return soap stub
	 */
	public Set<ReplicationStub> createPaymentInTransaction(List<PaymentIn> payments) {

		Set<ReplicationStub> paymentRelated = new HashSet<ReplicationStub>(20);

		for (PaymentIn paymentIn : payments) {
			
			addRelatedStub(paymentRelated, paymentIn);

			for (PaymentInLine paymentLine : paymentIn.getPaymentInLines()) {

				addRelatedStub(paymentRelated, paymentLine);

				Invoice invoice = paymentLine.getInvoice();
				addRelatedStub(paymentRelated, invoice);

				for (InvoiceLine invoiceLine : invoice.getInvoiceLines()) {

					addRelatedStub(paymentRelated, invoiceLine);

					Enrolment enrol = invoiceLine.getEnrolment();

					if (enrol != null) {
						addRelatedStub(paymentRelated, enrol);

						CourseClass courseClass = enrol.getCourseClass();
						addRelatedStub(paymentRelated, courseClass);
						addRelatedStub(paymentRelated, courseClass.getCourse());

						Room room = courseClass.getRoom();
						addRelatedStub(paymentRelated, room);

						if (room != null) {
							addRelatedStub(paymentRelated, room.getSite());
						}

						addRelatedStub(paymentRelated, enrol.getStudent());
						addRelatedStub(paymentRelated, enrol.getStudent().getContact());
						
						if (enrol.getStudent().getContact() != null) {
							addRelatedStub(paymentRelated, enrol.getStudent().getContact().getTutor());
						}
					}

					for (InvoiceLineDiscount lineDiscount : invoiceLine.getInvoiceLineDiscounts()) {
						addRelatedStub(paymentRelated, lineDiscount);
						addRelatedStub(paymentRelated, lineDiscount.getDiscount());
					}
				}
			}

			addRelatedStub(paymentRelated, paymentIn.getContact());
			
			if (paymentIn.getContact() != null) {
				addRelatedStub(paymentRelated, paymentIn.getContact().getStudent());
				addRelatedStub(paymentRelated, paymentIn.getContact().getTutor());
			}
		}
		
		return paymentRelated;
	}

	public Set<ReplicationStub> createRefundTransaction(PaymentOut paymentOut) {

		Set<ReplicationStub> paymentOutRelated = new HashSet<ReplicationStub>(20);
		addRelatedStub(paymentOutRelated, paymentOut);
		addRelatedStub(paymentOutRelated, paymentOut.getContact());
		addRelatedStub(paymentOutRelated, paymentOut.getContact().getStudent());
		addRelatedStub(paymentOutRelated, paymentOut.getContact().getTutor());

		return paymentOutRelated;
	}

	/**
	 * Updates list with replication stub, if only it wasn't replicated to willow.
	 * 
	 * @param enrlRelated
	 * @param relatedEntity
	 */
	private void addRelatedStub(Set<ReplicationStub> enrlRelated, Queueable relatedEntity) {
		if (relatedEntity != null) {
			enrlRelated.add(builder.convert(relatedEntity));
		}
	}
}
