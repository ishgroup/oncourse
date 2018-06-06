/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.solr.reindex;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import ish.oncourse.solr.SolrCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class ReindexCollection<E> implements Runnable {
	private static Logger logger = LogManager.getLogger();
	private Observable<E> collectDoc;
	private SolrClient solrClient;
	private SolrCollection collection;
	private AtomicInteger total = new AtomicInteger(0);

	public ReindexCollection(SolrClient solrClient,
							 SolrCollection collection,
							 Observable<E> collectDoc) {
		this.solrClient = solrClient;
		this.collectDoc = collectDoc;
		this.collection = collection;
	}

	@Override
	public void run() {
		logger.debug("Starting reindex \"{}\"...", collection.name());
		collectDoc.blockingSubscribe(this::addBean, this::handle, this::commit);
	}

	private void handle(Throwable t) {
		logger.error(t.getLocalizedMessage(), t);
	}

	private Observable<E> batch(Observable<E> source) {
		return source.window(1000)
				.flatMap((w) -> w.doOnEach(this::doOnEach)
						.observeOn(Schedulers.io()), 4);
	}

	private void doOnEach(Notification<E> notification) {
		try {
			if (notification.isOnNext()) {
				total.incrementAndGet();
				addBean(notification.getValue());
			}
			if (notification.isOnComplete())
				commit();
			if (notification.isOnError())
				logger.error(notification.getError());
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	private UpdateResponse addBean(E bean) throws IOException, SolrServerException {
		total.incrementAndGet();
		return solrClient.addBean(collection.name(), bean);
	}

	private void commit() throws IOException, SolrServerException {
		logger.debug("Total Processed \"{}\" for \"{}\"...", total.intValue(), collection.name());
		solrClient.commit(collection.name());
	}
}
