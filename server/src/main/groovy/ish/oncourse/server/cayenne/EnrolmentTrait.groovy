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

package ish.oncourse.server.cayenne

import ish.common.types.AttendanceType
import ish.common.types.EnrolmentStatus
import ish.math.Money
import ish.messaging.IAttendance
import ish.oncourse.API
import ish.oncourse.entity.services.EnrolmentService
import static ish.oncourse.server.entity.mixins.MixinHelper.getService

import javax.annotation.Nonnull

trait EnrolmentTrait {

    public static final String STATUS_COMPLETE = 'Complete'

    abstract EnrolmentStatus getStatus()

    abstract CourseClass getCourseClass()

    abstract Student getStudent()
    
    abstract List<InvoiceLine> getInvoiceLines()

    /**
     * @return display status name
     */
    @API
    String getDisplayStatus() {
        if (EnrolmentStatus.SUCCESS == getStatus() && getCourseClass().endDateTime != null && getCourseClass().endDateTime.before(new Date()) ) {
            return STATUS_COMPLETE
        } else {
            getStatus() ? getStatus().displayName : null
        }
    }

    /**
     * @return total amount of all related invoices
     */
    @API
    Money getFeeCharged() {
        if (invoiceLines?.empty)  {
            return Money.ZERO
        } else {
            return invoiceLines*.priceTotalIncTax.inject { a, b -> a.add(b) }
        }
    }
    


    /**
     * @return related attendance list
     */
    @API
    List<Attendance> getAttendances() {
        List<Attendance> attendances = []
        if (! (status in EnrolmentStatus.STATUSES_LEGIT) || courseClass.sessions.empty) {
            return attendances
        }

        for (Session session :  courseClass.sessions) {
            for (Attendance a : session.attendance) {
                if (a.student.equals(student)) {
                    attendances.add(a)
                    break
                }
            }
        }
        return attendances
    }

    /**
     * Will return null if the class is self paced
     * @return percent Attendance
     */
    @API
    Integer getAttendancePercent() {
        double minutesPassed = 0d
        double minutesPresent = 0d

        Date now = new Date()
        for (Attendance a : attendances) {
            if (a.getSession().getEndDatetime().before(now)) {
                if (a.getAttendanceType() != null && !AttendanceType.UNMARKED.equals(a.getAttendanceType())) {
                    double sessionDuration = a.getSession().getDurationInMinutes().doubleValue();
                    minutesPassed += sessionDuration;
                    if (AttendanceType.ATTENDED.equals(a.getAttendanceType()) || AttendanceType.DID_NOT_ATTEND_WITH_REASON.equals(a.getAttendanceType())) {
                        minutesPresent += sessionDuration;
                    } else if (AttendanceType.PARTIAL.equals(a.getAttendanceType())) {
                        Integer partialDuration = a.getDurationMinutes();
                        if (partialDuration != null) {
                            minutesPresent += Math.min(sessionDuration, partialDuration.doubleValue());
                        }
                    }
                }
            }
        }

        return (int) (100 * minutesPresent / minutesPassed);

    }
}
