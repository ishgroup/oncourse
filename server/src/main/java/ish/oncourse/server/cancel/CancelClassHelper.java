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

import ish.cancel.CancelationResult;
import ish.common.types.EnrolmentStatus;
import ish.common.types.SystemEventType;
import ish.math.Money;
import ish.oncourse.common.SystemEvent;
import ish.oncourse.server.cayenne.CourseClass;
import ish.oncourse.server.cayenne.CourseClassNoteRelation;
import ish.oncourse.server.cayenne.Enrolment;
import ish.oncourse.server.cayenne.SystemUser;
import ish.oncourse.server.integration.EventService;
import ish.util.NoteUtil;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.validation.ValidationException;
import org.apache.cayenne.validation.ValidationResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CancelClassHelper {

    private static final Logger logger = LogManager.getLogger();


    private EventService eventService;
    private CancelEnrolmentService cancelEnrolmentService;
    private ObjectContext context;
    private CourseClass courseCLass;
    private SystemUser systemUser;

    private boolean refundManualInvoices;
    private boolean sendEmail;


    private ValidationResult validationResult = new ValidationResult();
    private List<Enrolment> processedEnrolments = new ArrayList<>();
    private CancelationResult cancelationResult = new CancelationResult();

    public CancelationResult cancel() {

        cancelEnrolments();

        if (refundManualInvoices) {
            refundManualInvoices();
        }

        courseCLass.setIsCancelled(true);

        createCancellationNote();

        if (validate() && commit()) {
            postEvents();
        }
        return cancelationResult;
    }

    private boolean validate() {
        courseCLass.validateForSave(validationResult);
        if (validationResult.hasFailures()) {
            validationResult.getFailures().forEach(cancelationResult::addFailure);
            return false;
        }
        return true;
    }

    private boolean commit() {
        try{
            context.commitChanges();
        } catch (ValidationException ve) {
            logger.error(ve);
            context.rollbackChanges();
            ve.getValidationResult().getFailures()
                    .forEach(cancelationResult::addFailure);
            cancelationResult.setFailed(true);
            return false;
        } catch (Exception e) {
            logger.error("An exception (not a validation error) was thrown when trying to duplicate a class.", e);
            context.rollbackChanges();
            cancelationResult.setFailed(true);
            return false;
        }
        return true;
    }

    private void postEvents() {
        try {
            eventService.postEvent(SystemEvent.valueOf(SystemEventType.CLASS_CANCELLED, courseCLass));
        } catch (Exception e) {
            logger.error("Unable to post class cancellation event.", e);
        }

        processedEnrolments.forEach(enrolment -> {
            try {
                eventService.postEvent(SystemEvent.valueOf(SystemEventType.ENROLMENT_CANCELLED, enrolment));
            } catch (Exception e) {
                logger.error("Unable to post enrolment cancellation event.", e);
            }
        });
    }

    private void cancelEnrolments() {
        for (var enrolment : courseCLass.getEnrolments()) {
            if (cancelEnrolment(enrolment)) {
                processedEnrolments.add(enrolment);
            }
        }
    }

    private boolean cancelEnrolment(Enrolment enrolment) {
        if (!EnrolmentStatus.SUCCESS.equals(enrolment.getStatus())) {
            return false;
        } else if (enrolment.getInvoiceLines().isEmpty()) {
            return false;
        }

        List<RefundInvoiceParam> linesToRefund = new ArrayList<>();

        for (var invoiceLine : enrolment.getInvoiceLines()) {
            linesToRefund.add(
                    new RefundInvoiceParam(invoiceLine,
                        courseCLass.getIncomeAccount(),
                        courseCLass.getIncomeAccount().getTax(),
                        Money.ZERO(),
                            sendEmail
                    )
            );
        }

        var result = cancelEnrolmentService.processEnrolmentCancel(linesToRefund, context, enrolment, true, false );
        if (result.hasFailures()) {
            result.getFailures().forEach(validationResult::addFailure);
            return false;
        }
        return true;
    }

    private void refundManualInvoices() {
        for (var invoiceLine : new ArrayList<>(courseCLass.getInvoiceLines())) {
            RefundHelper.createRefundInvoice(invoiceLine,
                    courseCLass.getIncomeAccount(),
                    courseCLass.getIncomeAccount().getTax(),
                    Money.ZERO(),
                    Boolean.FALSE,
                    validationResult,
                    "Invoice line", false, false, systemUser);
        }
    }

    private void createCancellationNote() {
        var uniqueCode = courseCLass.getUniqueCode();
        var dateToCancel = LocalDate.now().format(DateTimeFormatter.ofPattern("d/M/yyyy"));
        var newNote = NoteUtil.createNewNote(courseCLass, CourseClassNoteRelation.class);
        newNote.setInteractionDate(new Date());
        newNote.setNote(String.format("class : %s\n cancelled : %s", uniqueCode, dateToCancel));
    }

    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

    public void setCancelEnrolmentService(CancelEnrolmentService cancelEnrolmentService) {
        this.cancelEnrolmentService = cancelEnrolmentService;
    }

    public void setContext(ObjectContext context) {
        this.context = context;
    }

    public void setCourseCLass(CourseClass courseCLass) {
        this.courseCLass = courseCLass;
    }

    public void setRefundManualInvoices(boolean refundManualInvoices) {
        this.refundManualInvoices = refundManualInvoices;
    }

    public void setSendEmail(boolean sendEmail) {
        this.sendEmail = sendEmail;
    }

    public void setSystemUser(SystemUser systemUser) {
        this.systemUser = systemUser;
    }
}
