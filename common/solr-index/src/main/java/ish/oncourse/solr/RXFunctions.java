/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.solr;

import io.reactivex.Observable;

import java.io.Closeable;
import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * User: akoiro
 * Date: 6/6/18
 */
public class RXFunctions {

	public static <S, T> Observable<T> fromIterable(Callable<Iterable<S>> callable, Function<S, T> mapper) {
		return Observable.fromCallable(callable)
				.flatMap((iterable) ->
						Observable.fromIterable(iterable)
								.map(mapper::apply)
								.doAfterTerminate(() -> {
									if (iterable instanceof Closeable)
										((Closeable) iterable).close();
								}));
	}
}
