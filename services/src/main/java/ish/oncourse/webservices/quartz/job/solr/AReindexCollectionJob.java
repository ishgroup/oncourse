package ish.oncourse.webservices.quartz.job.solr;

import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.message.IMessagePersonService;
import ish.oncourse.webservices.ServicesModule;
import ish.oncourse.webservices.quartz.QuartzModule;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.solr.client.solrj.SolrClient;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DisallowConcurrentExecution
public abstract class AReindexCollectionJob implements Job {
    protected ServerRuntime serverRuntime;
    protected SolrClient solrClient;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            serverRuntime = (ServerRuntime) context.getScheduler().getContext().get(QuartzModule.QUARTZ_CONTEXT_SERVER_RUNTIME_KEY);
            solrClient = (SolrClient) context.getScheduler().getContext().get(QuartzModule.QUARTZ_CONTEXT_SOLR_CLIENT_KEY);
            execute0();
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }

    protected abstract void execute0();
}
