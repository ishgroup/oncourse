package ish.oncourse.solr.functions.suburb

import io.reactivex.Observable
import ish.oncourse.model.PostcodeDb
import ish.oncourse.solr.model.SSuburb
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.ResultIterator
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import static org.apache.cayenne.query.ObjectSelect.query

class Functions {
    private static final Logger logger = LogManager.logger

    static final ObjectSelect<PostcodeDb> PostcodesQuery = query(PostcodeDb).orderBy(PostcodeDb.POSTCODE.asc())

    static Closure<SSuburb> getSSuburb = { PostcodeDb postcode ->
        return new SSuburb().with {
            it.id = "${postcode.postcode}${postcode.suburb}"
            it.suburb = postcode.suburb
            it.state = postcode.state
            it.postcode = postcode.postcode
            it.loc = "${postcode.lat},${postcode.lon}"
            it
        }
    }

    static Closure<Observable<SSuburb>> getSolrSuburbs = { ObjectContext context ->
        return Observable.fromCallable({ PostcodesQuery.iterator(context) })
                .flatMap({ ResultIterator<PostcodeDb> iterator ->
            Observable.fromIterable(iterator)
                    .map({ p -> getSSuburb.call(p) })
                    .doAfterTerminate({
                logger.debug("Closing suburbs iterator ...")
                iterator.close()
            })
        })
    }
}
