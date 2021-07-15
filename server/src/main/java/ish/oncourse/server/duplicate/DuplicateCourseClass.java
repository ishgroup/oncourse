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

package ish.oncourse.server.duplicate;

import ish.common.types.AttendanceType;
import ish.common.types.ClassCostFlowType;
import ish.duplicate.ClassDuplicationRequest;
import ish.math.Money;
import ish.oncourse.entity.services.CourseClassService;
import ish.oncourse.server.api.dao.CourseClassDao;
import ish.oncourse.server.api.v1.model.ClassCostDTO;
import ish.oncourse.server.cayenne.Account;
import ish.oncourse.server.cayenne.AssessmentClass;
import ish.oncourse.server.cayenne.AssessmentClassModule;
import ish.oncourse.server.cayenne.AssessmentClassTutor;
import ish.oncourse.server.cayenne.ClassCost;
import ish.oncourse.server.cayenne.Course;
import ish.oncourse.server.cayenne.CourseClass;
import ish.oncourse.server.cayenne.CourseClassNoteRelation;
import ish.oncourse.server.cayenne.CourseClassPaymentPlanLine;
import ish.oncourse.server.cayenne.CourseClassTutor;
import ish.oncourse.server.cayenne.Discount;
import ish.oncourse.server.cayenne.DiscountCourseClass;
import ish.oncourse.server.cayenne.Module;
import ish.oncourse.server.cayenne.Note;
import ish.oncourse.server.cayenne.SessionModule;
import ish.oncourse.server.cayenne.Tax;
import ish.oncourse.server.cayenne.TutorAttendance;
import ish.oncourse.server.cayenne.Session;
import ish.util.AccountUtil;
import ish.util.DateTimeUtil;
import ish.util.DiscountUtil;
import ish.util.MoneyUtil;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;

public class DuplicateCourseClass {

    private static final Logger logger = LogManager.getLogger(DuplicateCourseClass.class);


    private CourseClass oldClass;
    private ClassDuplicationRequest request;
    private CourseClassService courseClassService;
    private DataContext context;
    private CourseClassDao courseClassDao;
    private ClassCostDTO classCostDTO;

    private DuplicateCourseClass() {

    }

    public static DuplicateCourseClass valueOf(CourseClass oldClass, ClassDuplicationRequest request, CourseClassService courseClassService, DataContext context, CourseClassDao courseClassDao, ClassCostDTO classCostDto) {
        var duplicateCourseClass = new DuplicateCourseClass();
        duplicateCourseClass.oldClass = oldClass;
        duplicateCourseClass.request = request;
        duplicateCourseClass.courseClassService = courseClassService;
        duplicateCourseClass.context = context;
        duplicateCourseClass.courseClassDao = courseClassDao;
        duplicateCourseClass.classCostDTO = classCostDto;
        return duplicateCourseClass;
    }

