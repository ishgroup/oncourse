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
package ish.messaging;

import ish.common.types.DeliveryMode;
import ish.math.Money;
import ish.oncourse.cayenne.CourseClassInterface;
import ish.oncourse.cayenne.DiscountCourseClassInterface;
import ish.oncourse.cayenne.PersistentObjectI;
import ish.oncourse.server.cayenne.*;
import ish.oncourse.server.cayenne.Module;

import java.math.BigDecimal;
import java.util.List;
import java.util.TimeZone;

public interface ICourseClass extends PersistentObjectI {

    String END_DATE_TIME_KEY = "endDateTime";
	String FEE_INC_GST = "feeIncGst";
    String START_DATE_TIME_KEY = "startDateTime";
    String IS_CANCELLED_KEY = "isCancelled";

	String DISCOUNT_FEES_PROPERTY = "discount_fees";

	String ENROLMENTS_TO_PROCEED_MESSAGE = "enrolments_to_proceed";
	String ENROLMENTS_TO_PROFIT_MESSAGE = "enrolments_to_profit";

    String REFUNDED_AND_CANCELLED_ENROLMENTS_COUNT_PROPERTY = "refunded_and_cancelled_enrolments_count";
	String MALE_ENROLMENTS_COUNT_PROPERTY = "male_enrolments_count";

	String OUTCOMES_PROPERTY = "outcomes";
	String UNIQUE_CODE_PROPERTY = "uniqueCode";
	String TUTOR_NAMES_PROP = "tutor_names";
	String TUTOR_NAMES_ABRIDGED_PROP = "tutor_names_abridged";
	String SUCCESS_AND_QUEUED_ENROLMENTS_PROPERTY = "successAndQueuedEnrolments";

	String TIMETABLE_SUMMARY_PROPERTY = "timetableSummary";

	// hours fields
	String STUDENT_CONTACT_HOURS_PROP = "studentContactHoursProp";
	// + reportable hours from model
	String CLASSROOM_HOURS = "classroomHoursProp";
	String NOMINAL_HOURS_PROP = "nominalHoursProp";
	String PLACES_LEFT_PROPERTY = "places_left";

	String UNIQUE_SESSION_MODULES_PROPERTY = "unique_session_modules";
	String SESSION_MODULES_PROPERTY = "session_modules";

	String FULL_NAME_OF_CLASS_WITH_CODE = "full_name_of_class_with_code";

	String getCode();

	ICourse getCourse();

    Money getFeeIncGst();

    Room getRoom();

	List<CourseClassTutor> getTutorRoles();

	String getUniqueCode();

	Integer getSessionsCount();

	BigDecimal getPayableClassroomHours();

	List<Session> getSessions();

	List<Enrolment> getEnrolments();

	int getValidEnrolmentCount();

	Integer getBudgetedPlaces();

	List<ClassCost> getCosts();

	List<Enrolment> getSuccessAndQueuedEnrolments();

	Money getFeeExGst();

	Integer getMaximumPlaces();

	Integer getMinutesPerSession();

	List<Discount> getDiscounts();

	Tax getTax();

	Boolean getIsCancelled();

	TimeZone getTimeZone();

	BigDecimal getReportableHours();

	BigDecimal getExpectedHours();

	List<InvoiceLine> getInvoiceLines();

	List<DiscountCourseClass> getDiscountCourseClasses();

	void addModuleToAllSessions(Module module);

	DeliveryMode getDeliveryMode();
}
