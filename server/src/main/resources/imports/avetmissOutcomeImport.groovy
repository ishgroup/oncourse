import ish.common.types.AvetmissStudentIndigenousStatus
import ish.common.types.AvetmissStudentLabourStatus
import ish.common.types.AvetmissStudentSchoolLevel
import ish.common.types.UsiStatus
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Language
import ish.oncourse.server.cayenne.PriorLearning
import ish.oncourse.server.cayenne.Student
import ish.validation.ValidationUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager

import java.time.format.DateTimeFormatter

logger = LogManager.getLogger(getClass())

import120(new String(avetmiss120), new String(avetmiss60), new String(avetmiss80), new String(avetmiss85), context)

context.commitChanges()

def import120(String nat120, String nat60, String nat80, String nat85, ObjectContext context) {
    List<String> validationResult = []
    Map<String, Student> newStudents = [:]
	def lineNumber = 1
	nat120.eachLine { rawLine ->
		def line = new InputLine(rawLine)

		line.readString(10)
		line.readString(10)

		String studentNumber = line.readString(10)
		String moduleId = line.readString(12)
		String courseId = line.readString(10)
		LocalDate start = line.readLocalDate(8)
		LocalDate end = line.readLocalDate(8)
		String deliveryMode = line.readString(3)
		int result = line.readInteger(2)
		String scheduledHours = line.readString(4)
		String fundingSource = line.readString(2)

		line.readString(34)

		String outcomeId = line.readString(3)

		def student = newStudents[studentNumber]

		if (student == null) {
			student = importStudent(studentNumber, nat80, nat85, context)

			if (student == null) {
				validationResult.add("AVETMISS-120: record at line " + lineNumber + " doesn't contain valid student number")
				return null
			} else {
				newStudents.put(studentNumber, student)
			}
		}

		Outcome outcome = context.newObject(Outcome)
		PriorLearning priorLearning = context.newObject(PriorLearning)

		priorLearning.student = student
		outcome.priorLearning = priorLearning

		def module = ObjectSelect.query(Module).where(Module.NATIONAL_CODE.eq(moduleId)).selectOne(context)

		if (module) {
			outcome.module = module
			priorLearning.title = module.title
		} else {
			importModule(moduleId, nat60, priorLearning)
		}

		def qualification = ObjectSelect.query(Qualification).where(Qualification.NATIONAL_CODE.eq(courseId)).selectOne(context)

		if (qualification) {
			priorLearning.qualification = qualification
		}

		outcome.startDate = start
		outcome.endDate = end

		outcome.deliveryMode = DeliveryMode.values().find { value -> value.code == deliveryMode }
		outcome.status = OutcomeStatus.values().find { value -> value.databaseValue == result } ?: OutcomeStatus.STATUS_NOT_SET
		outcome.reportableHours = new BigDecimal(scheduledHours)
		outcome.fundingSource = ClassFundingSource.values().find { value -> value.avetmissCode.equals(fundingSource) }

		priorLearning.outcomeIdTrainingOrg = outcomeId

		if (!validationResult.isEmpty()) {
			logger.error(String.format("Student number: %s, students: %s, validation: %s", studentNumber, newStudents.keySet(), validationResult.join("\n")))
			throw new RuntimeException(validationResult.join("\n"))
		}
		if ((newStudents.size() % 10) == 0) {
			try {
				context.commitChanges()
				newStudents.clear()
			} catch (Exception e) {
				logger.error(String.format("Student number: %s, students: %s", studentNumber, newStudents.keySet()), e)
				throw e
			}
		}

		lineNumber = lineNumber + 1
	}
}

def importModule(String moduleId, String nat60, PriorLearning priorLearning) {
	def nat60Line = nat60.readLines().collect { line -> new InputLine(line) }.find { line ->
		line.readString(1)
		line.readString(12) == moduleId
	}

	if (nat60Line) {
		priorLearning.externalRef = moduleId
		priorLearning.title = nat60Line.readString(100)
	} else {
		throw new RuntimeException("AVETMISS-60: can't find module code " + moduleId + " which was found in the 120 file.")
	}
}