    public CourseClass duplicate() {
        var newClass = context.newObject(CourseClass.class);


        if (oldClass.getStartDateTime() != null) {
            newClass.setStartDateTime(DateTimeUtil.addDaysDaylightSafe(oldClass.getStartDateTime(), request.getDaysTo()));
        }
        if (oldClass.getEndDateTime() != null) {
            newClass.setEndDateTime(DateTimeUtil.addDaysDaylightSafe(oldClass.getEndDateTime(), request.getDaysTo()));
        }

        newClass.setAttendanceType(oldClass.getAttendanceType());
        newClass.setIsCancelled(Boolean.FALSE);
        var isDistantLearning = oldClass.getIsDistantLearningCourse();
        newClass.setIsDistantLearningCourse(isDistantLearning);
        if (Boolean.TRUE.equals(isDistantLearning)) {
            newClass.setExpectedHours(oldClass.getExpectedHours());
            newClass.setMaximumDays(oldClass.getMaximumDays());
        }
        newClass.setIsShownOnWeb(Boolean.FALSE);
        newClass.setMaximumPlaces(oldClass.getMaximumPlaces());
        newClass.setMinimumPlaces(oldClass.getMinimumPlaces());
        newClass.setMaxStudentAge(oldClass.getMaxStudentAge());
        newClass.setMinStudentAge(oldClass.getMinStudentAge());
        newClass.setMinutesPerSession(oldClass.getMinutesPerSession());
        newClass.setSessionsSkipWeekends(oldClass.getSessionsSkipWeekends());
        newClass.setSessionRepeatType(oldClass.getSessionRepeatType());
        newClass.setSessionsCount(oldClass.getSessionsCount());
        newClass.setWebDescription(oldClass.getWebDescription());
        newClass.setReportableHours(oldClass.getReportableHours());

        Course course = request.getCourseId() != null ?
                SelectById.query(Course.class, request.getCourseId()).selectOne(context) :
                oldClass.getCourse();

        newClass.setCourse(course);
        newClass.setCode(course.getNextAvailableCode(oldClass));


        if (oldClass.getIncomeAccount() != null) {
            newClass.setIncomeAccount(oldClass.getIncomeAccount());
        }

        if (request.isCopySitesAndRooms()) {
            newClass.setRoom(oldClass.getRoom());
        }

        if (oldClass.getTax() != null) {
            newClass.setTax(oldClass.getTax());
        }
        if (request.isCopyTutors() && courseClassService.getTutors(oldClass) != null) {
            for (var cct : oldClass.getTutorRoles()) {
                var newCCT = context.newObject(CourseClassTutor.class);
                newCCT.setTutor(cct.getTutor());
                newCCT.setCourseClass(newClass);
                newCCT.setDefinedTutorRole(cct.getDefinedTutorRole());

                if (request.isCopyCosts()) {
                    cct.getClassCosts()
                            .forEach(cc -> DuplicateClassCost.valueOf(cc, context, newClass, newCCT).duplicate());
                }
            }
        }

        if (request.isCopyNotes()) {
            var noteRelations = oldClass.getNoteRelations();
            for (var relation : noteRelations) {
                var oldNote = relation.getNote();

                var newRelation = context.newObject(CourseClassNoteRelation.class);

                var note = context.newObject(Note.class);
                newRelation.setNote(note);
                newRelation.setNotableEntity(newClass);

                note.setNote(oldNote.getNote());
                note.setSystemUser(oldNote.getSystemUser());
                note.setCreatedOn(oldNote.getCreatedOn());
                if (oldNote.getChangedBy() != null) {
                    note.setChangedBy(oldNote.getChangedBy());
                }

            }
        }

        // creating new sessions
        if (oldClass.getSessions() != null && oldClass.getSessions().size() > 0) {
            for (var oldSession : oldClass.getSessions()) {
                var newSession = context.newObject(Session.class);

                if (oldSession.getStartDatetime() != null) {
                    newSession.setStartDatetime(DateTimeUtil.addDaysDaylightSafe(oldSession.getStartDatetime(), request.getDaysTo()));
                }
                if (oldSession.getEndDatetime() != null) {
                    newSession.setEndDatetime(DateTimeUtil.addDaysDaylightSafe(oldSession.getEndDatetime(), request.getDaysTo()));
                }

                if (request.isCopyPayableTimeForSessions()) {
                    newSession.setPayAdjustment(oldSession.getPayAdjustment());
                }

                if (request.isCopyTutors() && newClass.getTutorRoles() != null) {
                    // create tutor attendance records
                    for (var cct : newClass.getTutorRoles()) {
                        var tutorAttendance = context.newObject(TutorAttendance.class);
                        tutorAttendance.setSession(newSession);
                        tutorAttendance.setCourseClassTutor(cct);
                        tutorAttendance.setAttendanceType(AttendanceType.UNMARKED);
                    }
                }

                //copy training plan only in case if new class has the same course with old one
                if (request.getCourseId() == null && request.isCopyTrainingPlans()) {
                    for (var module : oldSession.getModules()) {
                        var sessionModule = context.newObject(SessionModule.class);
                        sessionModule.setSession(newSession);
                        sessionModule.setModule(module);
                    }
                }

                if (oldSession.getRoom() != null && request.isCopySitesAndRooms()) {
                    newSession.setRoom(context.localObject(oldSession.getRoom()));

                    if (oldSession.getStartDatetime() != null) {
                        newSession.setStartDatetime(adjustClientTime(oldSession.getTimeZone(), oldSession.getStartDatetime(), newSession.getStartDatetime()));
                    }

                    if (oldSession.getStartDatetime() != null) {
                        newSession.setEndDatetime(adjustClientTime(oldSession.getTimeZone(), oldSession.getEndDatetime(), newSession.getEndDatetime()));
                    }
                }

                newSession.setCourseClass(newClass);
            }
        }

        // re-linking the discounts :
        if (request.isApplyDiscounts() && !oldClass.getDiscounts().isEmpty()) {
            oldClass.getDiscountCourseClasses().stream()
                    .filter(dcc -> dcc.getDiscount().getValidTo() == null || dcc.getDiscount().getValidTo().after(new Date()))
                    .forEach(dcc -> {
                        var discount = context.localObject(dcc.getDiscount());

                        var newDcc = context.newObject(DiscountCourseClass.class);
                        newDcc.setCourseClass(newClass);
                        newDcc.setDiscount(discount);
                        newDcc.setDiscountDollar(dcc.getDiscountDollar());
                        newDcc.setPredictedStudentsPercentage(dcc.getPredictedStudentsPercentage());

                        DuplicateClassCost.valueOf(dcc.getClassCost(), context, newClass, newDcc).duplicate();
                    });
        }

        // re-linking the tags:
        oldClass.getTags().stream()
                .filter(tag -> !request.isCopyOnlyMandatoryTags() || tag.getRoot().isRequiredFor(newClass.getClass()))
                .forEach(newClass::addTag);

        // rollover of class costs
        if (request.isCopyCosts() && oldClass.getCosts() != null && oldClass.getCosts().size() > 0) {
            oldClass.getCosts().stream()
                    // skip discount class costs
                    .filter(cc -> !ClassCostFlowType.DISCOUNT.equals(cc.getFlowType()))
                    //skip tutor wages here (were copied together with tutor roles)
                    .filter(cc -> !ClassCostFlowType.WAGES.equals(cc.getFlowType()))
                    .forEach(cc -> DuplicateClassCost.valueOf(cc, context, newClass).duplicate());


            newClass.setIncomeAccount(oldClass.getIncomeAccount());
            newClass.setTax(oldClass.getTax());
            newClass.setFeeExGst(oldClass.getFeeExGst());
            newClass.setTaxAdjustment(oldClass.getTaxAdjustment());
            newClass.setDeposit(oldClass.getDeposit());

            // add same structure of payment plan
            oldClass.getPaymentPlanLines().forEach(line -> {
                CourseClassPaymentPlanLine newLine = context.newObject(CourseClassPaymentPlanLine.class);
                newLine.setDayOffset(line.getDayOffset());
                newLine.setAmount(line.getAmount());
                newLine.setCourseClass(newClass);
            });

        } else {
            courseClassDao.addStudentFeeCost(context, newClass);
            if (classCostDTO != null) {
                ClassCost classCost = newClass.getCosts().stream()
                        .filter(cc -> ClassCostFlowType.INCOME.equals(cc.getFlowType()))
                        .findFirst().get();
                classCost.setDescription(classCostDTO.getDescription());
                classCost.setPerUnitAmountExTax(Money.valueOf(classCostDTO.getPerUnitAmountExTax()));

                Tax tax = SelectById.query(Tax.class, classCostDTO.getTaxId()).selectOne(context);
                Money incTax = Money.valueOf(classCostDTO.getPerUnitAmountIncTax());
                Money exTax = Money.valueOf(classCostDTO.getPerUnitAmountExTax());
                Money taxAdjustment =  MoneyUtil.calculateTaxAdjustment(incTax, exTax, tax.getRate());
                classCost.setTax(tax);
                classCost.setTaxAdjustment(taxAdjustment);

                Account account = SelectById.query(Account.class, classCostDTO.getAccountId()).selectOne(context);
                newClass.setIncomeAccount(account);
                newClass.setTax(tax);

                classCostDTO.getPaymentPlan().forEach(line -> {
                    CourseClassPaymentPlanLine newLine = context.newObject(CourseClassPaymentPlanLine.class);
                    newLine.setDayOffset(line.getDayOffset());
                    newLine.setAmount(Money.valueOf(line.getAmount()));
                    newLine.setCourseClass(newClass);
                });
            }
        }

        DiscountUtil.assignDefaultDiscounts(newClass);

        if (request.isCopyVetData()) {
            newClass.setDeliveryMode(oldClass.getDeliveryMode());
            newClass.setSuppressAvetmissExport(oldClass.getSuppressAvetmissExport());
            newClass.setFundingSource(oldClass.getFundingSource());
            newClass.setVetFundingSourceStateID(oldClass.getVetFundingSourceStateID());
            newClass.setDetBookingId(oldClass.getDetBookingId());
            newClass.setVetCourseSiteID(oldClass.getVetCourseSiteID());
            newClass.setVetPurchasingContractID(oldClass.getVetPurchasingContractID());
            newClass.setVetPurchasingContractScheduleID(oldClass.getVetPurchasingContractScheduleID());
            newClass.setAttendanceType(oldClass.getAttendanceType());
            newClass.setCensusDate(oldClass.getCensusDate());
            newClass.setRelatedFundingSource(oldClass.getRelatedFundingSource());
        }

        if (request.isCopyAssessments() && !oldClass.getAssessmentClasses().isEmpty()) {
            for (var oldAC : oldClass.getAssessmentClasses()) {
                var newAC = context.newObject(AssessmentClass.class);
                newAC.setCourseClass(newClass);
                newAC.setAssessment(oldAC.getAssessment());
                if (oldAC.getReleaseDate() != null) {
                    newAC.setReleaseDate(DateTimeUtil.addDaysDaylightSafe(oldAC.getReleaseDate(), request.getDaysTo()));
                }

                newAC.setDueDate(DateTimeUtil.addDaysDaylightSafe(oldAC.getDueDate(), request.getDaysTo()));

                for (var oldACM : oldAC.getAssessmentClassModules()) {
                    var newACM = context.newObject(AssessmentClassModule.class);
                    newACM.setAssessmentClass(newAC);
                    newACM.setModule(oldACM.getModule());
                }

                if (request.isCopyTutors()) {
                    for (var oldACT : oldAC.getAssessmentClassTutors()) {
                        var newACT = context.newObject(AssessmentClassTutor.class);
                        newACT.setAssessmentClass(newAC);
                        newACT.setTutor(oldACT.getTutor());
                    }
                }
            }
        }

        return newClass;
    }

    private Date adjustClientTime(TimeZone tz, Date originalDate, Date newDate) {

        var originalCalendar = Calendar.getInstance(tz);
        var newCalendar = Calendar.getInstance(tz);

        originalCalendar.setTime(originalDate);
        newCalendar.setTime(newDate);
        newCalendar.set(HOUR_OF_DAY, originalCalendar.get(HOUR_OF_DAY));
        newCalendar.set(MINUTE, originalCalendar.get(MINUTE));
        return newCalendar.getTime();
    }
}
