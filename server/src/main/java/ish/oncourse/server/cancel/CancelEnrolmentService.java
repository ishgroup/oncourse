/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cancel;

import com.google.inject.Inject;
import ish.cancel.CancelationResult;
import ish.cancel.EnrolmentCancelationRequest;
import ish.common.types.EnrolmentStatus;
import ish.common.types.SystemEventType;
import ish.math.Money;
import ish.oncourse.common.SystemEvent;
import ish.oncourse.server.cayenne.Account;
import ish.oncourse.server.cayenne.Enrolment;
import ish.oncourse.server.cayenne.Invoice;
import ish.oncourse.server.cayenne.InvoiceLine;
import ish.oncourse.server.cayenne.Tax;
import ish.oncourse.server.integration.EventService;
import ish.oncourse.server.users.SystemUserService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectById;
import org.apache.cayenne.validation.ValidationException;
import org.apache.cayenne.validation.ValidationResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


public class CancelEnrolmentService {

    private static final Logger logger = LogManager.getLogger();

    @Inject
    private EventService eventService;

    @Inject
    private SystemUserService systemUserService;

    public CancelationResult cancelEnrolment(EnrolmentCancelationRequest enrolmentCancelationRequest,
                                             Boolean deleteNotSetOutcomes, Enrolment cancelEnrolment ) {
        ObjectContext context = cancelEnrolment.getContext();

        List<RefundInvoiceParam> linesToRefund = new ArrayList<>();

        for (var lineToRefund : enrolmentCancelationRequest.getLinesToRefund()) {
            InvoiceLine invoiceLineToRefund = SelectById.query(InvoiceLine.class, lineToRefund.getInvoiceLineId()).selectOne(context);
            if (lineToRefund.getCancellationFee() != null) {
                Account accountToRefund = SelectById.query(Account.class, lineToRefund.getAccountId()).selectOne(context);
                Tax taxToRefund = SelectById.query(Tax.class, lineToRefund.getTaxId()).selectOne(context);
                Money cancellationFee = Money.of(lineToRefund.getCancellationFee());
                linesToRefund.add(new RefundInvoiceParam(invoiceLineToRefund, accountToRefund, taxToRefund, cancellationFee, lineToRefund.getSendInvoice()));
            } else {
                linesToRefund.add(new RefundInvoiceParam(invoiceLineToRefund, null, null, Money.ZERO(), lineToRefund.getSendInvoice()));
            }
        }

        var result = processEnrolmentCancel(linesToRefund, context, cancelEnrolment, deleteNotSetOutcomes, enrolmentCancelationRequest.getTransfer());

        if (!result.isFailed() && commit(result, context)) {
            postEvents(cancelEnrolment);
        }

        return result;
    }

    public CancelationResult processEnrolmentCancel(List<RefundInvoiceParam> linesToRefund,
                                                    ObjectContext context,
                                                    Enrolment cancelEnrolment,
                                                    Boolean deleteNotSetOutcomes,
                                                    Boolean transfer) {

        CancelationResult result = new CancelationResult();
        ValidationResult validationResult = new ValidationResult();

        if (linesToRefund.isEmpty()) {
            cancelEnrolment.setStatus(EnrolmentStatus.CANCELLED);
        } else {
            cancelEnrolment.setStatus(EnrolmentStatus.REFUNDED);

            for (RefundInvoiceParam lineToRefund: linesToRefund) {

                RefundHelper.createRefundInvoice(lineToRefund.getLineToRefund(),
                        lineToRefund.getAccount(),
                        lineToRefund.getTax(),
                        lineToRefund.getCancellationFee(),
                        lineToRefund.getSendInvoice(),
                        validationResult,
                        "enrolment", transfer, true, systemUserService.getCurrentUser())
                        .getInvoiceLines().get(0).setEnrolment(cancelEnrolment);
            }
        }

        if (validate(result, cancelEnrolment, validationResult)) {
            if (deleteNotSetOutcomes) {
                DeleteNotSetOutcomes.deleteOutcomes(cancelEnrolment, context);
            }
        }

        return result;
    }

    private boolean validate(CancelationResult cancelationResult, Enrolment cancelEnrolment, ValidationResult validationResult) {
        cancelEnrolment.validateForSave(validationResult);
        var contact = cancelEnrolment.getStudent().getContact();
        if (contact != null) {
            contact.validateForSave(validationResult);
        }
        if (validationResult.hasFailures()) {
            cancelationResult.setFailed(true);
            validationResult.getFailures().forEach(cancelationResult::addFailure);
            return false;
        }
        return true;
    }

    private boolean commit(CancelationResult result, ObjectContext context) {
        try{
            context.commitChanges();
        } catch (ValidationException ve) {
            logger.error(ve);
            context.rollbackChanges();
            ve.getValidationResult().getFailures().forEach(result::addFailure);
            result.setFailed(true);
            return false;
        } catch (Exception e) {
            logger.error("An exception (not a validation error) was thrown when trying to duplicate a enrolment.", e);
            context.rollbackChanges();
            result.setFailed(true);
            return false;
        }
        return true;
    }

    private void postEvents(Enrolment cancelEnrolment) {
        try {
            eventService.postEvent(SystemEvent.valueOf(SystemEventType.ENROLMENT_CANCELLED, cancelEnrolment));
        } catch (Exception e) {
            logger.error("Unable to post enrolment cancellation event.", e);
        }
    }

}
