package ish.oncourse.scheduler

interface IExecutor {
    void execute(Closure execute)
    void release()
}
