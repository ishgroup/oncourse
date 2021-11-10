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
package ish.oncourse.server.print.proxy;

import ish.common.types.AttendanceType;
import ish.common.types.EnrolmentStatus;
import ish.common.types.Gender;
import ish.oncourse.entity.services.EnrolmentService;
import ish.oncourse.server.cayenne.Enrolment;
import ish.oncourse.server.cayenne.Session;
import ish.oncourse.server.cayenne.Student;
import ish.oncourse.server.cayenne.Tutor;
import ish.oncourse.server.cayenne.glue.CayenneDataObject;
import ish.print.PrintableObject;
import ish.util.TimeZoneUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.Date;
import java.util.TimeZone;

/**
 */
public class PrintableAttendance implements PrintableObject {

	private static final Logger logger = LogManager.getLogger();

	public static final String STUDENT_PROPERTY = "student";
	public static final String ENROLMENT_PROPERTY = "enrolment";
	public static final String TUTOR_PROPERTY = "tutor";
	public static final String SESSION_PROPERTY = "session";

	public static final String STUDENT_NAME = "studentName";
	public static final String STUDENT_FIRST_NAME = "studentFirstName";
	public static final String STUDENT_LAST_NAME = "studentLastName";
	public static final String STUDENT_SEX = "sex";
	public static final String STUDENT_MOBILE = "mobile";
	public static final String STUDENT_AGE = "age";
	public static final String STUDENT_BIRTDATE = "birthDate";
	public static final String STUDENT_NUMBER = "studentNo";
	public static final String INVOICE_NUMBER = "invoiceNo";
	public static final String SESSION_DESC = "sessionDesc";
	public static final String SESSION_DATE = "sessionDate";
	public static final String PRESENT = "isPresent";
	public static final String IS_TUTOR = "isTutor";

	public static final String IS_SORTABLE = "isSortable";
    public static final String ATTENDANCE_TYPE = "attendanceType";
    public static final String DISPLAYABLE_STATUS = "displayableStatus";

	private EnrolmentService enrolmentService;

	private String text;
	private Tutor tutor;
	private Enrolment enrolment;

	private Session session;

	private Boolean isSortable;

	private Boolean isPresent;
    private AttendanceType type;

	public Boolean getIsPresent() {
		return this.isPresent;
	}

	public void setIsPresent(Boolean isPresent) {
		this.isPresent = isPresent;
	}

	public void setIsPresent(AttendanceType type) {
		if (type == null || AttendanceType.UNMARKED.equals(type)) {
			this.isPresent = null;
		} else {
			this.isPresent = AttendanceType.ATTENDED.equals(type) || AttendanceType.PARTIAL.equals(type);
		}

	}

	public Boolean getIsSortable() {
		return isSortable;
	}

	public void setIsSortable(Boolean isSortable) {
		this.isSortable = isSortable;
	}

	public Object getValueForKey(CayenneDataObject dataObject, String key) {
		if (key.contains(".")) {
			var subKey = key.substring(0, key.indexOf("."));
			var newKey = key.substring(key.indexOf(".") + 1);

			var newObject = dataObject.getValueForKey(subKey);
			if (newObject instanceof CayenneDataObject) {
				return getValueForKey((CayenneDataObject) newObject, newKey);
			} else {
				return newObject;
			}
		} else {
			return dataObject.getValueForKey(key);
		}
	}

	public Object getValueForKey(String key) {

		if (key.contains(".")) {
			var object = getValueForKey(key.substring(0, key.indexOf(".")));
			if (object instanceof CayenneDataObject) {
				var dataObject = (CayenneDataObject) object;
				return getValueForKey(dataObject, key.substring(key.indexOf(".") + 1));
			} else {
				return null;
			}
		}

		switch (key) {
			case STUDENT_PROPERTY:
				return getStudent();
			case TUTOR_PROPERTY:
				return getTutor();
			case ENROLMENT_PROPERTY:
				return getEnrolment();
			case STUDENT_NAME:
				return getStudentName();
			case STUDENT_FIRST_NAME:
				return getStudentFirstName();
			case STUDENT_LAST_NAME:
				return getStudentLastName();
			case IS_TUTOR:
				return getIsTutor();
			case SESSION_DESC:
				return getSessionDesc();
			case SESSION_DATE:
				return getSessionDate();
			case PRESENT:
				return getIsPresent();
			case STUDENT_NUMBER:
				return getStudentNo();
			case INVOICE_NUMBER:
				return getInvoiceNo();
			case STUDENT_MOBILE:
				return getMobile();
			case STUDENT_AGE:
				return getAge();
			case STUDENT_SEX:
				return getSex();
			case IS_SORTABLE:
				return getIsSortable();
			case STUDENT_BIRTDATE:
				return getBirthDate();
            case ATTENDANCE_TYPE:
                return type;
            case DISPLAYABLE_STATUS:
                return getDisplayableStatus();
			default:
				return null;
		}
	}

	public String getStudentName() {
		if (getEnrolment() == null) {
			if (getText() != null) {
				return getText();
			} else if (getTutor() != null) {
				return "Tutor Signature: " + getTutor().getContact().getName(false);
			}
		}
		return getStudentContactName();
	}

	public String getStudentFirstName() {
		if (getEnrolment() == null) {
			if (getText() != null) {
				return getText();
			} else if (getTutor() != null) {
				return getTutor().getContact().getFirstName();
			}
		}
		return getStudentContactFirstName();
	}

