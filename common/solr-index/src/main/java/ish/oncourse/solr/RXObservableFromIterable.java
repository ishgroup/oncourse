/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.solr;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * User: akoiro
 * Date: 6/6/18
 */
public class RXObservableFromIterable<S, T> {
	private Callable<Iterable<S>> iterable;
	private Function<S, T> mapper;
	private Scheduler scheduler = Schedulers.io();
	private Logger logger = LogManager.getLogger();

	public RXObservableFromIterable<S, T> iterable(Callable<Iterable<S>> iterable) {
		this.iterable = iterable;
		return this;
	}

	public RXObservableFromIterable<S, T> mapper(Function<S, T> mapper) {
		this.mapper = mapper;
		return this;
	}

	public RXObservableFromIterable<S, T> scheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
		return this;
	}

	public RXObservableFromIterable<S, T> logger(Logger logger) {
		this.logger = logger;
		return this;
	}


	public Observable<T> observable() {
		return Observable.fromCallable(iterable)
				.flatMap((iterable) ->
						Observable.fromIterable(iterable)
								.map(mapper::apply)
								.doAfterTerminate(this::close));
	}


	public Observable<T> parallelObservable() {
		return Flowable.fromCallable(iterable).flatMap((it) ->
				Flowable.fromIterable(it).parallel().runOn(scheduler)
						.map(mapper::apply)
						.doOnError(logger::catching).sequential())
				.toObservable().doAfterTerminate(this::close);
	}

	private void close() {
		if (iterable instanceof Closeable) {
			IOUtils.closeQuietly((Closeable) iterable);
		}
	}

}
