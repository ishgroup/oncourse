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
package ish.oncourse.server.payroll;

import com.google.inject.Inject;
import ish.common.types.AttendanceType;
import ish.common.types.ClassCostFlowType;
import ish.common.types.CourseClassType;
import ish.math.Money;
import ish.oncourse.entity.services.SessionService;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.*;
import ish.oncourse.server.payroll.filters.ClassCostConfirmed;
import ish.payroll.PayrollGenerationRequest;
import ish.payroll.WagesSummaryResponse;
import ish.util.EntityUtil;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ish.common.types.ClassCostFlowType.WAGES;
import static ish.common.types.ClassCostRepetitionType.PER_SESSION;
import static ish.common.types.ClassCostRepetitionType.PER_TIMETABLED_HOUR;

/**
 * Create Payslip records for passed ClassCost (wage) records from selected CourseClasses/Contacts for assigned date range.
 * Group all Paylines from all ClassCosts by Contact (ClassCost.contact) into single Payslip record.
 * Do not recreate Paylines for any ClassCost if corresponded Paylines exists.
 * Do not reassign previously generated unpaid Paylines to the newest Payslip (transfer from old Payslip) for corresponded Contact.
 * The previously generated Payslips are immutable now
 */
public class PayrollService {

    private static final Logger logger = LogManager.getLogger(PayrollService.class);

    private ICayenneService cayenneService;
    private SessionService sessionService;

    private static final String COURSE_CLASS_ENTITY_NAME = "CourseClass";
    private static final String CONTACT_ENTITY_NAME = "Contact";

    @Inject
    public PayrollService(ICayenneService cayenneService, SessionService sessionService) {
        this.cayenneService = cayenneService;
        this.sessionService = sessionService;
    }

