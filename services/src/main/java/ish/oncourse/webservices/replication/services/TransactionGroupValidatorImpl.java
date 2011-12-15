package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.InvoiceLineDiscount;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.webservices.replication.builders.ITransactionStubBuilder;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;
import ish.oncourse.webservices.v4.stubs.replication.TransactionGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.cayenne.Cayenne;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

public class TransactionGroupValidatorImpl implements ITransactionGroupValidator {
	
	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(TransactionGroupValidatorImpl.class);

	@Inject
	private ITransactionStubBuilder transactionBuilder;

	@Inject
	private ICayenneService cayenneService;

	@Override
	public List<TransactionGroup> validateAndReturnFixedGroups(List<TransactionGroup> groups) {

		List<TransactionGroup> list = new ArrayList<TransactionGroup>();

		for (TransactionGroup group : new ArrayList<TransactionGroup>(groups)) {

			if (!group.getAttendanceOrBinaryDataOrBinaryInfo().isEmpty()) {

				try {
					Set<ReplicationStub> existingStubs = new HashSet<ReplicationStub>();
					existingStubs.addAll(group.getAttendanceOrBinaryDataOrBinaryInfo());

					for (ReplicationStub stub : new ArrayList<ReplicationStub>(group.getAttendanceOrBinaryDataOrBinaryInfo())) {

						Invoice invoice = null;

						if ("PaymentIn".equalsIgnoreCase(stub.getEntityIdentifier())) {
							PaymentIn p = Cayenne.objectForPK(cayenneService.sharedContext(), PaymentIn.class, stub.getWillowId());
							existingStubs.addAll(transactionBuilder.createPaymentInTransaction(Collections.singletonList(p)));
						} else if ("PaymentInLine".equalsIgnoreCase(stub.getEntityIdentifier())) {
							PaymentInLine pLine = Cayenne.objectForPK(cayenneService.sharedContext(), PaymentInLine.class,
									stub.getWillowId());
							existingStubs.addAll(transactionBuilder.createPaymentInTransaction(Collections.singletonList(pLine
									.getPaymentIn())));
						} else if ("Enrolment".equalsIgnoreCase(stub.getEntityIdentifier())) {
							Enrolment enrol = Cayenne.objectForPK(cayenneService.sharedContext(), Enrolment.class, stub.getWillowId());
							if (enrol.getInvoiceLine() != null && !enrol.getInvoiceLine().getInvoice().getPaymentInLines().isEmpty()) {
								for (PaymentInLine line : enrol.getInvoiceLine().getInvoice().getPaymentInLines()) {
									existingStubs.addAll(transactionBuilder.createPaymentInTransaction(Collections.singletonList(line
											.getPaymentIn())));
								}
							}
						} else if ("Invoice".equalsIgnoreCase(stub.getEntityIdentifier())) {
							invoice = Cayenne.objectForPK(cayenneService.sharedContext(), Invoice.class, stub.getWillowId());
						} else if ("InvoiceLine".equalsIgnoreCase(stub.getEntityIdentifier())) {
							InvoiceLine line = Cayenne.objectForPK(cayenneService.sharedContext(), InvoiceLine.class, stub.getWillowId());
							invoice = line.getInvoice();
						} else if ("InvoiceLineDiscount".equalsIgnoreCase(stub.getEntityIdentifier())) {
							InvoiceLineDiscount lineDiscount = Cayenne.objectForPK(cayenneService.sharedContext(),
									InvoiceLineDiscount.class, stub.getWillowId());
							invoice = lineDiscount.getInvoiceLine().getInvoice();
						}

						if (invoice != null && !invoice.getPaymentInLines().isEmpty()) {
							for (PaymentInLine line : invoice.getPaymentInLines()) {
								existingStubs.addAll(transactionBuilder.createPaymentInTransaction(Collections.singletonList(line
										.getPaymentIn())));
							}
						}
					}
					
					list.add(group);
					
				} catch (Exception se) {
					logger.error("Unable to validate transaction group. This group will be skipped.", se);
				}
			}
		}

		return list;
	}
}