def importStudent(String studentNumber, String nat80, String nat85, ObjectContext context) {
	String nat80Line = nat80.readLines().collect { line -> new InputLine(line) }.find { line -> line.readString(10) == studentNumber }?.text

	if (nat80Line) {
		Contact contact = context.newObject(Contact)
		Student student = context.newObject(Student)

		import80(nat80Line, contact, student, context)

		def nat85Line = nat85.readLines().collect { line -> new InputLine(line) }.find { line -> line.readString(10) == studentNumber }?.text

		if (nat85Line) {
			import85(nat85Line, student)
		}

		return student
	}

	return null
}

def import80(String rawLine, Contact contact, Student student, ObjectContext context) {
	def line = new InputLine(rawLine)
	student.contact = contact
	contact.isStudent = true

	// ------------------
	// client identifier p9
	line.readInteger(10)

	// ------------------
	// name for encryption p59
	String name = line.readString(60)

	String[] studentNames
	if (name != null && !name.trim().empty) {
		if (name.indexOf(',') > 0) {
			studentNames = name.split(",", 2)
		} else {
			studentNames = name.split(" ", 2)
		}
		if (studentNames.length > 0) {
			student.contact.lastName = studentNames[0].trim()
		}
		if (studentNames.length > 1) {
			String givenName = studentNames[1].trim()
			if (givenName.indexOf(' ') > 0) {
				String[] givenNames = givenName.split(" ", 2)
				student.contact.firstName = givenNames[0].trim()
				student.contact.middleName = givenNames[1].trim()
			} else {
				student.contact.firstName = givenName
			}
		}
	} else {
		return null
	}
	// ------------------
	// highest school level completed p43
	Integer highestSchoolLevel = line.readInteger(2)

	if (highestSchoolLevel == null) {
		student.highestSchoolLevel = AvetmissStudentSchoolLevel.DEFAULT_POPUP_OPTION
	} else {
		AvetmissStudentSchoolLevel schoolLevel

		switch (highestSchoolLevel) {
			case 2:
				schoolLevel = AvetmissStudentSchoolLevel.DID_NOT_GO_TO_SCHOOL
				break
			case 8:
				schoolLevel = AvetmissStudentSchoolLevel.COMPLETED_YEAR_8_OR_BELOW
				break
			case 9:
				schoolLevel = AvetmissStudentSchoolLevel.COMPLETED_YEAR_9
				break
			case 10:
				schoolLevel = AvetmissStudentSchoolLevel.COMPLETED_YEAR_10
				break
			case 11:
				schoolLevel = AvetmissStudentSchoolLevel.COMPLETED_YEAR_11
				break
			case 12:
				schoolLevel = AvetmissStudentSchoolLevel.COMPLETED_YEAR_12
				break
			default:
				schoolLevel = null
				break
		}
		student.highestSchoolLevel = schoolLevel
	}
	// ------------------
	// sex p94
	// "F", "M" or "@"
	String gender = line.readString(1)
	student.contact.gender = "M".equals(gender) ? Gender.MALE : "F".equals(gender) ? Gender.FEMALE : "X".equals(gender) ? Gender.OTHER_GENDER : null
	// ------------------
	// date of birth p26
	student.contact.birthDate = line.readLocalDate(8)
	// ------------------
	// postcode p71
	// may be 0000 (unknown)
	// 0001-9999
	// OSPC (overseas)
	// @@@@ (not stated)
	student.contact.postcode = line.readString(4)
	// ------------------
	// indigenous status identifier p46
	// 1 (aboriginal)
	// 2 (Torres Strait)
	// 3 (Aboriginal and Torres Strait)
	// 4 (neither)
	// @ (not stated)
	Integer indStatus = line.readInteger(1)
	if (indStatus != null && indStatus <= 4 && indStatus >= 0) {
		student.indigenousStatus =
				(AvetmissStudentIndigenousStatus) EnumUtil.enumForDatabaseValue(AvetmissStudentIndigenousStatus, indStatus)
	} else {
		student.indigenousStatus = AvetmissStudentIndigenousStatus.DEFAULT_POPUP_OPTION
	}
	// ------------------
	// language spoken at home p50
	// 0000-9999 Australian standard classification of language code
	// (Aust Bureau of Stats: 1267.0)
	Integer absCode = line.readInteger(4)

	ObjectSelect<Language> query = ObjectSelect.query(Language)
	query = query.where(Language.ABS_CODE.eq(String.valueOf(absCode)))
	if (Integer.valueOf(1201).equals(absCode)) {
		query = query.and(Language.NAME.likeIgnoreCase('English'))
	}
	List<Language> languageList = query.select(context)

	if (languageList.size() > 0) {
		student.setLanguage(languageList.get(0))
	}
	// ------------------
	// labour force status identifer p48
	// 01 (full time)
	// 02 (part time)
	// 03 (self employed, not employing others)
	// 04 (employer)
	// 05 (employed unpaid in family business)
	// 06 (unemployed seeking full time)
	// 07 (unemployed seeking part time)
	// 08 (not employed, not seeking)
	// @@ (not stated)
	Integer labourStatus = line.readInteger(2)
	if (labourStatus != null) {
		student.labourForceStatus =
				(AvetmissStudentLabourStatus) EnumUtil.enumForDatabaseValue(AvetmissStudentLabourStatus, labourStatus)
	} else {
		student.labourForceStatus = AvetmissStudentLabourStatus.DEFAULT_POPUP_OPTION
	}
	// ------------------
	// country identifier p19
	// 0000-9999 Aust Bureau of Stats 1269.0
	Integer saccCode = line.readInteger(4)
	Country aCountry = null
	try {
		aCountry = getCountryWithCode(saccCode, context)
	} catch (Exception e) {
		logger.debug("country not found saccCode: {}", saccCode, e)
	}
	student.countryOfBirth = aCountry
	// ------------------
	// disability flag p30
	// Y/N/@
	String disabilityType = line.readString(1)

	student.disabilityType = "Y".equals(disabilityType) ? AvetmissStudentDisabilityType.OTHER
			: AvetmissStudentDisabilityType.DEFAULT_POPUP_OPTION
	// ------------------
	// prior educational achievement flag p75
	// Y/N/@
	// beyond year 12 (sort of)
	String priorEducationCode = line.readString(1)

	student.priorEducationCode = "Y".equals(priorEducationCode) ? AvetmissStudentPriorEducation.MISC
			: AvetmissStudentPriorEducation.DEFAULT_POPUP_OPTION
	// ------------------
	// at school p7
	// still at secondary school
	String stillAtSchool = line.readString(1)

	student.isStillAtSchool = "Y".equals(stillAtSchool) ? Boolean.TRUE : "N".equals(stillAtSchool) ? Boolean.FALSE : null
	// ------------------
	// address suburb or town or locality p4
	String suburb = line.readString(50)
	student.contact.suburb = "NOT PROVIDED".equalsIgnoreCase(suburb) ? null : suburb
	// ------------------
	// Unique student identifier
	String usi = line.readString(10)
	switch (usi) {
		case "INTOFF":
			student.usiStatus = UsiStatus.INTERNATIONAL
			break
		case "INDIV":
			student.usiStatus = UsiStatus.EXEMPTION
			break
		default:
			student.usiStatus = UsiStatus.DEFAULT_NOT_SUPPLIED
			student.usi = usi
			break
	}
	// ------------------
	// State identifier
	line.readString(2)
	// ------------------
	// Street
	String buildingName = line.readString(50)
	String unit = line.readString(30)
	String streetNumber = line.readString(15)
	if ("NOT SPECIFIED".equalsIgnoreCase(streetNumber)) {
		streetNumber = null
	}
	String streetName = line.readString(70)
	if ("NOT SPECIFIED".equalsIgnoreCase(streetName)) {
		streetName = null
	}

	student.contact.street = [buildingName, unit, streetNumber, streetName]
			.findAll { s -> StringUtils.trimToNull(s) != null }.join(", ")

	return student
}

