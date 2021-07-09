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
package ish.oncourse.entity.services;

import com.google.inject.Inject;
import ish.budget.ClassBudgetUtil;
import ish.common.types.EnrolmentStatus;
import ish.math.Money;
import ish.messaging.*;
import ish.oncourse.cayenne.CourseClassUtil;
import ish.oncourse.cayenne.DiscountCourseClassInterface;
import ish.oncourse.function.CalculateClassroomHours;
import ish.oncourse.server.cayenne.CourseClassTrait;
import ish.oncourse.server.cayenne.Room;
import ish.oncourse.server.cayenne.Student;
import ish.oncourse.server.cayenne.Tutor;
import ish.util.*;
import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;

import java.math.BigDecimal;
import java.util.*;

/**
 */
public class CourseClassService {

	private SessionService sessionService;

	@Inject
	public CourseClassService(SessionService sessionService) {
		this.sessionService = sessionService;
	}

	/**
	 * Returns list of successful and queued enrolments for specified class.
	 *
	 * @param courseClass
	 * @return list of enrolments
	 */
	public List<? extends IEnrolment> getSuccessfulAndQueuedEnrolments(ICourseClass courseClass) {

		List<? extends IEnrolment> theEnrolments = courseClass.getEnrolments();

		if (theEnrolments == null || theEnrolments.size() == 0) {
			return theEnrolments;
		}
		Expression validEnrolmentExpr = ExpressionFactory.matchExp(IEnrolment.STATUS_PROPERTY, null);
		for (EnrolmentStatus es : EnrolmentStatus.STATUSES_LEGIT) {
			validEnrolmentExpr = validEnrolmentExpr.orExp(ExpressionFactory.matchExp(IEnrolment.STATUS_PROPERTY, es));
		}

		return validEnrolmentExpr.filterObjects(theEnrolments);

	}

	/**
	 * Returns count of successful and queued enrolments in class.
	 *
	 * @param courseClass
	 * @return count of successful and queued enrolments
	 */
	public int getValidEnrolmentsCount(ICourseClass courseClass) {
		return getSuccessfulAndQueuedEnrolments(courseClass).size();
	}

	/**
	 * @return number of places are left for enrolments in the class
	 */
	public int getPlacesLeft(ICourseClass courseClass) {
		if (courseClass.getMaximumPlaces() == null) {
			// can happen when initializing new controller
			return 0;
		}
		return courseClass.getMaximumPlaces() - getValidEnrolmentsCount(courseClass);
	}

	/**
	 * Get number of enrolments for class to be profitable.
	 *
	 * @param courseClass
	 * @return required number of enrolments
	 */
	public Integer getEnrolmentsToProfit(ICourseClass courseClass) {
		if (courseClass.getFeeExGst().isZero()) {
			return 0;
		}

		return (int) Math.ceil(ClassBudgetUtil.getClassCostsExTax(courseClass, ClassBudgetUtil.ACTUAL)
				.subtract(ClassBudgetUtil.getClassIncomeExTax(courseClass, ClassBudgetUtil.ACTUAL)).divide(courseClass.getFeeExGst()).doubleValue());
	}

	/**
	 * Get number of enrolments for class to cover its running costs.
	 *
	 * @param courseClass
	 * @return required number of enrolments
	 */
	public Integer getEnrolmentsToProceed(ICourseClass courseClass) {
		if (courseClass.getFeeExGst().isZero()) {
			return 0;
		}

		return (int) Math.ceil(ClassBudgetUtil.getClassRunningCostsExTax(courseClass, ClassBudgetUtil.ACTUAL)
				.subtract(ClassBudgetUtil.getClassIncomeExTax(courseClass, ClassBudgetUtil.ACTUAL)).divide(courseClass.getFeeExGst()).doubleValue());
	}

	/**
	 * Get unique code for class, i.e. <code>{course code}-{class code}</code>
	 *
	 * @return unique code for class
	 */
	public String getUniqueCode(ICourseClass courseClass) {
		StringBuilder buff = new StringBuilder();
		if (courseClass.getCourse() != null && courseClass.getCourse().getCode() != null) {
			buff.append(courseClass.getCourse().getCode());
		}
		buff.append("-");
		if (courseClass.getCode() != null) {
			buff.append(courseClass.getCode());
		}
		return buff.toString();
	}

	/**
	 * Get tutor names delimited by semicolon.
	 *
	 * @param courseClass
	 * @return tutor names
	 */
	public String getTutorNames(ICourseClass courseClass) {
		StringBuilder result = new StringBuilder();
		if (courseClass.getTutorRoles() != null) {
			for (ICourseClassTutor t : courseClass.getTutorRoles()) {
				result.append(t.getTutor().getContact().getName());
				if (result.length() > 0) {
					result.append("; ");
				}
			}
		}
		return result.toString();
	}

