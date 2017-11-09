package ish.oncourse.solr.functions.suburb

import io.reactivex.Observable
import ish.oncourse.model.PostcodeDb
import ish.oncourse.solr.model.SSuburb
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.ResultIterator

import static org.apache.cayenne.query.ObjectSelect.query

class Functions {
    static Closure<SSuburb> getSolrSuburb = { PostcodeDb postcode ->
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
        ResultIterator<PostcodeDb> postcodeDbs = query(PostcodeDb).orderBy(PostcodeDb.POSTCODE.asc()).iterator(context)
        return Observable.fromIterable(postcodeDbs).map({p -> getSolrSuburb.call(p)})
    }
}
