package ish.oncourse.services

import groovy.transform.CompileStatic
import ish.oncourse.solr.reindex.IReindexJob
import ish.oncourse.solr.reindex.ScheduleConfig
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
        IReindexJob job = mock(IReindexJob)
        when(job.config).thenReturn(config)

        IExecutor executor = mock(IExecutor)

        doAnswer(new Answer() {
            @Override
            Object answer(InvocationOnMock invocation) throws Throwable {
                ((Closure)invocation.arguments[0]).call()
            }
        }).when(executor).execute(any(Closure))


        ScheduledService service = ScheduledService.valueOf(executor, job)
        service.start()

        while (service.futures.size() < 1) {
            sleep(100)
        }

        verify(config, times(1)).period
        verify(config, times(1)).initialDelay
        verify(config, times(1)).timeUnit
    }
}