	public String getTutorNamesAbriged(ICourseClass courseClass) {
		List<? extends Tutor> tutors = getTutors(courseClass);
		if (tutors == null || tutors.size() == 0) {
			return "not set";
		} else if (tutors.size() == 1) {
			return tutors.get(0).getContact().getName();
		}
		return tutors.get(0).getContact().getName() + " et al";
	}

	public List<? extends Tutor> getTutors(ICourseClass courseClass) {
		List<? extends ICourseClassTutor> avalableTutorRoles = courseClass.getTutorRoles();
		List<Tutor> tutors = new ArrayList<>();
		if (avalableTutorRoles != null) {
			for (ICourseClassTutor aRole : avalableTutorRoles) {
				if (aRole.getTutor() != null) {
					tutors.add(aRole.getTutor());
				}
			}
		}
		return tutors;
	}

	/**
	 * Get all outcomes for specified class.
	 *
	 * @param courseClass
	 * @return class outcomes
	 */
	public List<? extends IOutcome> getOutcomes(ICourseClass courseClass) {
		List<IOutcome> outcomes = new ArrayList<>();
		for (IEnrolment e : courseClass.getEnrolments()) {
			outcomes.addAll(e.getOutcomes());
		}

		return outcomes;
	}

	/**
	 * Total fee income ex tax for refunded and cancelled classes.
	 *
	 * @param courseClass
	 * @return total fee income ex tax for refunded and cancelled classes
	 */
	public Money getClassTotalFeeIncomeExTaxForRefundedAndCancelledEnrolments(ICourseClass courseClass) {
		Money result = Money.ZERO;
		List<IEnrolment> canceledRefundedEnrolmentList = (List<IEnrolment>) CourseClassUtil.getRefundedAndCancelledEnrolments(courseClass.getEnrolments());

		if (canceledRefundedEnrolmentList != null) {
			result = canceledRefundedEnrolmentList.stream().flatMap(e -> e.getInvoiceLines().stream())
					.map(il -> il.getPriceTotalExTax() != null ? il.getPriceTotalExTax().subtract(il.getDiscountEachExTax()) : Money.ZERO)
					.reduce(Money.ZERO, (a, b) -> a.add(b));
		}
		return result;
	}

	/**
	 * @return a sentence describing the timetable, html formatted. Used in activity bubble and mouse hover effect.
	 */
	public String getTimetableSummaryForClass(ICourseClass courseClass) {
		StringBuilder summary = new StringBuilder("<html>");

		List<? extends ISession> sessions = courseClass.getSessions();

		int scount = courseClass.getSessions().size();
		if (sessions.size() > 0) {
			if (sessions.size() > 1) {
				summary.append(sessions.size());
				summary.append(" sessions");
			} else {
				summary.append("One session");
			}
			Long duration = null;
			for (ISession session : sessions) {
				if (duration == null) {
					duration = sessionService.getDuration(session);
				} else if (duration.equals(sessionService.getDuration(session))) {
					duration = null;
					break;
				}
			}
			if (duration != null && duration > 0) {
				summary.append(" of ");
				summary.append(DurationFormatter.formatDuration(duration, DurationFormatter.FORMAT_DD_HH_MM));
			}
			summary.append(RuntimeUtil.HTML_LINE_SEPARATOR);
		} else if (courseClass.getSessionsCount() != null && courseClass.getSessionsCount() > 0) {
			summary.append("No recorded sessions. Proposed:");
			summary.append(RuntimeUtil.HTML_LINE_SEPARATOR);
			scount = courseClass.getSessionsCount();
			summary.append(courseClass.getSessionsCount()).append(" sessions");
			if (courseClass.getMinutesPerSession() != null && courseClass.getMinutesPerSession() > 0) {
				summary.append(" of ");
				summary.append(DurationFormatter.formatDuration(courseClass.getMinutesPerSession().longValue() * 60000, DurationFormatter.FORMAT_HH_MM));
			}
			summary.append(RuntimeUtil.HTML_LINE_SEPARATOR);
		} else {
			summary.append("No sessions have been set in the timetable.");
			summary.append(RuntimeUtil.HTML_LINE_SEPARATOR);
		}

		if (courseClass.getStartDateTime() != null) {
			if (scount > 1) {
				summary.append("From ").append(DateTimeFormatter.formatDateTime(courseClass.getStartDateTime(), true, courseClass.getTimeZone()));
				summary.append(" <font color=#444444 size=3>(");
				summary.append(DateTimeFormatter.getUserReadableTimezoneName(courseClass.getTimeZone()));
				summary.append(")</font>");

				summary.append(RuntimeUtil.HTML_LINE_SEPARATOR);
				if (courseClass.getEndDateTime() != null) {
					summary.append("To ").append(DateTimeFormatter.formatDateTime(courseClass.getEndDateTime(), true, courseClass.getTimeZone()));
					summary.append(" <font color=#444444 size=3>(");
					summary.append(DateTimeFormatter.getUserReadableTimezoneName(courseClass.getTimeZone()));
					summary.append(")</font>");
					summary.append(RuntimeUtil.HTML_LINE_SEPARATOR);
				}
			} else {
				summary.append("At ").append(DateTimeFormatter.formatDateTime(courseClass.getStartDateTime(), true, courseClass.getTimeZone()));
				summary.append(RuntimeUtil.HTML_LINE_SEPARATOR);
			}
		}
		Room aRoom = getRoomForAllSessions(courseClass);
		if (aRoom != null && aRoom.getSite() != null) {
			summary.append("All sessions held in ");
			summary.append(aRoom.getName());
			summary.append(", ");
			summary.append(aRoom.getSite().getName());
			summary.append(RuntimeUtil.HTML_LINE_SEPARATOR);
		} else if (sessions.size() > 0) {
			ISession firstSession = getFirstSession(courseClass);

			if (firstSession.getRoom() != null && firstSession.getRoom().getSite() != null) {
				aRoom = sessions.get(0).getRoom();
				summary.append("First session held in ");
				summary.append(aRoom.getName());
				summary.append(", ");
				summary.append(aRoom.getSite().getName());
				summary.append(RuntimeUtil.HTML_LINE_SEPARATOR);
			} else {
				if (getFirstRoomSpecified(courseClass) != null) {
					summary.append("Held in various rooms");
					summary.append(RuntimeUtil.HTML_LINE_SEPARATOR);
				} else {
					summary.append("Room is unspecified");
					summary.append(RuntimeUtil.HTML_LINE_SEPARATOR);
				}
			}
		}

		return summary.append("</html>").toString();
	}

