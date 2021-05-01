/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.services


import groovy.transform.CompileStatic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory
import org.reflections.Reflections

import java.text.ParseException

/**
 * This tests whether the jobs executed with quartz scheduler are not executed concurrently.
 * Its kind of useless test of a code from external library, but since we rely on this its better to make sure it works.
 *
 */
@CompileStatic
@DisallowConcurrentExecution
class TriggerListenerTest implements Job {

    private final static long JOB_CHECK_INTERVAL = 500
    private final static long JOB_EXECUTION_TIME = JOB_CHECK_INTERVAL * 5
    static int instancesExecuted = 0

    /**
     * tests if there is always one job running at a time, when the DisallowConcurrentExecution annnotation is in place.
     * @throws ParseException
     */
    
    @Test
    void testDisallowConcurrentExecution() throws ParseException {
        try {

            SchedulerFactory sf = new StdSchedulerFactory()
            Scheduler scheduler = sf.getScheduler()

            JobDetail testJob = JobBuilder.newJob(TriggerListenerTest.class).withIdentity("testJob", "testJobs").build()
            CronTrigger testTrigger = TriggerBuilder.newTrigger().withIdentity("testTrigger", "testJobs").startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule("* * * * * ?")).build()
            scheduler.scheduleJob(testJob, testTrigger)

            scheduler.start()

            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(JOB_CHECK_INTERVAL)
                } catch (InterruptedException ignored) {
                }

                Assertions.assertTrue(instancesExecuted == 0 || instancesExecuted == 1, "Job interval check no. " + i + ", job instances running " + instancesExecuted)

            }

            //terminate all jobs and the scheduler
            scheduler.shutdown(false)

        } catch (SchedulerException e) {
            Assertions.fail("scheduler exception : " + e)
        }
    }

    /**
     * checks if the jobs added to the project are disallowing concuret execution.
     * This test will prompt developer to think about addign the 'DisallowConcurrentExecution' to any new job created in the system.
     * @throws ParseException
     */
    
    @Test
    void checkJobs() throws ParseException {
        Reflections reflections = new Reflections("ish")
        Set<Class<? extends Job>> set = reflections.getSubTypesOf(Job.class)

        for (Class<? extends Job> job : set) {
            System.out.println("checking job " + job.getName())
            Assertions.assertTrue(job.isAnnotationPresent(DisallowConcurrentExecution.class), "Job " + job.getName() + " is not annotated with 'DisallowConcurrentExecution'.")
        }

    }

    /**
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    void execute(JobExecutionContext arg0) throws JobExecutionException {
        instancesExecuted++
        try {
            Thread.sleep(JOB_EXECUTION_TIME)
        } catch (InterruptedException ignored) {

        }
        instancesExecuted--
    }

}
