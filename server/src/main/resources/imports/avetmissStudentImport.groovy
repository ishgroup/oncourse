import ish.common.types.AvetmissStudentIndigenousStatus
import ish.common.types.AvetmissStudentLabourStatus
import ish.common.types.AvetmissStudentSchoolLevel
import ish.common.types.UsiStatus
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Country
import ish.oncourse.server.cayenne.Language
import ish.oncourse.server.cayenne.Student
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import static ish.oncourse.server.api.v1.function.ContactFunctions.isValidEmailAddress

logger = LogManager.getLogger(getClass())

List<String> validationResult = new ArrayList<>()
Map<String, Student> importedStudents = new HashMap<>()

import80(new String(avetmiss80), importedStudents, context, validationResult)
import85(new String(avetmiss85), importedStudents, context, validationResult)

if (!validationResult.isEmpty()) {
	throw new RuntimeException(validationResult.join("\n"))
}

context.commitChanges()



def import80(String data, Map<String, Student> importedStudents, ObjectContext context, List<String> validationResult) {
	def lineNumber = 0
	data.eachLine { rawLine ->
		def line = new InputLine(rawLine)

		Contact aContact = context.newObject(Contact)
		Student aStudent = context.newObject(Student)

		aStudent.contact = aContact
		aContact.isStudent = true

		// ------------------
		// client identifier p9
		// Unique per college.
		String clientId = line.readString(10)
		if (clientId == null || clientId.trim().empty) {
			validationResult.add("AVETMISS-80: record at line " + (lineNumber + 1) + " doesn't contain student client identifier")
		}
		importedStudents.put(clientId, aStudent)

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
				aStudent.contact.lastName = studentNames[0].trim()
			}
			if (studentNames.length > 1) {
				String givenName = studentNames[1].trim()
				if (givenName.indexOf(' ') > 0) {
					String[] givenNames = givenName.split(" ", 2)
					aStudent.contact.firstName = givenNames[0].trim()
					aStudent.contact.middleName = givenNames[1].trim()
				} else {
					aStudent.contact.firstName = givenName
				}
			}
		} else {
			validationResult.add("AVETMISS-80: record at line ${lineNumber + 1} doesn't contain student name")
		}

		// ------------------
		// highest school level completed p43
		Integer highestSchoolLevel = line.readInteger(2)

		if (highestSchoolLevel == null) {
			aStudent.highestSchoolLevel = AvetmissStudentSchoolLevel.DEFAULT_POPUP_OPTION
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
			aStudent.highestSchoolLevel = schoolLevel
		}

		// ------------------
		// sex p94
		// "F", "M" or "@"
		String gender = line.readString(1)
		aStudent.contact.gender = "M".equals(gender) ? Gender.MALE : "F".equals(gender) ? Gender.FEMALE : "X".equals(gender) ? Gender.OTHER_GENDER : null
		// ------------------
		// date of birth p26
		aStudent.contact.birthDate = line.readLocalDate(8)
		// ------------------
		// postcode p71
		// may be 0000 (unknown)
		// 0001-9999
		// OSPC (overseas)
		// @@@@ (not stated)
		aStudent.contact.postcode = line.readString(4)
		// ------------------
		// indigenous status identifier p46
		// 1 (aboriginal)
		// 2 (Torres Strait)
		// 3 (Aboriginal and Torres Strait)
		// 4 (neither)
		// @ (not stated)
		Integer indStatus = line.readInteger(1)
		if (indStatus != null && indStatus <= 4 && indStatus >= 0) {
			aStudent.indigenousStatus =
					(AvetmissStudentIndigenousStatus) EnumUtil.enumForDatabaseValue(AvetmissStudentIndigenousStatus, indStatus)
		} else {
			aStudent.indigenousStatus = AvetmissStudentIndigenousStatus.DEFAULT_POPUP_OPTION
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
			aStudent.setLanguage(languageList.get(0))
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
			aStudent.labourForceStatus =
					(AvetmissStudentLabourStatus) EnumUtil.enumForDatabaseValue(AvetmissStudentLabourStatus, labourStatus)
		} else {
			aStudent.labourForceStatus = AvetmissStudentLabourStatus.DEFAULT_POPUP_OPTION
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
		aStudent.countryOfBirth = aCountry
		// ------------------
		// disability flag p30
		// Y/N/@
		String disabilityType = line.readString(1)

		aStudent.disabilityType = "Y".equals(disabilityType) ? AvetmissStudentDisabilityType.OTHER
				: AvetmissStudentDisabilityType.DEFAULT_POPUP_OPTION
		// ------------------
		// prior educational achievement flag p75
		// Y/N/@
		// beyond year 12 (sort of)
		String priorEducationCode = line.readString(1)

		aStudent.priorEducationCode = "Y".equals(priorEducationCode) ? AvetmissStudentPriorEducation.MISC
				: AvetmissStudentPriorEducation.DEFAULT_POPUP_OPTION
		// ------------------
		// at school p7
		// still at secondary school
		String stillAtSchool = line.readString(1)

		aStudent.isStillAtSchool = "Y".equals(stillAtSchool) ? Boolean.TRUE : "N".equals(stillAtSchool) ? Boolean.FALSE : null
		// ------------------
		// address suburb or town or locality p4
		String suburb = line.readString(50)
		aStudent.contact.suburb = "NOT PROVIDED".equalsIgnoreCase(suburb) ? null : suburb
		// ------------------
		// Unique student identifier
		String usi = line.readString(10)
		switch (usi) {
			case "INTOFF":
				aStudent.usiStatus = UsiStatus.INTERNATIONAL
				break
			case "INDIV":
				aStudent.usiStatus = UsiStatus.EXEMPTION
				break
			default:
				aStudent.usiStatus = UsiStatus.DEFAULT_NOT_SUPPLIED
				aStudent.usi = usi
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

		aStudent.contact.street = [buildingName, unit, streetNumber, streetName]
				.findAll { s -> StringUtils.trimToNull(s) != null }.join(", ")

		// ------------------
		// end of line
		lineNumber++
	}
}

