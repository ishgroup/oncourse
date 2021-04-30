package ish.oncourse.server.querying

import ish.CayenneIshTestCase
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.Script
import ish.oncourse.server.scripting.GroovyScriptService
import ish.oncourse.server.scripting.ScriptParameters
import ish.scripting.ScriptResult
import org.apache.cayenne.ObjectContext
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.BeforeClass
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import static org.junit.Assert.assertEquals
import static org.junit.Assert.fail

@RunWith(value = Parameterized)
class QuerySpecTest extends CayenneIshTestCase {

    private static ICayenneService cayenneService
    private String query
    private String entity
    private String result
    private String description

    QuerySpecTest(String entity, String description, String result, String query) {
        this.query = query
        this.entity = entity
        this.result = result
        this.description = description
    }

    @BeforeClass
    static void setup() throws Exception {
        wipeTables()
        cayenneService = injector.getInstance(ICayenneService)

        InputStream st = QuerySpecTest.getClassLoader().getResourceAsStream("ish/oncourse/server/querying/DataSet.xml")

        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder()
        builder.setColumnSensing(true)
        FlatXmlDataSet dataSet = builder.build(st)

        ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)
        rDataSet.addReplacementObject("[null]", null)

        executeDatabaseOperation(rDataSet)
    }


    @Parameterized.Parameters(name = "{1}: {3}")
    static Collection<String[]> data() {
        List<String[]> result = new ArrayList<>()

//Query rules:
    //1) Date:
        // - range inclusive
        String[] dateRangeInclusive = ["CourseClass", "Date range inclusive", "[200, 202, 220, 222, 223, 224]"]
        result.add(dateRangeInclusive + "startDateTime in 2018-03-15 .. 2018-03-20")
        result.add(dateRangeInclusive + "startDateTime in 15/03/2018 .. 20/03/2018")
        result.add(dateRangeInclusive + "startDateTime in 2018-03-15 .. 2018-03-20")

        // - from date and after inclusive
        String[] fromDateInclusive = ["CourseClass", "After date inclusive", "[200, 202, 220, 222, 223, 224]"]
        result.add(fromDateInclusive + "startDateTime in 2018-03-15 .. *")
        result.add(fromDateInclusive + "startDateTime >= 2018-03-15")

        // - to date and before inclusive
        String[] beforeDateInclusive = ["CourseClass", "Before date inclusive", "[200, 203, 221, 222, 224, 240, 241]"]
        result.add(beforeDateInclusive + "startDateTime in * .. 2018-03-15")
        result.add(beforeDateInclusive + "startDateTime <= 2018-03-15")

        // - from date and after exclusive
        String[] fromDateExclusive = ["CourseClass", "After date exclusive", "[202, 220, 223]"]
        result.add(fromDateExclusive + "startDateTime after 2018-03-15")
        result.add(fromDateExclusive + "startDateTime > 2018-03-15")

        // - to date and before exclusive
        String[] beforeDateExclusive = ["CourseClass", "Before date exclusive", "[203, 221, 240, 241]"]
        result.add(beforeDateExclusive + "startDateTime before 2018-03-15")
        result.add(beforeDateExclusive + "startDateTime < 2018-03-15")

        // - range inclusive for DB date without time data types
        String[] dateRangeInclusiveForDbDateWithoutTimeDataType = ["Contact", "Date range inclusive for DB date without time data types", "[9, 27, 28, 29]"]
        result.add(dateRangeInclusiveForDbDateWithoutTimeDataType + "birthDate in 2000-01-01 .. 2000-01-30")
        result.add(dateRangeInclusiveForDbDateWithoutTimeDataType + "birthDate in 1/01/2000 .. 30/01/2000")
        result.add(dateRangeInclusiveForDbDateWithoutTimeDataType + "birthDate in 2000-01-01 .. 2000-01-30")

        // - from date and after inclusive for DB date without time data types
        String[] fromDateInclusiveForDbDateWithoutTimeDataType = ["Contact", "After date inclusive for DB date without time data types", "[5, 20, 26]"]
        result.add(fromDateInclusiveForDbDateWithoutTimeDataType + "birthDate in 2002-03-09 .. *")
        result.add(fromDateInclusiveForDbDateWithoutTimeDataType + "birthDate >= 2002-03-09")

        // - to date and before inclusive for DB date without time data types
        String[] beforeDateInclusiveForDbDateWithoutTimeDataType = ["Contact", "Before date inclusive for DB date without time data types", "[3, 10, 25]"]
        result.add(beforeDateInclusiveForDbDateWithoutTimeDataType + "birthDate in * .. 1956-08-02")
        result.add(beforeDateInclusiveForDbDateWithoutTimeDataType + "birthDate <= 1956-08-02")

        // - from date and after exclusive for DB date without time data types
        String[] fromDateExclusiveForDbDateWithoutTimeDataType = ["Contact", "After date exclusive for DB date without time data types", "[5, 26]"]
        result.add(fromDateExclusiveForDbDateWithoutTimeDataType + "birthDate after 2002-03-09")
        result.add(fromDateExclusiveForDbDateWithoutTimeDataType + "birthDate > 2002-03-09")

        // - to date and before exclusive for DB date without time data types
        String[] beforeDateExclusiveForDbDateWithoutTimeDataType = ["Contact", "Before date exclusive for DB date without time data types", "[3, 10]"]
        result.add(beforeDateExclusiveForDbDateWithoutTimeDataType + "birthDate before 1956-08-02")
        result.add(beforeDateExclusiveForDbDateWithoutTimeDataType + "birthDate < 1956-08-02")

        // - definite day
        String[] definiteDay = ["CourseClass", "Definite day", "[200, 222, 224]"]
        result.add(definiteDay + "startDateTime = 2018-03-15")
        result.add(definiteDay + "startDateTime is 2018-03-15")

        // - range with time
        String[] dateRangeWithTime = ["CourseClass", "Date range with time", "[220, 222, 223, 224]"]
        result.add(dateRangeWithTime + "startDateTime in 2018-03-15 09:00 .. 2018-03-20 19:00")
        result.add(dateRangeWithTime + "startDateTime in 15/03/2018 9:00 am .. 20/03/2018 7:00 PM")
        result.add(dateRangeWithTime + "startDateTime in 2018-03-15 09:00 .. 2018-03-20 19:00")

        // - range with time of intervals of a day (from 8.00 to 19.00)
        String[] rangeWithTimeOfDay = ["CourseClass", "Time range of one day correct syntax", "[222]"]
        result.add(rangeWithTimeOfDay + "startDateTime in 2018-03-15 08:00 .. 2018-03-15 19:00")
        result.add(rangeWithTimeOfDay + "startDateTime in 15/03/2018 8:00 a.m. .. 15/03/2018 7:00 P.M.")
        result.add(rangeWithTimeOfDay + "startDateTime in 2018-03-15 08:00 .. 2018-03-15 19:00")


    // 2) Enum
        // - match one enum
        String[] correctOneEnum = ["Enrolment", "Match one enum correct syntax", "[200, 201, 202, 203, 204]"]
        result.add(correctOneEnum + "confirmationStatus is NOT_SENT")
        result.add(correctOneEnum + "confirmationStatus = NOT_SENT")
        result.add(correctOneEnum + "confirmationStatus = 1")

        // - match any of enums
        String[] correctAnyEnum = ["Enrolment", "Match any of enums correct syntax", "[200, 201, 202, 203, 204, 237, 238, 240, 241, 242, 243]"]
        result.add(correctAnyEnum + "confirmationStatus in 1, 3")
        result.add(correctAnyEnum + "confirmationStatus in NOT_SENT, DO_NOT_SEND")
        result.add(correctAnyEnum + "confirmationStatus in NOT_SENT, DO_NOT_SEND")

        // - not match enum
        String[] correctNotMatchEnum = ["Enrolment", "Not Match enum correct syntax", "[200, 201, 202, 203, 204, 237, 238, 240, 241, 242, 243]"]
        result.add(correctNotMatchEnum + "confirmationStatus != SENT")
        result.add(correctNotMatchEnum + "confirmationStatus != 2")
        result.add(correctNotMatchEnum + "confirmationStatus not is SENT")

        // - not match any of enums
        String[] correctNotAnyOfEnums = ["Enrolment", "Not Match any of enums correct syntax", "[237, 238, 240, 241, 242, 243]"]
        result.add(correctNotAnyOfEnums + "confirmationStatus not NOT_SENT, SENT")
        result.add(correctNotAnyOfEnums + "confirmationStatus not (NOT_SENT, SENT)")

    // 3) Number
        // - definite number
        result.add(["Invoice", "By definite number", "[222]", "amountOwing = 1320"].toArray() as String[])
        result.add(["Invoice", "By definite number", "[222]", "amountOwing is 1320"].toArray() as String[])
        result.add(["Invoice", "By definite number", "[222]", "amountOwing = '1320'"].toArray() as String[])
        result.add(["Invoice", "By definite number", "[222]", "amountOwing is '1320'"].toArray() as String[])

        // - definite float number
        result.add(["Invoice", "By definite float number", "[201]", "amountOwing = 1320.01"].toArray() as String[])
        result.add(["Invoice", "By definite float number", "[201]", "amountOwing is 1320.01"].toArray() as String[])
        result.add(["Invoice", "By definite float number", "[201]", "amountOwing = '1320.01'"].toArray() as String[])
        result.add(["Invoice", "By definite float number", "[201]", "amountOwing is '1320.01'"].toArray() as String[])

        // - not definite number
        result.add(["Invoice", "Except definite number", "[200, 201, 220, 221, 223, 240]", "amountOwing != 1320"].toArray() as String[])
        result.add(["Invoice", "Except definite number", "[200, 201, 220, 221, 223, 240]", "amountOwing not is 1320"].toArray() as String[])
        result.add(["Invoice", "Except definite number", "[200, 201, 220, 221, 223, 240]", "amountOwing != '1320'"].toArray() as String[])
        result.add(["Invoice", "Except definite number", "[200, 201, 220, 221, 223, 240]", "amountOwing not is '1320'"].toArray() as String[])

        // - number list
        result.add(["Invoice", "By number list", "[222, 223, 240]", "amountOwing in 440, 1320, 550"].toArray() as String[])
        result.add(["Invoice", "By number list", "[222, 223, 240]", "amountOwing in 440, 1320, 550"].toArray() as String[])

        // - not number list
        result.add(["Invoice", "By number list", "[200, 201, 220, 221]", "amountOwing not 440, 1320, 550"].toArray() as String[])

        // - greater
        result.add(["Invoice", "Greater than number", "[201]", "amountOwing after 1320"].toArray() as String[])
        result.add(["Invoice", "Greater than number", "[201]", "amountOwing > 1320"].toArray() as String[])

        // - greater or equals
        result.add(["Invoice", "Greater or equals to number", "[201, 222]", "amountOwing >= 1320"].toArray() as String[])

        // - less
        result.add(["Invoice", "Less than number", "[200, 220, 221, 223, 240]", "amountOwing before 1320"].toArray() as String[])
        result.add(["Invoice", "Less than number", "[200, 220, 221, 223, 240]", "amountOwing < 1320"].toArray() as String[])

        // - less or equal
        result.add(["Invoice", "Less or equals to number", "[200, 220, 221, 222, 223, 240]", "amountOwing <= 1320"].toArray() as String[])

    // 4) String
        // - contains char
        result.add(["Qualification", "Contains char sequence", "[4138, 8058]", "level contains 'IV'"].toArray() as String[])

        // - not contains
        result.add(["Qualification", "Not contains char sequence", "[1372, 10082, 10655, 12085]", "level not contains 'IV'"].toArray() as String[])

        // - starts with
        result.add(["Qualification", "Starts with char sequence", "[4138, 8058, 10082, 10655, 12085]", "level starts with 'Certificate'"].toArray() as String[])

        // - not starts with
        result.add(["Qualification", "Not starts with char sequence", "[1372]", "level not starts with 'Certificate'"].toArray() as String[])

        // - ends with
        result.add(["Qualification", "Ends with char sequence", "[4138, 8058]", "level ends with 'IV in'"].toArray() as String[])

        // - not ends with
        result.add(["Qualification", "Not ends with char sequence", "[1372, 10082, 10655, 12085]", "level not ends with 'IV in'"].toArray() as String[])

        // - match
        result.add(["Qualification", "Match char sequence", "[4138, 8058]", "level is 'Certificate IV in'"].toArray() as String[])
        result.add(["Qualification", "Match char sequence", "[4138, 8058]", "level = 'Certificate IV in'"].toArray() as String[])

        // - not match
        result.add(["Qualification", "Not match char sequence", "[1372, 10082, 10655, 12085]", "level not is 'Certificate IV in'"].toArray() as String[])

    // 5) boolean
        // - true
        String[] correctTrueCondition = ["Contact", "Boolean true condition correct syntax", "[2, 3, 4, 5, 17, 18, 20, 24, 27, 28, 29]"]
        result.add(correctTrueCondition + "gender is  MALE")
        result.add(correctTrueCondition + "gender = MALE")
        result.add(correctTrueCondition + "gender != FEMALE")
        result.add(correctTrueCondition + "gender not is FEMALE")

        // - false
        String[] correctFalseCondition = ["Contact", "Boolean false condition correct syntax", "[6, 7, 15, 16, 19, 21, 23]"]
        result.add(correctFalseCondition + "gender = FEMALE")
        result.add(correctFalseCondition + "gender is FEMALE")
        result.add(correctFalseCondition + "gender = FEMALE")
        result.add(correctFalseCondition + "gender != MALE")
        result.add(correctFalseCondition + "gender not is MALE")

    // 6) All types
        // - null
        String[] correctNullOrEmpty = ["Qualification", "Null or empty string condition correct syntax", "[12085]"]
        result.add(correctNullOrEmpty + "newApprenticeship = null")
        result.add(correctNullOrEmpty + "newApprenticeship is null")

        // - NOT null
        String[] correctNotNullOrEmpty = ["Qualification", "Not null or empty string condition correct syntax", "[1372, 4138, 8058, 10082, 10655]"]
        result.add(correctNotNullOrEmpty + "newApprenticeship not is null")
        result.add(correctNotNullOrEmpty + "newApprenticeship != null")

        //two booleans in query
        result.add(["Contact", "two boolean in query", "[6, 7, 15, 16, 19, 21, 23]", "isStudent is true and gender = FEMALE"].toArray() as String[])

        //by tag
        result.add(["CourseClass", "By tag", "[201]", "#ClassWithoutSpaceTag"].toArray() as String[])
        result.add(["CourseClass", "By not courseClass tag", "[203, 220]", "#'Course With space Tag'"].toArray() as String[])
        result.add(["Course", "By tag", "[205]", "#CourseWithoutSpaceTag"].toArray() as String[])
        result.add(["CourseClass", "By tag with spaces", "[202]", "#'Class With space Tag'"].toArray() as String[])
        result.add(["Course", "By tag with spaces", "[202]", "#'Course With space Tag'"].toArray() as String[])

        //by related entity's tag
        result.add(["CourseClass", "By Course tag", "[202, 250]", "course #CourseWithoutSpaceTag"].toArray() as String[])
        result.add(["CourseClass", "By Course tag with spaces", "[203, 220]", "course #'Course With space Tag'"].toArray() as String[])

        //by customField
        result.add(["Contact", "By customField", "[3]", "someField1 = 'value10'"].toArray() as String[])
        result.add(["Contact", "By customField", "[3]", "someField1 is 'value10'"].toArray() as String[])
        result.add(["Contact", "By customField", "[3]", "someField1 = value10"].toArray() as String[])
        result.add(["Contact", "By customField", "[3]", "someField1 is value10"].toArray() as String[])
        result.add(["Contact", "By customField", "[4]", "someField1 = 'value11'"].toArray() as String[])
        result.add(["Contact", "By customField", "[]", "someField1 is 'mandatoryText'"].toArray() as String[])
        result.add(["Contact", "By customField", "[]", "someField1 = 'mandatoryText'"].toArray() as String[])

        //entity by contact's firstname and lastname
        result.add(["Student", "Entity by contact's name, firstName first", "[210]", "contact ~ ',Lei'"].toArray() as String[])
        result.add(["Student", "Entity by contact's name, firstName first", "[210]", "contact ~ ',Leigh'"].toArray() as String[])
        result.add(["Student", "Entity by contact's name, firstName first", "[210]", "contact ~ 'Leigh '"].toArray() as String[])
        result.add(["Student", "Entity by contact's name, firstName first", "[210]", "contact ~ 'Leigh Stevens'"].toArray() as String[])
        result.add(["Student", "Entity by contact's name, firstName first", "[210]", "contact ~ 'Leigh Stev'"].toArray() as String[])
        result.add(["Student", "Entity by contact's name, lastName first", "[210]", "contact ~ 'Stevens'"].toArray() as String[])
        result.add(["Student", "Entity by contact's name, lastName first", "[210]", "contact ~ 'Stevens, '"].toArray() as String[])
        result.add(["Student", "Entity by contact's name, lastName first", "[210]", "contact ~ 'Stevens, Leigh'"].toArray() as String[])
        result.add(["Student", "Entity by contact's name, lastName first", "[210]", "contact ~ 'Stevens, Le'"].toArray() as String[])

        //contact by firstname and lastname
        result.add(["Contact", "By name, firstName first", "[2]", "~ ',Lei'"].toArray() as String[])
        result.add(["Contact", "By name, firstName first", "[2]", "~ ',Leigh'"].toArray() as String[])
        result.add(["Contact", "By name, firstName first", "[2]", "~ 'Leigh '"].toArray() as String[])
        result.add(["Contact", "By name, firstName first", "[2]", "~ 'Leigh Stevens'"].toArray() as String[])
        result.add(["Contact", "By name, firstName first", "[2]", "~ 'Leigh Stev'"].toArray() as String[])
        result.add(["Contact", "By name, lastName first", "[2]", "~ 'Stevens'"].toArray() as String[])
        result.add(["Contact", "By name, lastName first", "[2]", "~ 'Stevens, '"].toArray() as String[])
        result.add(["Contact", "By name, lastName first", "[2]", "~ 'Stevens, Leigh'"].toArray() as String[])
        result.add(["Contact", "By name, lastName first", "[2]", "~ 'Stevens, Le'"].toArray() as String[])

        result.add(["Contact", "By company name, string with spaces", "[30]", "~ 'The Co'"].toArray() as String[])

        //complex query
        result.add(["Enrolment", "Complex query", "[200, 202, 203, 204]", "confirmationStatus is NOT_SENT and (student.contact ~ 'Rod ' or student.studentNumber > 5)"].toArray() as String[])

        return result
    }


    @Test
    void testRunQueryClosure() throws Exception {
        ObjectContext context = cayenneService.newContext
        GroovyScriptService scriptService = injector.getInstance(GroovyScriptService)

        Script script = context.newObject(Script.class)
        script.setEnabled(true)

        script.setScript(
            "    \n" +
            "   def result = query {\n" +
            "       entity \"${entity}\"\n" +
            "       query \"${query}\"\n" +
                "}\n" +
            "\n" +
            "   result*.id.sort()")

        ScriptResult result = scriptService.runScript(script, ScriptParameters.empty(), cayenneService.getNewContext())

        if (result.getType() == ScriptResult.ResultType.SUCCESS)
            assertEquals(this.result, result.getResultValue().toString())
        else
            fail("Incorrect syntax: " + result.error)
    }
}
