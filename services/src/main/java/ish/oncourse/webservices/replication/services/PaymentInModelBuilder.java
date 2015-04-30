/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.services;

import ish.common.types.PaymentSource;
import ish.common.types.PaymentType;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.utils.PaymentInUtil;
import ish.oncourse.webservices.util.GenericParameterEntry;
import ish.oncourse.webservices.util.GenericParametersMap;
import ish.oncourse.webservices.util.GenericReplicatedRecord;
import ish.oncourse.webservices.util.StubUtils;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class PaymentInModelBuilder {
	
	private GenericParametersMap parametersMap;
	private ObjectContext context;
	private List<GenericReplicatedRecord> replicatedRecords;
	private String sessionId;

	public PaymentInModel build() throws Exception {
		validate();
		PaymentInModel model;

		if (parametersMap != null) {
			model = buildFromParametersMap();
		} else {
			model = buildFromReplicatedRecords();
		}

		for (PaymentIn voucherPayment :	PaymentInUtil.getRelatedVoucherPayments(model.getPaymentIn())) {
			model.getRelatedVoucherPayments().add(voucherPayment);
		}
		
		return model;
	}

	private PaymentInModel buildFromReplicatedRecords() throws Exception {
		
		final PaymentInModel model = new PaymentInModel();
		for (GenericReplicatedRecord r : replicatedRecords) {
			if (ReplicationUtils.getEntityName(Enrolment.class).equalsIgnoreCase(r.getStub().getEntityIdentifier())) {
				Enrolment enrolment = (Enrolment) Cayenne.objectForPK(context, ReplicationUtils.getEntityName(Enrolment.class),r.getStub().getWillowId());
				model.getEnrolments().add(enrolment);
			} else if (ReplicationUtils.getEntityName(PaymentIn.class).equalsIgnoreCase(r.getStub().getEntityIdentifier())) {
				PaymentIn p = (PaymentIn) Cayenne.objectForPK(context, ReplicationUtils.getEntityName(PaymentIn.class),r.getStub().getWillowId());
				if (p == null) {
					throw new Exception(String.format(
							"The paymentIn record with angelId:%s willowId: %s wasn't saved during the payment group processing.", r.getStub()
									.getAngelId(),r.getStub().getWillowId()));
				}
				// ignore voucher payments here since further processing is based on money payment
				if (!PaymentType.VOUCHER.equals(p.getType())) {
					p.setSessionId(sessionId);
					model.setPaymentIn(p);
				}
			}
		}
		
		SortedSet<Invoice> invoices = new TreeSet<>(new Comparator<Invoice>() {
			public int compare(Invoice o1, Invoice o2) {
				return (model.getPaymentIn().getSource() == PaymentSource.SOURCE_ONCOURSE) ? o2.getInvoiceNumber().compareTo(o1.getInvoiceNumber()) : o2.getId()
						.compareTo(o1.getId());
			}
		});

		for (PaymentInLine line : model.getPaymentIn().getPaymentInLines()) {
			Invoice invoice = line.getInvoice();
			invoices.add(invoice);
		}
		invoices.first().setSessionId(sessionId);
		model.getInvoices().add(invoices.first());
		
		return model;
	}

	private PaymentInModel buildFromParametersMap() throws Exception {
		
		PaymentInModel model = new PaymentInModel();
		
		Long paymentInId = null;
		List<Long> enrolmentsIds = new ArrayList<>();
		List<Long> invoicesIds = new ArrayList<>();

		for (GenericParameterEntry parameter : parametersMap.getGenericEntry()) {
			if (parameter.getName().startsWith(ReplicationUtils.getEntityName(PaymentIn.class))) {
				paymentInId = Long.valueOf(parameter.getValue());
			} else if (parameter.getName().startsWith(ReplicationUtils.getEntityName(Enrolment.class))) {
				enrolmentsIds.add(Long.valueOf(parameter.getValue()));
			} else if (parameter.getName().startsWith(ReplicationUtils.getEntityName(Invoice.class))) {
				invoicesIds.add(Long.valueOf(parameter.getValue()));
			}
		}

		for (GenericReplicatedRecord r : replicatedRecords) {
			if (ReplicationUtils.getEntityName(Invoice.class).equalsIgnoreCase(r.getStub().getEntityIdentifier()) &&
					invoicesIds.contains(r.getStub().getAngelId())) {
				Invoice invoice =  (Invoice) Cayenne.objectForPK(context, ReplicationUtils.getEntityName(Invoice.class),r.getStub().getWillowId());
				invoice.setSessionId(sessionId);
				model.getInvoices().add(invoice);
			} else if (ReplicationUtils.getEntityName(Enrolment.class).equalsIgnoreCase(r.getStub().getEntityIdentifier()) &&
					enrolmentsIds.contains(r.getStub().getAngelId())) {
				Enrolment enrolment =  (Enrolment) Cayenne.objectForPK(context, ReplicationUtils.getEntityName(Enrolment.class),r.getStub().getWillowId());
				model.getEnrolments().add(enrolment);
			} else if (ReplicationUtils.getEntityName(PaymentIn.class).equalsIgnoreCase(r.getStub().getEntityIdentifier()) &&
					r.getStub().getAngelId().equals(paymentInId)) {
				PaymentIn paymentIn =  (PaymentIn) Cayenne.objectForPK(context, ReplicationUtils.getEntityName(PaymentIn.class),r.getStub().getWillowId());

				if (paymentIn == null) {
					throw new Exception(String.format(
							"The paymentIn record with angelId:%s willowId: %s wasn't saved during the payment group processing.", r.getStub()
									.getAngelId(), r.getStub().getWillowId()));
				}
				
				paymentIn.setSessionId(sessionId);
				model.setPaymentIn(paymentIn);
			}
		}

		
		return model;
	}

	private void validate() throws Exception {
		// check if group from angel is empty
		if (replicatedRecords.isEmpty()) {
			throw new Exception("Got an empty paymentIn transaction group from angel.");
		}

		// check if records were saved successfully
		if (!StubUtils.hasSuccessStatus(replicatedRecords.get(0))) {
			// records wasn't saved, immediately return to angel.
			throw new RuntimeException(String.format("Willow was unable to save paymentIn transaction group. ReplicationRecord error: %s", replicatedRecords.get(0).getMessage()));
		}
	}

	public void setParametersMap(GenericParametersMap parametersMap) {
		this.parametersMap = parametersMap;
	}

	public void setContext(ObjectContext context) {
		this.context = context;
	}

	public void setReplicatedRecords(List<GenericReplicatedRecord> replicatedRecords) {
		this.replicatedRecords = replicatedRecords;
	}
	
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
