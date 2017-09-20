package ish.oncourse.cayenne.cache

import org.apache.cayenne.CayenneRuntimeException
import org.apache.cayenne.cache.QueryCacheEntryFactory

import javax.cache.processor.EntryProcessor
import javax.cache.processor.EntryProcessorException
import javax.cache.processor.MutableEntry

class JCacheEntryLoader implements EntryProcessor<String, List, List> {

    private QueryCacheEntryFactory entryFactory

    JCacheEntryLoader(QueryCacheEntryFactory entryFactory) {
        this.entryFactory = entryFactory
    }
    
    @Override
    List process(MutableEntry<String, List> entry, Object... arguments) throws EntryProcessorException {
        if (!entry.exists()) {
            List result = (List)entryFactory.createObject()
            if (result == null) {
                throw new CayenneRuntimeException("Null object created: " + entry.getKey())
            }
            entry.value = result
        }

        return entry.value
    }
}
