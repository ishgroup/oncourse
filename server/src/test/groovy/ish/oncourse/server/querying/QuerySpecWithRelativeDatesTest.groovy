package ish.oncourse.server.querying

import ish.CayenneIshTestCase
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.Script
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.scripting.GroovyScriptService
import ish.oncourse.server.scripting.ScriptParameters
import ish.scripting.ScriptResult
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.sql.Timestamp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.temporal.TemporalAdjusters

import static java.time.LocalDateTime.now
import static java.time.LocalTime.MAX
import static java.time.LocalTime.MIN
import static org.junit.Assert.assertEquals
import static org.junit.Assert.fail

class QuerySpecWithRelativeDatesTest extends CayenneIshTestCase{
    private static ObjectContext context
    private List<Student> students


    @BeforeEach
    void setup() throws Exception {
        wipeTables()
        context = injector.getInstance(ICayenneService).newContext

        InputStream st = QuerySpecTest.getClassLoader().getResourceAsStream("ish/oncourse/server/querying/QuerySpecWithRelativeDatesTestDataSet.xml")

        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder()
        builder.setColumnSensing(true)
        FlatXmlDataSet dataSet = builder.build(st)

        ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)
        rDataSet.addReplacementObject("[null]", null)

        executeDatabaseOperation(rDataSet)