	public String getStudentLastName() {
		if (getEnrolment() == null) {
			if (getText() != null) {
				return getText();
			} else if (getTutor() != null) {
				return getTutor().getContact().getLastName();
			}
		}
		return getStudentContactLastName();
	}

	public Long getStudentNo() {
		if (getEnrolment() == null) {
			return null;
		}
		return getStudent().getStudentNumber();
	}

	public String getInvoiceNo() {
		if (getEnrolment() == null) {
			return "";
		} else if (getEnrolment().getInvoiceLines().isEmpty()) {
			return "Not set";
		} else {
			return enrolmentService.getOriginalInvoiceLine(getEnrolment()).getInvoice().getInvoiceNumber().toString();

		}
	}

	public String getMobile() {
		if (getEnrolment() == null) {
			return "";
		}
		return getStudent().getContact().getMobilePhone();
	}

	public String getAge() {
		if (getEnrolment() == null) {
			return "";
		}
		var age = getStudent().getContact().getAge();
		if (age != null) {
			return age.toString();
		}
		return "";
	}

	public String getSex() {
		if (getEnrolment() == null) {
			return "";
		}
		Gender gender = getStudent().getContact().getGender();
		if ( gender == null || Gender.OTHER_GENDER.getDatabaseValue().equals(gender.getDatabaseValue())) {
			return "Not set";
		}
		return Gender.FEMALE.getDatabaseValue().equals(gender.getDatabaseValue()) ? "Female" : "Male";
	}

	public LocalDate getBirthDate() {
		return getStudent().getContact().getBirthDate();
	}

	public String getShortRecordDescription() {
		return getStudentContactName() + getSessionDesc();
	}

	public String getStudentContactName() {
		if (getEnrolment() == null) {
			return "";
		}

		if (EnrolmentStatus.STATUSES_LEGIT.contains(getEnrolment().getStatus())) {
			return getStudent().getContact().getName(false);
		} else {
			return String.format("<strike>%s</strike>", getStudent().getContact().getName(false));
		}

	}

	public String getStudentContactFirstName() {
		if (getEnrolment() == null) {
			return "";
		}
		return getStudent().getContact().getFirstName();
	}

	public String getStudentContactLastName() {
		if (getEnrolment() == null) {
			return "";
		}
		return getStudent().getContact().getLastName();
	}

	public Boolean getIsTutor() {
		return getTutor() != null;
	}

	public String getSessionDesc() {
		if (getSession() == null || getSession().getRoom() == null) {
			return "";
		}
		return getSession().getRoom().getName();
	}

	public Date getSessionDate() {
		if (getSession() == null) {
			return null;
		}
		return getAdjustedSessionStartDateTime(getSession());
	}

	public Student getStudent() {
		return getEnrolment().getStudent();
	}

	public Session getSession() {
		return this.session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public Enrolment getEnrolment() {
		return this.enrolment;
	}

	public void setEnrolment(Enrolment enrolment) {
		this.enrolment = enrolment;
	}

	/**
	 * @return the tutor
	 */
	public Tutor getTutor() {
		return this.tutor;
	}

	/**
	 * @param tutor the tutor to set
	 */
	public void setTutor(Tutor tutor) {
		this.tutor = tutor;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	private Date getAdjustedSessionStartDateTime(Session session) {
		if (session.getStartDatetime() != null) {
			return TimeZoneUtil.adjustDateForTimeZone(TimeZone.getDefault(), session.getTimeZone(), session.getStartDatetime());
		}
		return null;
	}

    public String getDisplayableStatus() {
	    if (type == null) {
            return getEnrolment() != null && !EnrolmentStatus.STATUSES_LEGIT.contains(getEnrolment().getStatus()) ? "--" : null;
        } else {
            switch (type) {
                case ATTENDED:
                case PARTIAL:
                    return "Y";
                case DID_NOT_ATTEND_WITH_REASON:
                case DID_NOT_ATTEND_WITHOUT_REASON:
                    return "N";
                case UNMARKED:
                default:
                    return null;
            }
        }
    }

	public void setEnrolmentService(EnrolmentService enrolmentService) {
		this.enrolmentService = enrolmentService;
	}


	public static PrintableAttendance valueOf(EnrolmentService enrolmentService, Enrolment enrolment, Session session, AttendanceType type) {
		return valueOf(enrolmentService, true, enrolment, null, session, type, null);
	}

	public static PrintableAttendance valueOf(EnrolmentService enrolmentService, Tutor tutor, Session session, AttendanceType type) {
		return valueOf(enrolmentService, null, null, tutor, session, type, null);
	}

	public static PrintableAttendance valueOf(EnrolmentService enrolmentService, Session session, String text) {
		return valueOf(enrolmentService, false, null, null, session, null, text);
	}

	public static PrintableAttendance valueOf(EnrolmentService enrolmentService, Boolean isSortable,
											  Enrolment enrolment, Tutor tutor, Session session, AttendanceType type, String text) {
		var printableAttendance = new PrintableAttendance();
		printableAttendance.enrolmentService = enrolmentService;
		printableAttendance.isSortable = isSortable;
		printableAttendance.enrolment = enrolment;
		printableAttendance.tutor = tutor;
		printableAttendance.session = session;
        printableAttendance.type = type;
		printableAttendance.text = text;
		return printableAttendance;
	}
}
