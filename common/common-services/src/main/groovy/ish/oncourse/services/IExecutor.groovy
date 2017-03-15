package ish.oncourse.services

interface IExecutor {
    void execute(Closure execute)
    void release()
}
