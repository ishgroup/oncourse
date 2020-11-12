/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.util;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;

import java.util.Iterator;
import java.util.List;

/**
 * Use our own Iterable PaginatedResult to prevent loading huge number of objects in the single query (it can cause an OutOfMemoryError).
 * Just iterate by small frame and upload new objects when it needed, release wasted objects immediately.
 * @param <T>
 */
public class PaginatedResultIterable<T>  implements Iterable<T> {

	private ObjectContext context;
	private SelectQuery<T> query;
	private int fetchOffset;
	private int frameSize;


	private List<T> currentPage;

	private PaginatedResultIterable() {}

	public static <T> PaginatedResultIterable<T> valueOf(ObjectContext context,  SelectQuery<T> query, int frameSize) {
		var iterable = new PaginatedResultIterable<T>();
		iterable.context = context;
		iterable.query = query;
		iterable.frameSize = frameSize;
		return iterable;
	}

	private Iterator getNextIterator() {
		query.setFetchOffset(fetchOffset);
		query.setFetchLimit(frameSize);
		currentPage = context.performQuery(query);
		fetchOffset += frameSize;
		return currentPage.iterator();
	}

	@Override
	public Iterator<T> iterator() {
		fetchOffset = 0;
		return new QueryResultIterator<>();
	}

	public class QueryResultIterator<T> implements Iterator<T> {
		private Iterator<T> iterator;

		@Override
		public boolean hasNext() {

			if (iterator == null) {
				iterator = getNextIterator();
			}
			if (iterator.hasNext()) {
				return true;
			} else {
				iterator = getNextIterator();
				return iterator.hasNext();
			}
		}

		@Override
		public T next() {
			return iterator.next();
		}
	}
}
