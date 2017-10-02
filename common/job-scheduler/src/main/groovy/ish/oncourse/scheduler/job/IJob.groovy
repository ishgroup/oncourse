package ish.oncourse.scheduler.job

import groovy.transform.CompileStatic
import ish.oncourse.scheduler.ScheduleConfig

@CompileStatic
interface IJob extends Runnable {
    ScheduleConfig getConfig()
}