def import85(String rawLine, Student student) {
	def line = new InputLine(rawLine)

	// ------------------
	// client identifier p9
	line.readInteger(10)

	// ------------------
	// client title p14
	line.readString(4) // we don't store this

	// ------------------
	// client first name p8
	student.contact.firstName = line.readString(40)

	// ------------------
	// client last name p13
	student.contact.lastName = line.readString(40)

	// ------------------
	//address building/property name
	String building = line.readString(50)

	// ------------------
	//address flat/unit details
	String unit = line.readString(30)

	// address street number
	String streetNumber = line.readString(15)
	if ("NOT SPECIFIED".equalsIgnoreCase(streetNumber)) {
		streetNumber = null
	}

	// ------------------
	// address street name
	String streetName = line.readString(70)
	if ("NOT SPECIFIED".equalsIgnoreCase(streetName)) {
		streetName = null
	}

	// postal delivery box
	line.readString(22)

	student.contact.street = [building, unit, streetNumber, streetName]
			.findAll { s -> StringUtils.trimToNull(s) != null }.join(", ")

	// ------------------
	// address suburb or town or locality p4
	String suburb = line.readString(50)
	student.contact.suburb = "NOT PROVIDED".equalsIgnoreCase(suburb) ? null : suburb

	// ------------------
	// postcode p71
	// may be 0000 (unknown)
	// 0001-9999
	// OSPC (overseas)
	// @@@@ (not stated)
	student.contact.postcode = line.readString(4)

	// ------------------
	// state identifier p95
	line.readString(2) // ignore state identifier

	student.contact.homePhone = line.readString(20)
	student.contact.workPhone = line.readString(20)
	student.contact.mobilePhone = line.readString(20)

	def email = StringUtils.trimToNull(line.readString(80))
	if (email) {
		email = email.replaceFirst("\\.\$", "").replace(",", ".").replace("..", ".")
		if (ValidationUtil.isValidEmailAddress(email)) {
			student.contact.email = email
		} else {
			logger.error(String.format("Wrong email format for %s: %s", student.studentNumber, email))
		}
	}


	student.contact.email = line.readString(80)
}


