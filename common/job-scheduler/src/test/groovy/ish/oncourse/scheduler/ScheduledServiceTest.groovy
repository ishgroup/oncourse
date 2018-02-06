package ish.oncourse.scheduler

import groovy.transform.CompileStatic
import ish.oncourse.scheduler.job.IJob
import org.junit.Test
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

import java.util.concurrent.TimeUnit

import static org.mockito.Matchers.any
import static org.mockito.Mockito.*

@CompileStatic
class ScheduledServiceTest {

    @Test
    void test() {

        ScheduleConfig config = mock(ScheduleConfig)
        when(config.initialDelay).thenReturn(0L)
        when(config.period).thenReturn(1L)
        when(config.timeUnit).thenReturn(TimeUnit.SECONDS)
        IJob job = mock(IJob)
        when(job.config).thenReturn(config)

        IExecutor executor = mock(IExecutor)
        when(executor.job()).thenReturn(job)

        doAnswer(new Answer() {
            @Override
            Object answer(InvocationOnMock invocation) throws Throwable {
                ((Closure)invocation.arguments[0]).call()
            }
        }).when(executor).execute(any(Closure))


        ScheduledService service = ScheduledService.valueOf(executor)
        service.start()

        while (service.futures.size() < 1) {
            sleep(100)
        }

        verify(config, times(1)).period
        verify(config, times(1)).initialDelay
        verify(config, times(1)).timeUnit
    }
}
