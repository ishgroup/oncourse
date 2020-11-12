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

package ish.oncourse.server.lifecycle;

import ish.common.types.AttendanceType;
import ish.common.types.EnrolmentStatus;
import ish.oncourse.types.FundingStatus;
import ish.common.types.OutcomeStatus;
import ish.oncourse.function.CalculateOutcomeReportableHours;
import ish.oncourse.server.cayenne.Attendance;
import ish.oncourse.server.cayenne.Enrolment;
import ish.oncourse.server.cayenne.Module;
import ish.oncourse.server.cayenne.Outcome;
import org.apache.cayenne.ObjectContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Iterator;

public class UpdateAttendancesAndOutcomes {

    private static final Logger logger = LogManager.getLogger();

    private ObjectContext context;
    private Enrolment enrolment;
    private boolean create;

    public UpdateAttendancesAndOutcomes(ObjectContext context, Enrolment enrolment, boolean create) {
        this.context = context;
        this.enrolment = context.localObject(enrolment);
        this.create = create;

    }

    private void deleteOutcomes() {
        Iterator<Outcome> outcomeIterator = enrolment.getOutcomes().listIterator();
        while(outcomeIterator.hasNext()) {
            var outcome = outcomeIterator.next();
            if(outcome.getCertificateOutcomes().isEmpty() &&
                    outcome.getFundingUploadOutcomes().stream()
                            .noneMatch(fuo -> Arrays.asList(FundingStatus.EXPORTED, FundingStatus.SUCCESS)
                                    .contains(fuo.getFundingUpload().getStatus()))) {
                outcomeIterator.remove();
                context.deleteObject(outcome);
            }
        }
    }

    /**
     * upon triggering checks the consistency of Outcomes and Attendances, removes them in case of changing the enrolment to one of the failed statuses etc.
     */
    public void update() {

        if (EnrolmentStatus.STATUSES_FAILED.contains(enrolment.getStatus()) || EnrolmentStatus.STATUSES_CANCELLATIONS.contains(enrolment.getStatus())) {

            // check if student has another successfull enrolment in the class
            var successfullEnrolmentDetected = false;
            for (var e : enrolment.getStudent().getEnrolments()) {
                successfullEnrolmentDetected = successfullEnrolmentDetected || e.getCourseClass().equals(enrolment.getCourseClass()) &&
                        !(EnrolmentStatus.STATUSES_FAILED.contains(e.getStatus()) || EnrolmentStatus.STATUSES_CANCELLATIONS.contains(e.getStatus()));
            }
            if (!successfullEnrolmentDetected) {
                for (var session : enrolment.getCourseClass().getSessions()) {
                    var a = enrolment.getAttendanceForSessionAndStudent(session, enrolment.getStudent());
                    if (a != null && AttendanceType.UNMARKED.equals(a.getAttendanceType())) {
                        context.deleteObjects(a);
                    }
                }

                // do not delete outcomes here if user perform cancel or refund manually (through oncourse UI)
                // it was done on client side if user selected such option
                if (EnrolmentStatus.STATUSES_FAILED.contains(enrolment.getStatus())) {
                    // there should be no outcomes yet (during pre-persist), but just to be sure
                    deleteOutcomes();
                }
            }
        } else {
            // make sure there is attendance records for this student.
            for (var session : enrolment.getCourseClass().getSessions()) {
                if (enrolment.getAttendanceForSessionAndStudent(session, enrolment.getStudent()) == null) {
                    var a = context.newObject(Attendance.class);
                    a.setSession(session);
                    a.setStudent(enrolment.getStudent());
                    a.setAttendanceType(AttendanceType.UNMARKED);
                }
            }
            if (create) {
                // this is the only time the outcomes are created, on the prePersist trigger, for enrolments which might become successfull
                createOutcomes();
            } else {
                // do not create outcomes, they should retain from the time of record creation
                // (atm there is no guarantee that the enrolment will change status FAILED -> SUCCESS, which might cause missing outcomes)
            }
        }
    }

    private void createOutcomes() {
        if (enrolment.getCourseClass() == null) {
            logger.error("This enrolment has no class but the outcomes are being created.", new Exception());
        } else if (enrolment.getCourseClass().getCourse() == null) {
            logger.error("This class has no course but the outcomes are being created.", new Exception());
        } else if (!enrolment.getCourseModules().isEmpty()) {
            // we are looking at enrolment for assessable course
            // make sure the non-assessable outcome is removed.
            var o = getOutcomeForModuleAndEnrolment(null, false);
            // enrolment.getOutcomeForModuleAndEnrolment(null, false);
            if (o != null) {
                context.deleteObjects(o);
            }
            for (var module : enrolment.getCourseModules()) {
                getOutcomeForModuleAndEnrolment(module, true);
                // enrolment.getOutcomeForModuleAndEnrolment(module, true);
            }
        } else {
            logger.debug("({}) it is an enrolment for a non assessable course", hashCode());
            for (var o : enrolment.getOutcomes()) {
                if (o.getModule() != null) {
                    context.deleteObjects(o);
                }
            }
            getOutcomeForModuleAndEnrolment(null, true);
            // enrolment.getOutcomeForModuleAndEnrolment(null, true);
        }

    }

    public Outcome getOutcomeForModuleAndEnrolment(Module module, boolean create) {
        // only add outcome if it has not been created yet
        for (var o : enrolment.getOutcomes()) {
            var outcomeModule = o.getModule();
            if ((outcomeModule == null && module == null) || (outcomeModule != null && outcomeModule.equals(module))) {
                return o;
            }
        }
        if (create) {
            var outcome = context.newObject(Outcome.class);
            outcome.setModule(module);
            outcome.setEnrolment(enrolment);
            outcome.setStatus(OutcomeStatus.STATUS_NOT_SET);
            outcome.setFundingSource(enrolment.getFundingSource());
            outcome.setVetFundingSourceStateID(enrolment.getVetFundingSourceStateID());
            outcome.setVetPurchasingContractID(enrolment.getVetPurchasingContractID());
            outcome.setVetPurchasingContractScheduleID(enrolment.getCourseClass().getVetPurchasingContractScheduleID());
            outcome.setDeliveryMode(enrolment.getCourseClass().getDeliveryMode());
            outcome.setReportableHours(CalculateOutcomeReportableHours.valueOf(outcome).calculate());
            return outcome;
        }
        return null;
    }




}
