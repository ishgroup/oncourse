package ish.oncourse.solr.functions.suburb

import ish.oncourse.model.PostcodeDb
import ish.oncourse.solr.model.SolrSuburb
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.ResultIterator

import static org.apache.cayenne.query.ObjectSelect.query

class Functions {
    static Closure<SolrSuburb> getSolrSuburb = { PostcodeDb postcode ->
        return new SolrSuburb().with {
            it.id = "${postcode.postcode}${postcode.suburb}"
            it.suburb = postcode.suburb
            it.state = postcode.state
            it.postcode = postcode.postcode
            it.loc = "${postcode.lat},${postcode.lon}"
            it
        }
    }

    static Closure<Iterator<SolrSuburb>> getSolrSuburbs = { ObjectContext context ->
        ResultIterator<PostcodeDb> postcodeDbs = query(PostcodeDb).orderBy(PostcodeDb.POSTCODE.asc()).iterator(context)
        return [hasNext: { return postcodeDbs.hasNextRow() },
                next   : { return getSolrSuburb.call(postcodeDbs.nextRow()) },
                remove : { postcodeDbs.skipRow() }
        ] as Iterator
    }
}
