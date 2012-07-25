package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedStatistic;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.webservices.replication.v4.updaters.IWillowUpdater;
import ish.oncourse.webservices.util.GenericQueuedStatisticStub;
import ish.oncourse.webservices.util.GenericReplicationStub;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;

import java.util.List;

public class QueuedStatisticProcessor {

    private static final Logger logger = Logger.getLogger(TransactionGroupProcessorImpl.class);

    private IWebSiteService webSiteService;
    private IWillowUpdater willowUpdater;
    private TransactionGroupProcessorImpl transactionGroupProcessor;
    private java.util.Date receivedTimestamp;

    private ObjectContext atomicContext;

    public QueuedStatisticProcessor(ObjectContext atomicContext,
                                    IWebSiteService webSiteService,
                                    IWillowUpdater willowUpdater,
                                    TransactionGroupProcessorImpl transactionGroupProcessor
                                    )
    {
        this.atomicContext = atomicContext;
        this.webSiteService = webSiteService;
        this.willowUpdater = willowUpdater;
        this.transactionGroupProcessor = transactionGroupProcessor;
    }

    public void cleanupStatistic() {
        if (receivedTimestamp == null)
            return;

        SelectQuery q = new SelectQuery(QueuedStatistic.class);
        q.andQualifier(ExpressionFactory.matchExp(QueuedStatistic.COLLEGE_PROPERTY, webSiteService.getCurrentCollege()));
        q.andQualifier(ExpressionFactory.lessExp(QueuedStatistic.RECEIVED_TIMESTAMP_PROPERTY, receivedTimestamp));
        List<QueuedStatistic> statisticForDelete = atomicContext.performQuery(q);
        if (!statisticForDelete.isEmpty()) {
            atomicContext.deleteObjects(statisticForDelete);
        }

        /**
         * reset the field to exclude cleanup if no one statistic stub was got.
         */
        receivedTimestamp = null;
    }

    private List<QueuedStatistic> statisticByEntity(final String entityName) {
        SelectQuery q = new SelectQuery(QueuedStatistic.class);
        q.andQualifier(ExpressionFactory.matchDbExp(QueuedStatistic.ENTITY_IDENTIFIER_PROPERTY, entityName));
        q.andQualifier(ExpressionFactory.matchExp(QueuedStatistic.COLLEGE_PROPERTY, webSiteService.getCurrentCollege()));
        return atomicContext.performQuery(q);
    }


    public Queueable process(GenericQueuedStatisticStub statisticStub)
    {
        if (statisticStub.isCleanupStub())
            receivedTimestamp = statisticStub.getReceivedTimestamp();
            final List<QueuedStatistic> objects = statisticByEntity(statisticStub.getStackedEntityIdentifier());
            switch (objects.size()) {
                case 0:
                    return transactionGroupProcessor.createObject((GenericReplicationStub) statisticStub);
                case 1:
                    QueuedStatistic objectToUpdate = objects.get(0);
                    willowUpdater.updateEntityFromStub((GenericReplicationStub) statisticStub, objectToUpdate, transactionGroupProcessor.createRelationShipCallback());
                    return objectToUpdate;
                default:
                    //we should not throw and exception because even if this occurs on next replication data will be correct.
                    String message = String.format("%s statistic objects found for entity:%s", objects.size(),
                            statisticStub.getStackedEntityIdentifier());
                    logger.warn(message);
                    return null;
            }
    }
}