def import85(String data, Map<String, Student> importedStudents, ObjectContext context, List<String> validationResult) {
	def lineNumber = 0
	data.eachLine { rawLine ->
		def line = new InputLine(rawLine)

		// ------------------
		// client identifier p9
		// Unique per college.
		String clientId = line.readString(10)

		if (clientId == null || clientId.trim().empty) {
			validationResult.add("AVETMISS-85: record at line ${lineNumber + 1} doesn't contain student client identifier")
		}
		Student aStudent = importedStudents.get(clientId)
		if (aStudent == null) {
			Contact aContact = (Contact) context.newObject(Contact.class)
			aStudent = context.newObject(Student.class)

			aContact.student = aStudent
			aContact.isStudent = true
		}

		// ------------------
		// client title p14
		line.readString(4) // we don't store this

		// ------------------
		// client first name p8
		aStudent.contact.firstName = line.readString(40)

		// ------------------
		// client last name p13
		aStudent.contact.lastName = line.readString(40)

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

		aStudent.contact.street = [building, unit, streetNumber, streetName]
				.findAll { s -> StringUtils.trimToNull(s) != null}.join(", ")

		// ------------------
		// address suburb or town or locality p4
		String suburb = line.readString(50)
		aStudent.contact.suburb = "NOT PROVIDED".equalsIgnoreCase(suburb) ? null : suburb

		// ------------------
		// postcode p71
		// may be 0000 (unknown)
		// 0001-9999
		// OSPC (overseas)
		// @@@@ (not stated)
		aStudent.contact.postcode = line.readString(4)

		// ------------------
		// state identifier p95
		line.readString(2) // ignore state identifier

		aStudent.contact.homePhone = line.readString(20)
		aStudent.contact.workPhone = line.readString(20)
		aStudent.contact.mobilePhone = line.readString(20)
		String email = line.readString(80)
		aStudent.contact.email = !isValidEmailAddress(email) ? email : ""

		// ------------------
		// end of line

		lineNumber++
	}
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

	private String text
	private int position

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