	/**
	 * @return the room for all the sessions (if all sessions have the same room), or the room for the class if all sessions have no room, or null if any
	 *         sessions have a different room.
	 */
	public Room getRoomForAllSessions(ICourseClass courseClass) {
		Room aRoom = null;

		List<? extends ISession> sessions = courseClass.getSessions();

		if (sessions != null && sessions.size() > 0) {
			aRoom = sessions.get(0).getRoom();
		}

		if (sessions != null) {
			for (ISession session : sessions) {
				if (session.getRoom() != null && !session.getRoom().equals(aRoom) || session.getRoom() == null && aRoom != null) {
					return null;
				}
			}
		}
		if (aRoom == null && sessions != null && sessions.size() == 0) {
			aRoom = courseClass.getRoom();
		}
		return aRoom;
	}

	/**
	 * @return Room of first class session if current class room is not set
	 */
	public Room getFirstRoomSpecified(ICourseClass courseClass) {
		if (courseClass.getRoom() != null) {
			return null;
		}
		List<? extends ISession> theSessions = courseClass.getSessions();
		for (ISession session : theSessions) {
			Room aRoom = session.getRoom();
			if (aRoom != null) {
				return aRoom;
			}
		}
		return null;
	}

	/**
	 * @return first session by start time for specified class.
	 */
	public ISession getFirstSession(ICourseClass courseClass) {
		List<? extends ISession> sessions = courseClass.getSessions();

		if (!sessions.isEmpty()) {
			Ordering.orderList(sessions, Collections.singletonList(
					new Ordering(ISession.START_DATETIME_PROPERTY, SortOrder.ASCENDING)));
			return sessions.get(0);
		}

		return null;
	}

	/**
	 * @return number of successful male enrolments in the class
	 */
	public Integer getMaleCount(ICourseClass courseClass) {
		List<? extends IEnrolment> list = getSuccessfulAndQueuedEnrolments(courseClass);
		if (list.isEmpty()) {
			return 0;
		}
		Expression e = ExpressionFactory.matchExp(IEnrolment.STUDENT_KEY + "." + Student.CONTACT_KEY + "." + IContact.IS_MALE_KEY, true);

		return e.filterObjects(list).size();
	}

	/**
	 * @see CourseClassTrait#getClassroomHours()
	 */
	@Deprecated
	public BigDecimal getClassroomHours(ICourseClass courseClass) {
		return CalculateClassroomHours.valueOf(courseClass).calculate();
	}

