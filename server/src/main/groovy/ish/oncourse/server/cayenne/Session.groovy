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

import ish.common.CalculateEndDate
import ish.common.CalculateStartDate
import ish.common.types.EnrolmentStatus
import ish.messaging.IModule
import ish.messaging.ISession
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.entity.delegator.OutcomeDelegator
import ish.oncourse.server.cayenne.glue._Session
import ish.util.DurationFormatter
import ish.util.LocalDateUtils
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull

/**
 * Sessions represent the classroom sessions a student attends when enrolled in a class. Sessions are a timetabled event. Not all classes have sessions.
 * Self paced classes will no related sessions attached to them.
 */
@API
@QueueableEntity
class Session extends _Session implements SessionTrait, ISession, Queueable, AttachableTrait {
	private static final Logger logger = LogManager.getLogger()

	public static String DISPLAY_START_DATETIME = 'displayStartDateTime'
	public static String DISPLAY_END_DATETIME = 'displayEndDateTime'



	@Override
	void onEntityCreation() {
		super.onEntityCreation()

		if (getPayAdjustment() == null) {
			setPayAdjustment(0)
		}
	}

	@Override
	void addToAttachmentRelations(AttachmentRelation relation) {
		addToAttachmentRelations((SessionAttachmentRelation) relation)
	}

	@Override
	void removeFromAttachmentRelations(AttachmentRelation relation) {
		removeFromAttachmentRelations((SessionAttachmentRelation) relation)
	}

	@Override
	Class<? extends AttachmentRelation> getRelationClass() {
		return SessionAttachmentRelation.class
	}

	/**
	 * The payable duration may differ from the actual session duration. If no adjustment has been made
	 * to the regular session duration, then the regular session duration is returned here.
	 *
	 * @return duration in hours
	 */
	@API
	BigDecimal getPayableDurationInHours() {
		if (getEndDatetime() == null || getStartDatetime() == null) {
			return new BigDecimal("0")
		}
		return DurationFormatter.parseDurationInHours(getEndDatetime().getTime() - getStartDatetime().getTime() -
				(getPayAdjustment() == null ? 0 : getPayAdjustment() * 60000))
	}

	/**
	 * @return the duration of this session in hours
	 */
	@API
	BigDecimal getDurationInHours() {
		if (getEndDatetime() == null || getStartDatetime() == null) {
			return new BigDecimal("0")
		}
		return DurationFormatter.parseDurationInHours(getEndDatetime().getTime() - getStartDatetime().getTime())
	}

	@Override
	void postPersist() {
		updateRelatedOutcomes()
	}

	@Override
	void preUpdate(){
		super.preUpdate()
		updateRelatedOutcomes()
	}

	private void updateRelatedOutcomes() {
		List<Outcome> outcomes = sessionModules*.module*.outcomes*.findAll{o -> courseClass.enrolments*.id.contains(o.enrolment?.id) }.flatten() as List<Outcome>

		outcomes.findAll { !it.startDateOverridden }
				.each { o ->
					o.setStartDate(LocalDateUtils.dateToValue(new CalculateStartDate(OutcomeDelegator.valueOf(o), Boolean.TRUE).calculate()))
				}
		outcomes.findAll { !it.endDateOverridden }
				.each { o ->
					o.setEndDate(LocalDateUtils.dateToValue(new CalculateEndDate(OutcomeDelegator.valueOf(o), Boolean.TRUE).calculate()))
				}
	}

	/**
	 * Returns {@code true} when session linked to specified module.
	 *
	 * @param module module to search
	 * @return if session linked to specified module
	 */
	@API
	boolean hasModule(Module module) {
		return hasModule(module as IModule)
	}

	@Override
	boolean hasModule(IModule module) {
		Expression exp = ExpressionFactory.matchExp(SessionModule.MODULE_PROPERTY, module)

		return !exp.filterObjects(getSessionModules()).isEmpty()
	}

	/**
	 * Every session has a natural timezone which is derived from the site in which
	 * that session is delivered. For sessions not linked to a room or site, the default
	 * timezone for the college is returned.
	 *
	 * Note that on the onCourse website, virtual sessions (eg. online delivery) will be shown in
	 * the browser's local timezone.
	 *
	 * @return timezone
	 */
	@API
	@Override
	TimeZone getTimeZone() {
		if (getRoom() == null || getRoom().getSite() == null ||
				getRoom().getSite().getIsVirtual() || getRoom().getSite().getLocalTimezone() == null) {
			return TimeZone.getDefault()
		}
		return TimeZone.getTimeZone(getRoom().getSite().getLocalTimezone())
	}

	/**
	 * @return the date and time this record was created
	 */
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	/**
	 * @return the end time for this session
	 */
	@API
	@Override
	Date getEndDatetime() {
		return super.getEndDatetime()
	}


	/**
	 * @return the date and time this record was modified
	 */
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * @return difference between actual session duration and payable duration in minutes
	 */
	@Nonnull
	@API
	@Override
	Integer getPayAdjustment() {
		return super.getPayAdjustment()
	}

	/**
	 * @return private notes for this session
	 */
	@API
	@Override
	String getPrivateNotes() {
		return super.getPrivateNotes()
	}

	/**
	 * @return public notes (rich text)
	 */
	@API
	@Override
	String getPublicNotes() {
		return super.getPublicNotes()
	}

	/**
	 * @return the start time for this session
	 */
	@API
	@Override
	Date getStartDatetime() {
		return super.getStartDatetime()
	}

	/**
	 * @return attendance records for all students/tutors enrolled/teaching this session
	 */
	@Nonnull
	@API
	@Override
	List<Attendance> getAttendance() {
		return super.getAttendance()
	}

	/**
	 * @return attendance records for all SUCCESS enrolments
	 */
	@Nonnull
	@API
	List<Attendance> getActiveAttendances() {
		List<Student> students = courseClass.enrolments.findAll { e -> EnrolmentStatus.SUCCESS == e.status}*.student
		return attendance.findAll { a -> a.student in students }
	}
	/**
	 * This is just a convenience to save having to write {@code session.courseClass.course}
	 * @return the course related to this session
	 */
	@Nonnull
	@API
	@Override
	Course getCourse() {
		return super.getCourse()
	}

	/**
	 * @return the class related to this session
	 */
	@Nonnull
	@API
	@Override
	CourseClass getCourseClass() {
		return super.getCourseClass()
	}

	/**
	 * @return all modules delivered as part of the training plan
	 */
	@Nonnull
	@API
	@Override
	List<Module> getModules() {
		return super.getModules()
	}

	/**
	 * @return payroll entries for this session
	 */
	@Nonnull
	@API
	@Override
	List<PayLine> getPayLines() {
		return super.getPayLines()
	}

	/**
	 * @return the room in which the session is held
	 */
	@Nonnull
	@API
	@Override
	Room getRoom() {
		return super.getRoom()
	}

	/**
	 * @return
	 */
	@Nonnull
	@Override
	List<SessionModule> getSessionModules() {
		return super.getSessionModules()
	}

	/**
	 * @return
	 */
	@Nonnull
	@Override
	List<TutorAttendance> getSessionTutors() {
		return super.getSessionTutors()
	}

	/**
	 * @return
	 */
	@Nonnull
	@Override
	List<CourseClassTutor> getTutorRoles() {
		return super.getTutorRoles()
	}

	/**
	 * @return all tutors delivering this session
	 */
	@Nonnull
	@API
	@Override
	List<Tutor> getTutors() {
		return super.getTutors()
	}

}