    /**
     * Extract classCosts from requested classes and/or contacts (depended on provided request parameters),
     * filter it in accordance with assigned date range, create and save payslip record.
     *
     * @param request
     */
    public void generatePayslips(PayrollGenerationRequest request) {
        ObjectContext context = cayenneService.getNewContext();
        List<ClassCost> classCosts;

        Expression additionalFilter = null;
        if (request.getIds() != null && request.getIds().size() > 0) {
            switch (request.getEntityName()) {
                case COURSE_CLASS_ENTITY_NAME: {
                    additionalFilter = ClassCost.COURSE_CLASS.dot(CourseClass.ID).in(request.getIds());
                    break;
                }
                case CONTACT_ENTITY_NAME: {
                    additionalFilter = ClassCost.CONTACT.dot(Contact.ID).in(request.getIds());
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unsupported entity: " + request.getEntityName());
            }
        }

        classCosts = getEligibleClassCosts(context, request.getUntil(), additionalFilter);
        generatePayslipsForClassCosts(context, classCosts, request.getUntil(), request.isConfirm());

        context.commitChanges();
    }

    /**
     * Create set of paylines for each classCost (wage). If the list of payslips is not empty (payroll has no been generated yet for classCost)
     * then add all of them into single Payslip record for tutor.
     * Use Map<Contact, Payslip> container to group all paylines from all classCosts by contact.
     *
     * @param context
     * @param classCosts
     * @param until
     */
    private void generatePayslipsForClassCosts(ObjectContext context, List<ClassCost> classCosts, Date until, boolean confirm) {
        Map<Contact, Payslip> resultingPayslips = new HashMap<>();

        for (var costing : classCosts) {
            var payslipLines = createLackingPaylines(costing, until, confirm);

            if (payslipLines.size() > 0) {
                var payslip = resultingPayslips.get(costing.getContact());

                if (payslip == null) {
                    payslip = newPayslip(costing.getContact(), payslipLines, context);
                    resultingPayslips.put(costing.getContact(), payslip);
                } else {
                    for (var line : payslipLines) {
                        payslip.addToPaylines(line);
                    }
                }
            }
        }
    }

    /**
     * Create new Payslip for contacts paylines. Should been called only one time pre contact -
     * if payslip for contact is not yet exist into result container.
     *
     * @param contact
     * @param lines
     * @param context
     * @return
     */
    private Payslip newPayslip(Contact contact, Collection<PayLine> lines, ObjectContext context) {
        Payslip result = null;
        if (contact != null && lines != null && lines.size() > 0 && validateTutor(contact)) {
            result = context.newObject(Payslip.class);
            result.setContact(contact);
            result.setPayType(contact.getTutor().getPayType());
            for (var line : lines) {
                result.addToPaylines(line);
            }
        }
        return result;
    }

    /**
     * Filter input set of classCost in accordance with assigned date range.
     * Also check that corresponded class is not cancelled or classCost is sunk.
     *
     * @param context
     * @param until
     * @return
     */
    public List<ClassCost> getEligibleClassCosts(ObjectContext context, Date until, Expression additionalFilter) {
        Expression classCostsExpression = getEligibleClassCostsExpression(until);
        if(additionalFilter != null)
            classCostsExpression = classCostsExpression.andExp(additionalFilter);

        return ObjectSelect.query(ClassCost.class)
                .where(classCostsExpression)
                .prefetch(ClassCost.TUTOR_ROLE.disjoint())
                .prefetch(ClassCost.TUTOR_ROLE.dot(CourseClassTutor.SESSIONS_TUTORS).disjoint())
                .prefetch(ClassCost.TUTOR_ROLE.dot(CourseClassTutor.DEFINED_TUTOR_ROLE).disjoint())
                .prefetch(ClassCost.TUTOR_ROLE.dot(CourseClassTutor.DEFINED_TUTOR_ROLE).dot(DefinedTutorRole.PAY_RATES).disjoint())
                .prefetch(ClassCost.TUTOR_ROLE.dot(CourseClassTutor.SESSIONS_TUTORS).dot(TutorAttendance.SESSION).disjoint())
                .prefetch(ClassCost.COURSE_CLASS.disjoint())
                .prefetch(ClassCost.COURSE_CLASS.dot(CourseClass.SESSIONS).disjoint())
                .prefetch(ClassCost.PAYLINES.disjoint())
                .prefetch(ClassCost.PAYLINES.dot(PayLine.SESSION).disjoint())
                .select(context);
    }

    protected Expression getEligibleClassCostsExpression(Date until) {
        return ClassCost.FLOW_TYPE.eq(WAGES)
                .andExp(ClassCost.TUTOR_ROLE.isNotNull())
                .andExp(ClassCost.COURSE_CLASS.dot(CourseClass.START_DATE_TIME_KEY).lt(until)
                        .orExp(ClassCost.COURSE_CLASS.dot(CourseClass.TYPE).eq(CourseClassType.DISTANT_LEARNING)))
                .andExp(ClassCost.IS_SUNK.eq(true).orExp(ClassCost.COURSE_CLASS.dot(CourseClass.IS_CANCELLED).eq(false)));
    }

    /**
     * Create and return not yet created paylines for classCost. If classCost already has a full set of paylines
     * (when classCost.repetitionType == PER_SESSION and each classCost.tutorRole.tutorAttendance has corresponded payline
     * or payslip for classCost was already generated before)
     * then method will return empty list - nothing to pay, all required paylines already exist.
     * Skip generation of paylines if classCost is not a wage, if corresponded class/contact is null, class not started yet.
     * Use appropriate class-function for each repetition type of wage.
     *
     * @param classCost
     * @param until
     * @return
     */
    public List<PayLine> createLackingPaylines(ClassCost classCost, Date until, boolean confirm) {

        if (!validateClassCost(classCost, until) || !validateTutor(classCost.getContact())) {
            return Collections.EMPTY_LIST;
        }

        logger.debug("creating paylines for {}, repetition type {}", classCost.getContact().getName(), classCost.getRepetitionType());

        switch (classCost.getRepetitionType()) {
            case FIXED:
                return CreatePaylinesFixed.valueOf(classCost).createLines();
            case PER_SESSION:
                return CreatePaylinesPerSession.valueOf(classCost, until, confirm).createLines();
            case PER_ENROLMENT:
                return CreatePaylinesPerEnrolment.valueOf(classCost).createLines();
            case PER_UNIT:
                return CreatePaylinesPerUnit.valueOf(classCost).createLines();
            case PER_TIMETABLED_HOUR:
                return CreatePaylinesPerTimetabledHour.valueOf(classCost, until, sessionService, confirm).createLines();
            case PER_STUDENT_CONTACT_HOUR:
                return CreatePaylinesPerStudentContactHour.valueOf(classCost).createLines();
            default:
                throw new IllegalArgumentException();
        }
    }

    protected boolean validateClassCost(ClassCost classCost, Date until) {
        var courseClass = classCost.getCourseClass();
        if (!ClassCostFlowType.WAGES.equals(classCost.getFlowType())) {
            logger.debug("paylines irrelevant for non wage cost");
            return false;
        }

        logger.debug("update for cost: {} sunk: {} desc: {}", classCost.getFlowType(), classCost.getIsSunk(), classCost.getDescription());

        if (!isDistantCourseClass(courseClass) && (courseClass.getStartDateTime() == null || !hasClassStarted(courseClass, until))) {
            logger.debug("paylines not created: class not started or not self-placed");
            return false;
        }
        return true;
    }

    private boolean validateTutor(Contact contact) {
        if (contact.getTutor() == null || contact.getTutor().getPayType() == null) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * Return true if courseClass.startDate is not null and before current date.
     *
     * @param courseClass
     * @param until
     * @return
     */
    private boolean hasClassStarted(CourseClass courseClass, Date until) {
        if (courseClass != null) {
            var startDate = courseClass.getStartDateTime();
            var compare = until == null ? new Date() : until;
            return startDate != null && startDate.compareTo(compare) <= 0;
        }
        return false;
    }

    public WagesSummaryResponse getWagesSummary(PayrollGenerationRequest request) {
        var response = new WagesSummaryResponse();
        ObjectContext context = cayenneService.getNewContext();

        var eligibleToProcess = getCostsToProceed(request, context).stream()
                .filter(it -> isEligibleToProcess(it, request.getUntil()))
                .collect(Collectors.toList());

        response.setTotalWagesCount(eligibleToProcess.size());
        response.setUnprocessedWagesCount(eligibleToProcess.stream()
                .filter(cc -> unprocessed(cc, request.getUntil())).count());

        var notConfirmed = eligibleToProcess.stream().filter(cc ->
                ClassCostConfirmed.valueOf(cc, request.getUntil()).isFalse())
                .collect(Collectors.toList());

        response.setUnconfirmedWagesCount(notConfirmed.size());
        response.setUnconfirmedClassesIds(notConfirmed.stream().map(it -> it.getCourseClass().getId()).collect(Collectors.toSet()));

        return response;
    }

    private List<ClassCost> getCostsToProceed(PayrollGenerationRequest request, ObjectContext context) {

        if (request.getIds() != null && request.getIds().size() > 0) {
            Stream<ClassCost> costs;
            switch (request.getEntityName()) {
                case COURSE_CLASS_ENTITY_NAME: {
                    costs = EntityUtil.getObjectsByIds(context, CourseClass.class, request.getIds())
                            .stream().flatMap(it -> it.getCosts().stream());
                    break;
                }
                case CONTACT_ENTITY_NAME: {
                    costs = EntityUtil.getObjectsByIds(context, Contact.class, request.getIds())
                            .stream().flatMap(it -> it.getClassCosts().stream());
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unsupported entity: " + request.getEntityName());
            }
            return costs
                    .filter(it -> WAGES.equals(it.getFlowType()) && (!it.getCourseClass().getIsCancelled()) || it.getIsSunk())
                    .filter(cost -> cost.getTutorRole() != null)
                    .collect(Collectors.toList());
        }
        return getEligibleClassCosts(context, request.getUntil(), null);
    }


    protected boolean isEligibleToProcess(ClassCost classCost, Date untilDate) {
        var tutor = classCost.getContact().getTutor();
        if (tutor == null || tutor.getPayType() == null) {
            return false;
        }
        var result = false;
        var courseClass = classCost.getCourseClass();

        var fetchUntilDate = (isDistantCourseClass(courseClass) || (isHybridCourseClass(courseClass) && courseClass.getStartDateTime() == null)) ? untilDate : courseClass.getStartDateTime();

        switch (classCost.getRepetitionType()) {
            case FIXED:
                result = !hasPaylines(classCost) &&
                        hasEligibleRateOnDate(classCost, untilDate) &&
                        hasClassStarted(courseClass, untilDate);
                break;
            case PER_SESSION:
            case PER_TIMETABLED_HOUR:
                if (!isDistantCourseClass(courseClass)) {
                    assert classCost.getTutorRole() != null;
                    result = classCost.getTutorRole().getSessionsTutors().stream().anyMatch(
                            tutorAttendance ->
                                    tutorAttendance.getEndDatetime().before(untilDate)
                                    && tutorAttendance.getActualPayableDurationHours().compareTo(BigDecimal.ZERO) > 0
                                    && !isAlreadyPaid(classCost, tutorAttendance.getSession())
                                    && hasEligibleRateOnDate(classCost, tutorAttendance.getStartDatetime())
                    );
                }
                break;
            case PER_ENROLMENT:
                result = !hasPaylines(classCost)
                        && hasValidEnrolments(courseClass)
                        && hasEligibleRateOnDate(classCost, fetchUntilDate)
                        && hasClassStarted(courseClass, untilDate);
                break;
            case PER_UNIT:
                result = !hasPaylines(classCost)
                        && !hasZeroOrMoreUnits(classCost)
                        && hasEligibleRateOnDate(classCost, fetchUntilDate)
                        && hasClassStarted(courseClass, untilDate);
                break;
            case PER_STUDENT_CONTACT_HOUR:
                if (!isDistantCourseClass(courseClass) && !(isHybridCourseClass(courseClass) && courseClass.getStartDateTime() == null)) {
                    result = !hasPaylines(classCost)
                            && hasEligibleRateOnDate(classCost, courseClass.getStartDateTime())
                            && hasZeroOrMorePayableEnrolments(courseClass);
                }
                break;
            default:
                break;
        }
        return result;
    }

    private boolean unprocessed(ClassCost classCost, Date date) {
        if (classCost.getRepetitionType() == PER_SESSION || classCost.getRepetitionType() == PER_TIMETABLED_HOUR) {
            return classCost.getTutorRole().getSessionsTutors().stream()
                    .anyMatch(tutorAttendance ->
                            tutorAttendance.getEndDatetime().before(date)
                            && !isAlreadyPaid(classCost, tutorAttendance.getSession())
                            && !AttendanceType.DID_NOT_ATTEND_WITHOUT_REASON.equals(tutorAttendance.getAttendanceType())
                            && !AttendanceType.UNMARKED.equals(tutorAttendance.getAttendanceType()));
        } else {
            return true;
        }
    }

    protected boolean hasEligibleRateOnDate(ClassCost classCost, Date date) {
        return classCost.getPerUnitAmountExTax() != null || classCost.getTutorRole().getDefinedTutorRole().getPayRates().stream()
                .anyMatch(r -> r.getValidFrom().before(date) && r.getRate().isGreaterThan(Money.ZERO));
    }

    private boolean isAlreadyPaid(ClassCost classCost, Session session) {
        return classCost.getPaylines().stream()
                .anyMatch(payLine -> payLine.getSession() != null && payLine.getSession().equals(session));
    }

    private boolean hasPaylines(ClassCost classCost) {
        return !classCost.getPaylines().isEmpty();
    }

    private boolean isDistantCourseClass(CourseClass courseClass) {
        return courseClass.getIsDistantLearningCourse();
    }

    private boolean isHybridCourseClass(CourseClass courseClass) {
        return courseClass.getIsHybrid();
    }

    private boolean hasValidEnrolments(CourseClass courseClass) {
        return courseClass.getValidEnrolmentCount() > 0;
    }
    
    private boolean hasZeroOrMoreUnits(ClassCost classCost){
        return BigDecimal.ZERO.compareTo(classCost.getUnitCount()) >= 0;
    }

    private boolean hasZeroOrMorePayableEnrolments(CourseClass courseClass) {
        return BigDecimal.ZERO.compareTo(
                courseClass.getPayableClassroomHours()
                        .multiply(BigDecimal.valueOf(courseClass.getValidEnrolmentCount()))) >= 0;
    }
}
