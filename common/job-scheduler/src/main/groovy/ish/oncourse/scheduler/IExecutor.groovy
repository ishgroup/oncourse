package ish.oncourse.scheduler

import ish.oncourse.scheduler.job.IJob

interface IExecutor {
    void execute(Closure execute)
    void release()
    IJob job()
}
