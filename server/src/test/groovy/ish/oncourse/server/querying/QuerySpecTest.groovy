package ish.oncourse.server.querying

import groovy.transform.CompileStatic
import ish.DatabaseOperation
import ish.TestWithDatabase
import ish.DatabaseSetup
import ish.oncourse.server.cayenne.Script
import ish.oncourse.server.scripting.GroovyScriptService
import ish.oncourse.server.scripting.ScriptParameters
import ish.scripting.ScriptResult
import org.apache.cayenne.ObjectContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@CompileStatic
@DatabaseSetup(readOnly =  true, value = "ish/oncourse/server/querying/DataSet.xml")
class QuerySpecTest extends TestWithDatabase {

    static Collection<Arguments> values() {
        def data = [
                
        //Query rules:
        //1) Date:
        // - range inclusive                
        ["CourseClass", "Date range inclusive", "[200, 202, 220, 222, 223, 224]", "startDateTime in 2018-03-15 .. 2018-03-20"],
        ["CourseClass", "Date range inclusive", "[200, 202, 220, 222, 223, 224]", "startDateTime in 15/03/2018 .. 20/03/2018"],

        // - from date and after inclusive
        ["CourseClass", "After date inclusive", "[200, 202, 220, 222, 223, 224]", "startDateTime in 2018-03-15 .. *"],
        ["CourseClass", "After date inclusive", "[200, 202, 220, 222, 223, 224]", "startDateTime >= 2018-03-15"],

        // - to date and before inclusive
        ["CourseClass", "Before date inclusive", "[200, 203, 221, 222, 224, 240, 241]", "startDateTime in * .. 2018-03-15"],
        ["CourseClass", "Before date inclusive", "[200, 203, 221, 222, 224, 240, 241]", "startDateTime <= 2018-03-15"],

        // - from date and after exclusive
        ["CourseClass", "After date exclusive", "[202, 220, 223]", "startDateTime after 2018-03-15"],
        ["CourseClass", "After date exclusive", "[202, 220, 223]", "startDateTime > 2018-03-15"],

        // - to date and before exclusive
        ["CourseClass", "Before date exclusive", "[203, 221, 240, 241]", "startDateTime before 2018-03-15"],
        ["CourseClass", "Before date exclusive", "[203, 221, 240, 241]", "startDateTime < 2018-03-15"],

        // - range inclusive for DB date without time data types
        ["Contact", "Date range inclusive for DB date without time data types", "[9, 27, 28, 29]", "birthDate in 2000-01-01 .. 2000-01-30"],
        ["Contact", "Date range inclusive for DB date without time data types", "[9, 27, 28, 29]", "birthDate in 1/01/2000 .. 30/01/2000"],
        ["Contact", "Date range inclusive for DB date without time data types", "[9, 27, 28, 29]", "birthDate in 2000-01-01 .. 2000-01-30"],

        // - from date and after inclusive for DB date without time data types
        ["Contact", "After date inclusive for DB date without time data types", "[5, 20, 26]", "birthDate in 2002-03-09 .. *"],
        ["Contact", "After date inclusive for DB date without time data types", "[5, 20, 26]", "birthDate >= 2002-03-09"],

        // - to date and before inclusive for DB date without time data types
        ["Contact", "Before date inclusive for DB date without time data types", "[3, 10, 25]", "birthDate in * .. 1956-08-02"],
        ["Contact", "Before date inclusive for DB date without time data types", "[3, 10, 25]", "birthDate <= 1956-08-02"],

        // - from date and after exclusive for DB date without time data types
        ["Contact", "After date exclusive for DB date without time data types", "[5, 26]", "birthDate after 2002-03-09"],
        ["Contact", "After date exclusive for DB date without time data types", "[5, 26]", "birthDate > 2002-03-09"],

        // - to date and before exclusive for DB date without time data types
        ["Contact", "Before date exclusive for DB date without time data types", "[3, 10]", "birthDate before 1956-08-02"],
        ["Contact", "Before date exclusive for DB date without time data types", "[3, 10]", "birthDate < 1956-08-02"],

        // - definite day
        ["CourseClass", "Definite day", "[200, 222, 224]", "startDateTime = 2018-03-15"],
        ["CourseClass", "Definite day", "[200, 222, 224]", "startDateTime is 2018-03-15"],

        // - range with time
        ["CourseClass", "Date range with time", "[220, 222, 223, 224]", "startDateTime in 2018-03-15 09:00 .. 2018-03-20 19:00"],
        ["CourseClass", "Date range with time", "[220, 222, 223, 224]", "startDateTime in 15/03/2018 9:00 am .. 20/03/2018 7:00 PM"],
        ["CourseClass", "Date range with time", "[220, 222, 223, 224]", "startDateTime in 2018-03-15 09:00 .. 2018-03-20 19:00"],

        // - range with time of intervals of a day (from 8.00 to 19.00],
        ["CourseClass", "Time range of one day correct syntax", "[222]", "startDateTime in 2018-03-15 08:00 .. 2018-03-15 19:00"],
        ["CourseClass", "Time range of one day correct syntax", "[222]", "startDateTime in 15/03/2018 8:00 a.m. .. 15/03/2018 7:00 P.M."],
        ["CourseClass", "Time range of one day correct syntax", "[222]", "startDateTime in 2018-03-15 08:00 .. 2018-03-15 19:00"],


        // 2], Enum
        // - match one enum
        ["Enrolment", "Match one enum correct syntax", "[200, 201, 202, 203, 204]", "confirmationStatus is NOT_SENT"],
        ["Enrolment", "Match one enum correct syntax", "[200, 201, 202, 203, 204]", "confirmationStatus = NOT_SENT"],
        ["Enrolment", "Match one enum correct syntax", "[200, 201, 202, 203, 204]", "confirmationStatus = 1"],

        // - match any of enums
        ["Enrolment", "Match any of enums correct syntax", "[200, 201, 202, 203, 204, 237, 238, 240, 241, 242, 243]", "confirmationStatus in 1, 3"],
        ["Enrolment", "Match any of enums correct syntax", "[200, 201, 202, 203, 204, 237, 238, 240, 241, 242, 243]", "confirmationStatus in NOT_SENT, DO_NOT_SEND"],
        ["Enrolment", "Match any of enums correct syntax", "[200, 201, 202, 203, 204, 237, 238, 240, 241, 242, 243]", "confirmationStatus in NOT_SENT, DO_NOT_SEND"],

        // - not match enum
        ["Enrolment", "Not Match enum correct syntax", "[200, 201, 202, 203, 204, 237, 238, 240, 241, 242, 243]", "confirmationStatus != SENT"],
        ["Enrolment", "Not Match enum correct syntax", "[200, 201, 202, 203, 204, 237, 238, 240, 241, 242, 243]", "confirmationStatus != 2"],
        ["Enrolment", "Not Match enum correct syntax", "[200, 201, 202, 203, 204, 237, 238, 240, 241, 242, 243]", "confirmationStatus not is SENT"],

        // - not match any of enums
        ["Enrolment", "Not Match any of enums correct syntax", "[237, 238, 240, 241, 242, 243]", "confirmationStatus not NOT_SENT, SENT"], 
        ["Enrolment", "Not Match any of enums correct syntax", "[237, 238, 240, 241, 242, 243]", "confirmationStatus not (NOT_SENT, SENT)"],
        
        // 3) Num,,
        // - definite num
        ["Invoice", "By definite number", "[222]", "amountOwing = 1320"],
        ["Invoice", "By defnite number", "[222]", "amountOwing is 1320"],
        ["Invoice", "By definite number", "[222]", "amountOwing = '1320'"],
        ["Invoice", "By definite number", "[222]", "amountOwing is '1320'"],

        // - definite float number
        ["Invoice", "By definite float number", "[201]", "amountOwing = 1320.01"],
        ["Invoice", "By definite float number", "[201]", "amountOwing is 1320.01"],
        ["Invoice", "By definite float number", "[201]", "amountOwing = '1320.01'"],
        ["Invoice", "By definite float number", "[201]", "amountOwing is '1320.01'"],

        // - not definite number
        ["Invoice", "Except definite number", "[200, 201, 220, 221, 223, 240]", "amountOwing != 1320"],
        ["Invoice", "Except definite number", "[200, 201, 220, 221, 223, 240]", "amountOwing not is 1320"],
        ["Invoice", "Except definite number", "[200, 201, 220, 221, 223, 240]", "amountOwing != '1320'"],
        ["Invoice", "Except definite number", "[200, 201, 220, 221, 223, 240]", "amountOwing not is '1320'"],

        // - number list
        ["Invoice", "By number list", "[222, 223, 240]", "amountOwing in 440, 1320, 550"],
        ["Invoice", "By number list", "[222, 223, 240]", "amountOwing in 440, 1320, 550"],

        // - not number list
        ["Invoice", "By number list", "[200, 201, 220, 221]", "amountOwing not 440, 1320, 550"],

        // - greater
        ["Invoice", "Greater than number", "[201]", "amountOwing after 1320"],
        ["Invoice", "Greater than number", "[201]", "amountOwing > 1320"],

        // - greater or equals
        ["Invoice", "Greater or equals to number", "[201, 222]", "amountOwing >= 1320"],

        // - less
        ["Invoice", "Less than number", "[200, 220, 221, 223, 240]", "amountOwing before 1320"],
        ["Invoice", "Less than number", "[200, 220, 221, 223, 240]", "amountOwing < 1320"],

        // - less or equal
        ["Invoice", "Less or equals to number", "[200, 220, 221, 222, 223, 240]", "amountOwing <= 1320"],

        // 4) String
        // - contains char
        ["Qualification", "Contains char sequence", "[4138, 8058]", "level contains 'IV'"],

        // - not contains
        ["Qualification", "Not contains char sequence", "[1372, 10082, 10655, 12085]", "level not contains 'IV'"],

        // - starts with
        ["Qualification", "Starts with char sequence", "[4138, 8058, 10082, 10655, 12085]", "level starts with 'Certificate'"],

        // - not starts with
        ["Qualification", "Not starts with char sequence", "[1372]", "level not starts with 'Certificate'"],

        // - ends with
        ["Qualification", "Ends with char sequence", "[4138, 8058]", "level ends with 'IV in'"],

        // - not ends with
        ["Qualification", "Not ends with char sequence", "[1372, 10082, 10655, 12085]", "level not ends with 'IV in'"],

        // - match
        ["Qualification", "Match char sequence", "[4138, 8058]", "level is 'Certificate IV in'"],
        ["Qualification", "Match char sequence", "[4138, 8058]", "level = 'Certificate IV in'"],

        // - not match
        ["Qualification", "Not match char sequence", "[1372, 10082, 10655, 12085]", "level not is 'Certificate IV in'"],

        // 5) boolean
        // - true
        ["Contact", "Boolean true condition correct syntax", "[2, 3, 4, 5, 17, 18, 20, 24, 27, 28, 29]", "gender is  MALE", "gender = MALE"],
        ["Contact", "Boolean true condition correct syntax", "[2, 3, 4, 5, 17, 18, 20, 24, 27, 28, 29]", "gender != FEMALE"],
        ["Contact", "Boolean true condition correct syntax", "[2, 3, 4, 5, 17, 18, 20, 24, 27, 28, 29]", "gender not is FEMALE"],

        // - false
        ["Contact", "Boolean false condition correct syntax", "[6, 7, 15, 16, 19, 21, 23]", "gender = FEMALE"],
        ["Contact", "Boolean false condition correct syntax", "[6, 7, 15, 16, 19, 21, 23]", "gender is FEMALE"],
        ["Contact", "Boolean false condition correct syntax", "[6, 7, 15, 16, 19, 21, 23]", "gender = FEMALE"],
        ["Contact", "Boolean false condition correct syntax", "[6, 7, 15, 16, 19, 21, 23]", "gender != MALE"],
        ["Contact", "Boolean false condition correct syntax", "[6, 7, 15, 16, 19, 21, 23]", "gender not is MALE"],

        // 6) All types
        // - null
        ["Qualification", "Null or empty string condition correct syntax", "[12085]", "newApprenticeship = null", "newApprenticeship is null"],

        // - NOT null
        ["Qualification", "Not null or empty string condition correct syntax", "[1372, 4138, 8058, 10082, 10655]", "newApprenticeship not is null"],
        ["Qualification", "Not null or empty string condition correct syntax", "[1372, 4138, 8058, 10082, 10655]", "newApprenticeship != null"],

        //two booleans in query
        ["Contact", "two boolean in query", "[6, 7, 15, 16, 19, 21, 23]", "isStudent is true and gender = FEMALE"],

        //by tag
        ["CourseClass", "By tag", "[201]", "#ClassWithoutSpaceTag"],
        ["CourseClass", "By not courseClass tag", "[203, 220]", "#'Course With space Tag'"],
        ["Course", "By tag", "[205]", "#CourseWithoutSpaceTag"],
        ["CourseClass", "By tag with spaces", "[202]", "#'Class With space Tag'"],
        ["Course", "By tag with spaces", "[202]", "#'Course With space Tag'"],

        //by related entity's tag
        ["CourseClass", "By Course tag", "[202, 250]", "course #CourseWithoutSpaceTag"],
        ["CourseClass", "By Course tag with spaces", "[203, 220]", "course #'Course With space Tag'"],

        //by customField
        ["Contact", "By customField", "[3]", "someField1 = 'value10'"],
        ["Contact", "By customField", "[3]", "someField1 is 'value10'"],
        ["Contact", "By customField", "[3]", "someField1 = value10"],
        ["Contact", "By customField", "[3]", "someField1 is value10"],
        ["Contact", "By customField", "[4]", "someField1 = 'value11'"],
        ["Contact", "By customField", "[]", "someField1 is 'mandatoryText'"],
        ["Contact", "By customField", "[]", "someField1 = 'mandatoryText'"],

        //entity by contact's firstname and lastname
        ["Student", "Entity by contact's name, firstName first", "[210]", "contact ~ ',Lei'"],
        ["Student", "Entity by contact's name, firstName first", "[210]", "contact ~ ',Leigh'"],
        ["Student", "Entity by contact's name, firstName first", "[210]", "contact ~ 'Leigh '"],
        ["Student", "Entity by contact's name, firstName first", "[210]", "contact ~ 'Leigh Stevens'"],
        ["Student", "Entity by contact's name, firstName first", "[210]", "contact ~ 'Leigh Stev'"],
        ["Student", "Entity by contact's name, lastName first", "[210]", "contact ~ 'Stevens'"],
        ["Student", "Entity by contact's name, lastName first", "[210]", "contact ~ 'Stevens, '"],
        ["Student", "Entity by contact's name, lastName first", "[210]", "contact ~ 'Stevens, Leigh'"],
        ["Student", "Entity by contact's name, lastName first", "[210]", "contact ~ 'Stevens, Le'"],

        //contact by firstname and lastname
        ["Contact", "By name, firstName first", "[2]", "~ ',Lei'"],
        ["Contact", "By name, firstName first", "[2]", "~ ',Leigh'"],
        ["Contact", "By name, firstName first", "[2]", "~ 'Leigh '"],
        ["Contact", "By name, firstName first", "[2]", "~ 'Leigh Stevens'"],
        ["Contact", "By name, firstName first", "[2]", "~ 'Leigh Stev'"],
        ["Contact", "By name, lastName first", "[2]", "~ 'Stevens'"],
        ["Contact", "By name, lastName first", "[2]", "~ 'Stevens, '"],
        ["Contact", "By name, lastName first", "[2]", "~ 'Stevens, Leigh'"],
        ["Contact", "By name, lastName first", "[2]", "~ 'Stevens, Le'"],

        ["Contact", "By company name, string with spaces", "[30]", "~ 'The Co'"],

        //complex query
        ["Enrolment", "Complex query", "[200, 202, 203, 204]", "confirmationStatus is NOT_SENT and (student.contact ~ 'Rod ' or student.studentNumber > 5)"]
                ]

        Collection<Arguments> resultData = new ArrayList<>()
        for (List<String> test : data) {
            resultData.add(Arguments.of(test[0], test[1], test[2], test[3], test[4], test[5]))
        }
        return resultData
    }


    @ParameterizedTest(name = "{1}: {3}")
    @MethodSource("values")
    void testRunQueryClosure(String entity, String description, String expectedResult, String query) throws Exception {
        ObjectContext context = cayenneService.newContext
        GroovyScriptService scriptService = injector.getInstance(GroovyScriptService)

        Script script = context.newObject(Script.class)
        script.setEnabled(true)

        script.setScript("""
                           def result = query {
                               entity "${entity}"
                               query "${query}"
                        }

                           result*.id.sort()
                        """)

        ScriptResult result = scriptService.runScript(script, ScriptParameters.empty(), cayenneService.getNewContext())

        if (result.getType() == ScriptResult.ResultType.SUCCESS)
            Assertions.assertEquals(expectedResult, result.getResultValue().toString(), description)
        else
            Assertions.fail("Incorrect syntax: " + result.error)
    }
}