	/**
	 * @return '/' separated list of discounts for this class and their values
	 */
	public String getDiscountFees(ICourseClass courseClass) {
		StringBuilder result = new StringBuilder();
		for (IDiscount d : courseClass.getDiscounts()) {
			if (result.length() > 0) {
				result.append(" / ");
			}
			result.append(d.getName());
			result.append(" ");

			Money feeExGst = courseClass.getFeeExGst();
			BigDecimal rate = courseClass.getTax() != null ? courseClass.getTax().getRate() : null;


			for (DiscountCourseClassInterface discountCourseClass : courseClass.getDiscountCourseClasses()) {
				if (discountCourseClass.getDiscount().equals(d)) {
					result.append(DiscountUtils.getDiscountedFee(discountCourseClass, feeExGst, rate));
					break;
				}
			}
		}
		return result.toString();
	}

	/**
	 * Returns one {@link ISessionModule} instance for each {@link IModule} class has. <br>
	 * This is needed to provide Training Plan table with records list without duplicated modules for each session.
	 *
	 * @return list of SessionModules for each Module class has
	 */
	public List<? extends ISessionModule> getUniqueSessionModules(ICourseClass courseClass) {

		List<ISessionModule> uniqueSessionModules = new ArrayList<>();

		Set<IModule> classModules = new HashSet<>();
		for (ISession s : courseClass.getSessions()) {
			for (ISessionModule sm : s.getSessionModules()) {
				classModules.add(sm.getModule());
			}
		}

		List<? extends ISessionModule> allSessionModules = getSessionModules(courseClass);

		for (IModule module : classModules) {
			Expression exp = ExpressionFactory.matchExp(ISessionModule.MODULE_KEY, module);
			List<? extends ISessionModule> sessionModules = exp.filterObjects(allSessionModules);
			if (!sessionModules.isEmpty()) {
				uniqueSessionModules.add(sessionModules.get(0));
			}
		}

		return uniqueSessionModules;
	}

	public List<? extends ISessionModule> getSessionModules(ICourseClass courseClass) {
		List<ISessionModule> sessionModules = new ArrayList<>();

		for (ISession s : courseClass.getSessions()) {
			sessionModules.addAll(s.getSessionModules());
		}

		return sessionModules;
	}


	/**
	 * creates next logical code for a class. code is based on current code or code for the last available class.
	 * @deprecated see CourseTrait.getNextAvailableCode(CourseClass courseClass)
	 * @return new code
	 */
	@Deprecated
	public String getNextAvailableCode(ICourseClass courseClass) {
		String oldCode = courseClass.getCode();
		if (oldCode == null) {
			if (courseClass.getCourse() != null) {
				ICourseClass latestClass = getLatestSavedClass(courseClass.getCourse());
				if (latestClass != null) {
					return getNextAvailableCode(latestClass);
				}
			}
			return "1";
		}

		int i = oldCode.length();
		while (i > 0 && Character.isDigit(oldCode.charAt(i - 1))) {
			i--;
		}
		String staticPart = oldCode.substring(0, i);
		String oldNumericPart = oldCode.substring(i);

		int n = 0;
		if (!oldNumericPart.isEmpty()) {
			n = Integer.valueOf(oldNumericPart);
		}

		String newCode;
		do {
			n++;
			String newNumericPart = "" + n;
			while (newNumericPart.length() < oldNumericPart.length()) {
				newNumericPart = 0 + newNumericPart;
			}

			newCode = staticPart + newNumericPart;
		} while (timesClassCodeRepeatsWithinCourse(newCode, courseClass) > 0);
		return newCode;
	}

	/**
	 * calculates how many times the class code repeats within the course.
	 * @deprecated see CourseTrait.getNextAvailableCode(CourseClass courseClass)
	 * @param aCode
	 * @return
	 */
	@Deprecated
	public int timesClassCodeRepeatsWithinCourse(String aCode, ICourseClass courseClass) {
		if (courseClass.getCourse() != null && courseClass.getCourse().getCourseClasses() != null) {
			int result = 0;
			for (ICourseClass iCourseClass : courseClass.getCourse().getCourseClasses()) {
				if (!iCourseClass.equals(courseClass) && iCourseClass.getCode() != null && iCourseClass.getCode().equalsIgnoreCase(aCode)) {
					result++;
				}
			}
			return result;
		}
		return -1;
	}


	/**
	 * @return the CourseClass within this Course which was created most recently
	 */
	private ICourseClass getLatestSavedClass(ICourse course) {
		if (course.getCourseClasses() == null || course.getCourseClasses().size() == 0) {
			return null;
		}

		Ordering o = new Ordering("createdOn", SortOrder.DESCENDING);
		ArrayList<Ordering> orderings = new ArrayList<>();
		orderings.add(o);
		ArrayList<ICourseClass> classesSorted = new ArrayList<>(course.getCourseClasses());
		Ordering.orderList(classesSorted, orderings);

		for (ICourseClass cc : classesSorted) {
			if (cc.getPersistenceState() != PersistenceState.NEW) {
				return cc;
			}
		}
		return null;

	}
}
