/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.College;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.util.payment.PaymentInModel;
import ish.oncourse.utils.PaymentInUtil;
import ish.oncourse.webservices.util.GenericParameterEntry;
import ish.oncourse.webservices.util.GenericParametersMap;
import ish.oncourse.webservices.util.GenericReplicatedRecord;
import ish.oncourse.webservices.util.StubUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;

import java.util.List;

public class PaymentInModelFromParametersMapBuilder {

    private ObjectContext context;
    private College college;

    private List<GenericReplicatedRecord> replicatedRecords;
    private GenericParametersMap parametersMap;

    private PaymentInModel model = new PaymentInModel();

    private PaymentInModelFromParametersMapBuilder() {
    }

    public PaymentInModelFromParametersMapBuilder build() throws Exception {
        validate();
        for (GenericParameterEntry parameter : parametersMap.getGenericEntry()) {
            if (parameter.getName().startsWith(PaymentIn.class.getSimpleName())) {
                model.setPaymentIn(ObjectSelect.query(PaymentIn.class)
                        .where(PaymentIn.ANGEL_ID.eq(Long.valueOf(parameter.getValue()))).and(PaymentIn.COLLEGE.eq(college))
                        .selectOne(context));
            } else if (parameter.getName().startsWith(Enrolment.class.getSimpleName())) {
                model.getEnrolments().add(ObjectSelect.query(Enrolment.class)
                        .where(PaymentIn.ANGEL_ID.eq(Long.valueOf(parameter.getValue()))).and(PaymentIn.COLLEGE.eq(college))
                        .selectOne(context));
            } else if (parameter.getName().startsWith(Invoice.class.getSimpleName())) {
                model.getInvoices().add(ObjectSelect.query(Invoice.class)
                        .where(Invoice.ANGEL_ID.eq(Long.valueOf(parameter.getValue()))).and(PaymentIn.COLLEGE.eq(college))
                        .selectOne(context));
            }
        }
        initVoucherPayments();
        return this;
    }

    public PaymentInModel getModel() {
        return model;
    }


    private void initVoucherPayments() {
        for (PaymentIn voucherPayment : PaymentInUtil.getRelatedVoucherPayments(model.getPaymentIn())) {
            model.getVoucherPayments().add(voucherPayment);
        }
    }


    private void validate() throws Exception {
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

    public static PaymentInModelFromParametersMapBuilder valueOf(GenericParametersMap parametersMap,
                                                                 List<GenericReplicatedRecord> replicatedRecords,
                                                                 College college, ObjectContext context) {
        PaymentInModelFromParametersMapBuilder builder = new PaymentInModelFromParametersMapBuilder();
        builder.parametersMap = parametersMap;
        builder.replicatedRecords = replicatedRecords;
        builder.college = context.localObject(college);
        builder.context = context;
        return builder;
    }
}
