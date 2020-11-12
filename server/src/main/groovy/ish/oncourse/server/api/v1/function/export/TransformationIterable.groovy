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

package ish.oncourse.server.api.v1.function.export

import ish.oncourse.cayenne.PersistentObjectI
import ish.print.transformations.PrintTransformation
import ish.util.PaginatedResultIterable
import org.apache.cayenne.ObjectContext

class TransformationIterable<T> implements Iterable<T> {

    private List<Long> sourceIds
    private PrintTransformation transformation
    private int index = 0

    private int listSize
    private ObjectContext context

    TransformationIterable(PrintTransformation transformation, List<Long> sourceIds, ObjectContext context) {
        this.context = context
        this.transformation = transformation
        this.sourceIds = sourceIds
        this.listSize = sourceIds.size()
    }


    private Iterator<? extends PersistentObjectI> getNextIterator() {

        List<PersistentObjectI> records = []
        while (records.empty && index < listSize) {
            records = transformation.applyTransformation(context, Collections.singletonList(sourceIds[index++]), [:])
        }

        if (records.empty) {
            return  Collections.emptyIterator()
        } else {
            return records.iterator()
        }
    }

    @Override
    TransformationIterator<T> iterator() {
        new TransformationIterator<>()
    }

    class TransformationIterator<T> implements Iterator<T> {
        private Iterator iterator

        @Override
        boolean hasNext() {

            if (iterator == null) {
                iterator = nextIterator
            }
            if (iterator.hasNext()) {
                return true
            } else {
                iterator = nextIterator
                return iterator.hasNext()
            }
        }

        @Override
        T next() {
            return iterator.next()
        }
    }
}
