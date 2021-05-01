/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish

import groovy.transform.CompileStatic
import org.quartz.*

@CompileStatic
class FakeJobExecutionContext implements JobExecutionContext {

    /**
     *
     */
    FakeJobExecutionContext() {}

    /**
     * @see org.quartz.JobExecutionContext#get(java.lang.Object)
     */
    Object get(Object arg0) {
        return null
    }

    /**
     * @see org.quartz.JobExecutionContext#getCalendar()
     */
    Calendar getCalendar() {
        return null
    }

    /**
     * @see org.quartz.JobExecutionContext#getFireInstanceId()
     */
    String getFireInstanceId() {
        return null
    }

    /**
     * @see org.quartz.JobExecutionContext#getFireTime()
     */
    Date getFireTime() {
        return null
    }

    /**
     * @see org.quartz.JobExecutionContext#getJobDetail()
     */
    JobDetail getJobDetail() {
        return null
    }

    /**
     * @see org.quartz.JobExecutionContext#getJobInstance()
     */
    Job getJobInstance() {
        return null
    }

    /**
     * @see org.quartz.JobExecutionContext#getJobRunTime()
     */
    long getJobRunTime() {
        return 0
    }

    /**
     * @see org.quartz.JobExecutionContext#getMergedJobDataMap()
     */
    JobDataMap getMergedJobDataMap() {
        return null
    }

    /**
     * @see org.quartz.JobExecutionContext#getNextFireTime()
     */
    Date getNextFireTime() {
        return null
    }

    /**
     * @see org.quartz.JobExecutionContext#getPreviousFireTime()
     */
    Date getPreviousFireTime() {
        return null
    }

    /**
     * @see org.quartz.JobExecutionContext#getRefireCount()
     */
    int getRefireCount() {
        return 0
    }

    /**
     * @see org.quartz.JobExecutionContext#getResult()
     */
    Object getResult() {
        return null
    }

    /**
     * @see org.quartz.JobExecutionContext#getScheduledFireTime()
     */
    Date getScheduledFireTime() {
        return null
    }

    /**
     * @see org.quartz.JobExecutionContext#getScheduler()
     */
    Scheduler getScheduler() {
        return null
    }

    /**
     * @see org.quartz.JobExecutionContext#getTrigger()
     */
    Trigger getTrigger() {
        return null
    }

    /**
     * @see org.quartz.JobExecutionContext#isRecovering()
     */
    boolean isRecovering() {
        return false
    }

    /**
     * @see org.quartz.JobExecutionContext#put(java.lang.Object, java.lang.Object)
     */
    void put(Object arg0, Object arg1) {}

    /**
     * @see org.quartz.JobExecutionContext#setResult(java.lang.Object)
     */
    void setResult(Object arg0) {}

    @Override
    TriggerKey getRecoveringTriggerKey() throws IllegalStateException {
        return null
    }
}