        students = ObjectSelect.query(Student).orderBy(Student.ID.asc()).select(context)
    }


    @Test
    void todayYesterdayTomorrowOperatorTest(){
        LocalDateTime today = now()
        LocalDateTime todayStart = now().with(MIN)
        LocalDateTime todayEnd = now().with(MAX).withNano(0)

        LocalDateTime yesterday = now().minusDays(1)
        LocalDateTime yesterdayStart = yesterday.with(MIN)
        LocalDateTime yesterdayEnd = yesterday.with(MAX).withNano(0)

        LocalDateTime tomorrow = now().plusDays(1)
        LocalDateTime tomorrowStart = tomorrow.with(MIN)
        LocalDateTime tomorrowEnd = tomorrow.with(MAX).withNano(0)

        students.get(0).visaExpiryDate = Timestamp.valueOf( today )
        students.get(1).visaExpiryDate = Timestamp.valueOf( todayStart )
        students.get(2).visaExpiryDate = Timestamp.valueOf( todayEnd )
        students.get(3).visaExpiryDate = Timestamp.valueOf( yesterday )
        students.get(4).visaExpiryDate = Timestamp.valueOf( yesterdayStart )
        students.get(5).visaExpiryDate = Timestamp.valueOf( yesterdayEnd )
        students.get(6).visaExpiryDate = Timestamp.valueOf( tomorrow )
        students.get(7).visaExpiryDate = Timestamp.valueOf( tomorrowStart )
        students.get(8).visaExpiryDate = Timestamp.valueOf( tomorrowEnd )

        context.commitChanges()


        executeQuery("visaExpiryDate today", "[1, 2, 3]")
        executeQuery("visaExpiryDate = today", "[1, 2, 3]")
        executeQuery("visaExpiryDate is today", "[1, 2, 3]")

        executeQuery("visaExpiryDate yesterday", "[4, 5, 6]")
        executeQuery("visaExpiryDate = yesterday", "[4, 5, 6]")
        executeQuery("visaExpiryDate is yesterday", "[4, 5, 6]")

        executeQuery("visaExpiryDate tomorrow", "[7, 8, 9]")
        executeQuery("visaExpiryDate = tomorrow", "[7, 8, 9]")
        executeQuery("visaExpiryDate is tomorrow", "[7, 8, 9]")

        executeQuery("visaExpiryDate in yesterday .. today", "[1, 2, 3, 4, 5, 6]")

    }


    @Test
    void lastThisNextWeekOperatorTest(){
        LocalDateTime lastWeek = now().minusWeeks(1)
        LocalDateTime lastWeekStart = lastWeek.with(MIN)
        LocalDateTime lastWeekEnd = lastWeek.with(MAX).withNano(0)

        LocalDateTime thisWeek = now()
        LocalDateTime thisWeekStart = now().with(MIN).with(DayOfWeek.MONDAY)
        LocalDateTime thisWeekEnd = now().with(MAX).withNano(0).with(DayOfWeek.SUNDAY)

        LocalDateTime nextWeek = now().plusWeeks(1)
        LocalDateTime nextWeekStart = nextWeek.with(MIN).with(DayOfWeek.MONDAY)
        LocalDateTime nextWeekEnd = nextWeek.with(MAX).withNano(0).with(DayOfWeek.SUNDAY)

        students.get(0).visaExpiryDate = Timestamp.valueOf( lastWeek )
        students.get(1).visaExpiryDate = Timestamp.valueOf( lastWeekStart )
        students.get(2).visaExpiryDate = Timestamp.valueOf( lastWeekEnd )
        students.get(3).visaExpiryDate = Timestamp.valueOf( thisWeek )
        students.get(4).visaExpiryDate = Timestamp.valueOf( thisWeekStart )
        students.get(5).visaExpiryDate = Timestamp.valueOf( thisWeekEnd )
        students.get(6).visaExpiryDate = Timestamp.valueOf( nextWeek )
        students.get(7).visaExpiryDate = Timestamp.valueOf( nextWeekStart )
        students.get(8).visaExpiryDate = Timestamp.valueOf( nextWeekEnd )

        context.commitChanges()


        executeQuery("visaExpiryDate last week", "[1, 2, 3]")
        executeQuery("visaExpiryDate this week", "[4, 5, 6]")
        executeQuery("visaExpiryDate next week", "[7, 8, 9]")
    }


    @Test
    void lastThisNextMonthOperatorTest(){
        LocalDateTime lastMonth = now().minusMonths(1)
        LocalDateTime lastMonthStart = lastMonth.with(MIN).withDayOfMonth(1)
        LocalDateTime lastMonthEnd = lastMonth.with(MAX).withNano(0).with(TemporalAdjusters.lastDayOfMonth())

        LocalDateTime thisMonth = now()
        LocalDateTime thisMonthStart = now().with(MIN).withDayOfMonth(1)
        LocalDateTime thisMonthEnd = now().with(MAX).withNano(0).with(TemporalAdjusters.lastDayOfMonth())

        LocalDateTime nextMonth = now().plusMonths(1)
        LocalDateTime nextMonthStart = nextMonth.with(MIN).withDayOfMonth(1)
        LocalDateTime nextMonthEnd = nextMonth.with(MAX).withNano(0).with(TemporalAdjusters.lastDayOfMonth())

        students.get(0).visaExpiryDate = Timestamp.valueOf( lastMonth )
        students.get(1).visaExpiryDate = Timestamp.valueOf( lastMonthStart )
        students.get(2).visaExpiryDate = Timestamp.valueOf( lastMonthEnd )
        students.get(3).visaExpiryDate = Timestamp.valueOf( thisMonth )
        students.get(4).visaExpiryDate = Timestamp.valueOf( thisMonthStart )
        students.get(5).visaExpiryDate = Timestamp.valueOf( thisMonthEnd )
        students.get(6).visaExpiryDate = Timestamp.valueOf( nextMonth )
        students.get(7).visaExpiryDate = Timestamp.valueOf( nextMonthStart )
        students.get(8).visaExpiryDate = Timestamp.valueOf( nextMonthEnd )

        context.commitChanges()

        executeQuery("visaExpiryDate last month", "[1, 2, 3]")
        executeQuery("visaExpiryDate this month", "[4, 5, 6]")
        executeQuery("visaExpiryDate next month", "[7, 8, 9]")
    }


    @Test
    void lastThisNextYearOperatorTest(){
        LocalDateTime lastYear = now().minusYears(1)
        LocalDateTime lastYearStart = lastYear.with(MIN).with(Month.JANUARY).withDayOfMonth(1)
        LocalDateTime lastYearEnd = lastYear.with(MAX).withNano(0).with(Month.DECEMBER).with(TemporalAdjusters.lastDayOfMonth())

        LocalDateTime thisYear = now()
        LocalDateTime thisYearStart = now().with(MIN).with(Month.JANUARY).withDayOfMonth(1)
        LocalDateTime thisYearEnd = now().with(MAX).withNano(0).with(Month.DECEMBER).with(TemporalAdjusters.lastDayOfMonth())

        LocalDateTime nextYear = now().plusYears(1)
        LocalDateTime nextYearStart = nextYear.with(MIN).with(Month.JANUARY).withDayOfMonth(1)
        LocalDateTime nextYearEnd = nextYear.with(MAX).withNano(0).with(Month.DECEMBER).with(TemporalAdjusters.lastDayOfMonth())

        students.get(0).visaExpiryDate = Timestamp.valueOf( lastYear )
        students.get(1).visaExpiryDate = Timestamp.valueOf( lastYearStart )
        students.get(2).visaExpiryDate = Timestamp.valueOf( lastYearEnd )
        students.get(3).visaExpiryDate = Timestamp.valueOf( thisYear )
        students.get(4).visaExpiryDate = Timestamp.valueOf( thisYearStart )
        students.get(5).visaExpiryDate = Timestamp.valueOf( thisYearEnd )
        students.get(6).visaExpiryDate = Timestamp.valueOf( nextYear )
        students.get(7).visaExpiryDate = Timestamp.valueOf( nextYearStart )
        students.get(8).visaExpiryDate = Timestamp.valueOf( nextYearEnd )

        context.commitChanges()

        executeQuery("visaExpiryDate last year", "[1, 2, 3]")
        executeQuery("visaExpiryDate this year", "[4, 5, 6]")
        executeQuery("visaExpiryDate next year", "[7, 8, 9]")
    }


    /**
     *  relative date using today/yesterday/tomorrow constants with math operators
     */
    @Test
    void mathOperatorsWithDatesTest(){
        LocalDateTime plusMonth = now().plusMonths(1)
        LocalDateTime plusMonthStart = plusMonth.with(MIN)
        LocalDateTime plusMonthEnd = plusMonth.with(MAX).withNano(0)

        LocalDateTime minus8Days = now().minusDays(8)
        LocalDateTime minus8DaysStart = minus8Days.with(MIN)
        LocalDateTime minus8DaysEnd = minus8Days.with(MAX).withNano(0)

        LocalDate definiteDate = LocalDate.of(2012, 12, 13)

        students.get(0).visaExpiryDate = Timestamp.valueOf( plusMonth )
        students.get(1).visaExpiryDate = Timestamp.valueOf( plusMonthStart )
        students.get(2).visaExpiryDate = Timestamp.valueOf( plusMonthEnd )
        students.get(3).visaExpiryDate = Timestamp.valueOf( minus8Days )
        students.get(4).visaExpiryDate = Timestamp.valueOf( minus8DaysStart )
        students.get(5).visaExpiryDate = Timestamp.valueOf( minus8DaysEnd )
        students.get(6).visaExpiryDate = Timestamp.valueOf( definiteDate.atStartOfDay() )

        context.commitChanges()

        executeQuery("visaExpiryDate = today + 1 month", "[1, 2, 3]")
        executeQuery("visaExpiryDate = today + 1 months", "[1, 2, 3]")
        executeQuery("visaExpiryDate is today + 1 month", "[1, 2, 3]")
        executeQuery("visaExpiryDate is today + 1 months", "[1, 2, 3]")

        executeQuery("visaExpiryDate = tomorrow - 9 days", "[4, 5, 6]")
        executeQuery("visaExpiryDate = (tomorrow - 2 days) - 1 week", "[4, 5, 6]")
        executeQuery("visaExpiryDate = yesterday - 1 week", "[4, 5, 6]")
        executeQuery("visaExpiryDate = 20/12/2012 - 1 week", "[7]")

        executeQuery("visaExpiryDate in tomorrow - 9 days .. today + 1 month", "[1, 2, 3, 4, 5, 6]")
        executeQuery("visaExpiryDate in 20/12/2012 - 1 week .. tomorrow - 9 days", "[4, 5, 6, 7]")

    }


    static void executeQuery(String query, String expectedResult) throws Exception {
        GroovyScriptService scriptService = injector.getInstance(GroovyScriptService)

        Script script = context.newObject(Script.class)
        script.setEnabled(true)

        script.setScript("def result = query {\n" +
            "       entity \"Student\"\n" +
            "       query '${query}'\n" +
            "       context args.context" +
            "}\n" +
            "\n" +
            "result*.id.sort()")

        ScriptResult result = scriptService.runScript(script, ScriptParameters.empty(), context)

        if (result.getType() == ScriptResult.ResultType.SUCCESS)
            assertEquals(expectedResult, result.getResultValue().toString())
        else
            fail("Incorrect syntax: " + result.error)
    }
}
