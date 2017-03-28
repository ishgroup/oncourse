package ish.oncourse.solr.functions.suburb

import ish.oncourse.model.PostcodeDb
import ish.oncourse.solr.model.SolrSuburb
import org.apache.cayenne.ObjectContext

import static org.apache.cayenne.query.ObjectSelect.query

/**
 * Created by akoira on 28/3/17.
 */
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
        return query(PostcodeDb).orderBy(PostcodeDb.POSTCODE.asc()).iterator(context).each(getSolrSuburb)
    }
}