Country getCountryWithCode(Integer countryCode, ObjectContext context) throws Exception {
	Expression expr = Country.SACC_CODE.eq(countryCode)
	List<Country> countries = context.select(SelectQuery.query(Country.class, expr))
	if (countries.size() != 1) {
		throw new Exception("Found ${countries.size()} countries with code ${countryCode}.")
	}
	return countries.get(0)
}


class InputLine {
	private static final Logger logger = LogManager.getLogger(InputLine)

    String text
    int position

	/**
	 * @param text
	 */
    InputLine(String text) {
		this.text = text
		this.position = 0
	}

	/**
	 * @param length
	 * @return String
	 */
    String readString(int length) {

		if (this.text.length() < this.position + length) {
			logger.debug("Tried to retrieve {} bytes, but not enough left.", length)
			return null
		}
		String value = this.text.substring(this.position, this.position + length).trim()

		this.position += length
		char[] nullString = new char[length]
		Arrays.fill(nullString, '@' as char)
		if (value.compareTo(new String(nullString)) == 0) {
			return null
		}
		return value
	}

	/**
	 * @param length
	 * @return Integer
	 */
    Integer readInteger(int length) {
		String value = readString(length)
		if (value == null) {
			return null
		}

		try {
			return new Integer(value)
		} catch (Exception e) {
			return null
		}
	}

	/**
	 * @param length
	 * @return Date
	 */
    Date readDate(int length) {

		String value = readString(length)
		if (value == null) {
			return null
		}
		if (value.length() != 8) {
			logger.warn("AVETMISS dates must be 8 characters. Got: '{}'", value)
			return null
		}

		return DateFormatter.formatDateToNoon(Date.parse("ddMMyyyy", value))
	}

    LocalDate readLocalDate(int length) {

		String value = readString(length)
		if (value == null) {
			return null
		}
		if (value.length() != 8) {
			logger.warn("AVETMISS dates must be 8 characters. Got: '{}'", value)
			return null
		}

		return LocalDate.parse(value, DateTimeFormatter.ofPattern("ddMMyyyy"))
	}
}

