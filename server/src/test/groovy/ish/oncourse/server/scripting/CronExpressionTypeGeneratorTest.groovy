package ish.oncourse.server.scripting


import groovy.transform.CompileStatic
import ish.scripting.CronExpressionType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.quartz.CronScheduleBuilder

@CompileStatic
class CronExpressionTypeGeneratorTest {

    private static final String SPLITTER = " "

    @Test
    void testDailyMorningExpression() {
        String cronExpression = CronExpressionGenerator.generate(CronExpressionType.DAILY_MORNING.getDatabaseValue())

        String generatedNumber = cronExpression.split(SPLITTER)[1]
        Assertions.assertEquals(String.format("30 %s 5 * * ?", generatedNumber), cronExpression)

        CronScheduleBuilder.cronSchedule(cronExpression)
    }

    @Test
    void testDailyEveningExpression() {
        String cronExpression = CronExpressionGenerator.generate(CronExpressionType.DAILY_EVENING.getDatabaseValue())

        String generatedNumber = cronExpression.split(SPLITTER)[1]
        Assertions.assertEquals(String.format("30 %s 19 * * ?", generatedNumber), cronExpression)

        CronScheduleBuilder.cronSchedule(cronExpression)
    }

    
    @Test
    void testWeeklyMondayExpression() {
        String cronExpression = CronExpressionGenerator.generate(CronExpressionType.WEEKLY_MONDAY.getDatabaseValue())

        String generatedNumber = cronExpression.split(SPLITTER)[1]
        Assertions.assertEquals(String.format("30 %s 6 ? * MON *", generatedNumber), cronExpression)

        CronScheduleBuilder.cronSchedule(cronExpression)
    }

    
    @Test
    void testHourlyExpression() {
        String cronExpression = CronExpressionGenerator.generate(CronExpressionType.HOURLY.getDatabaseValue())

        String generatedNumber = cronExpression.split(SPLITTER)[1]
        Assertions.assertEquals(String.format("30 %s 0/1 * * ?", generatedNumber), cronExpression)

        CronScheduleBuilder.cronSchedule(cronExpression)
    }

    
    @Test
    void testCustomExpression() {
        String cronExpression = CronExpressionGenerator.generate("0 0 9 * * ?")

        Assertions.assertEquals("0 0 9 * * ?", cronExpression)
    }
}
