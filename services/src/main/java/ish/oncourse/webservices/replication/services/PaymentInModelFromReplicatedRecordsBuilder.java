package ish.oncourse.webservices.replication.services;

import ish.common.types.PaymentSource;
import ish.common.types.PaymentType;
import ish.oncourse.model.*;
import ish.oncourse.util.payment.PaymentInModel;
import ish.oncourse.utils.PaymentInUtil;
import ish.oncourse.webservices.util.GenericReplicatedRecord;
import ish.oncourse.webservices.util.StubUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;

import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class PaymentInModelFromReplicatedRecordsBuilder {

    private List<GenericReplicatedRecord> replicatedRecords;
    private ObjectContext objectContext;

    private PaymentInModel model = new PaymentInModel();


    private <T extends Queueable> T getEntity(Class<T> entityCalss, GenericReplicatedRecord r) {
        if (entityCalss.getSimpleName().equalsIgnoreCase(r.getStub().getEntityIdentifier())) {
            T result = ObjectSelect.query(entityCalss).where(ExpressionFactory.matchDbExp("id", r.getStub().getWillowId())).selectOne(objectContext);
            if (result != null) {
                return result;
            }
            else {
                throw new IllegalArgumentException(String.format("Cannot find entity %s with willowId %s, angelId %s", r.getStub().getEntityIdentifier(), r.getStub().getWillowId(), r.getStub().getAngelId()));
            }
        }
        return null;
    }

    private void initPaymentIn() {
        for (GenericReplicatedRecord r : replicatedRecords) {
            PaymentIn paymentIn = getEntity(PaymentIn.class, r);
            if (paymentIn != null && !PaymentType.VOUCHER.equals(paymentIn.getType())) {
                model.setPaymentIn(paymentIn);
                break;
            }
        }

        if (model.getPaymentIn() == null) {
            throw new IllegalArgumentException("Cannot find paymentIn");
        }
    }

    public PaymentInModelFromReplicatedRecordsBuilder build() {
        validate();
        initPaymentIn();
        initInvoices();
        initEnrolments();
        initVoucherPayments();

        return this;
    }

    private void initVoucherPayments() {
        for (PaymentIn voucherPayment :	PaymentInUtil.getRelatedVoucherPayments(model.getPaymentIn())) {
            model.getVoucherPayments().add(voucherPayment);
        }
    }

    private void validate() {
        // check if group from angel is empty
        if (replicatedRecords.isEmpty()) {
            throw new IllegalArgumentException("Got an empty paymentIn transaction group from angel.");
        }

        // check if records were saved successfully
        if (!StubUtils.hasSuccessStatus(replicatedRecords.get(0))) {
            // records wasn't saved, immediately return to angel.
            throw new IllegalArgumentException(String.format("Willow was unable to save paymentIn transaction group. ReplicationRecord error: %s", replicatedRecords.get(0).getMessage()));
        }
    }

    public PaymentInModel getModel() {
        return model;
    }

    private void initEnrolments() {
        for (InvoiceLine invoiceLine : model.getInvoices().get(0).getInvoiceLines()) {
            if (invoiceLine.getEnrolment() != null && !invoiceLine.getEnrolment().isInFinalStatus()) {
                model.getEnrolments().add(invoiceLine.getEnrolment());
            }
        }
    }

    private Invoice initInvoices() {
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

        Invoice invoice = invoices.first();
        model.getInvoices().add(invoice);
        return invoice;
    }


    public static PaymentInModelFromReplicatedRecordsBuilder valueOf(List<GenericReplicatedRecord> replicatedRecords, ObjectContext objectContext) {
        PaymentInModelFromReplicatedRecordsBuilder builder = new PaymentInModelFromReplicatedRecordsBuilder();
        builder.replicatedRecords = replicatedRecords;
        builder.objectContext = objectContext;
        return builder;
    }
}
