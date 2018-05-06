/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.solr.reindex;

import com.cronutils.model.Cron;
import com.cronutils.parser.CronParser;
import io.reactivex.Observable;
import ish.oncourse.scheduler.job.IJob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;

/**
 * User: akoiro
 * Date: 7/5/18
 */
public class ReindexJob<E> implements IJob {
	private static Logger logger = LogManager.getLogger();
	private Observable<E> collectDoc;
	private SolrClient solrClient;
	private Cron cron;

	public ReindexJob(SolrClient solrClient,
					  Observable<E> collectDoc,
					  String cron) {
		this.solrClient = solrClient;
		this.collectDoc = collectDoc;
		this.cron = new CronParser(DEFAULT_CRON_DEFINITION).parse(cron);
	}

	@Override
	public Cron getCron() {
		return cron;
	}

	@Override
	public void run() {
		collectDoc.blockingSubscribe((d) -> solrClient.addBean(d),
				(e) -> logger.error(e.getLocalizedMessage(), e),
				() -> solrClient.commit());
	}
}
