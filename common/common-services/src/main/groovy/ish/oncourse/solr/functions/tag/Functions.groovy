package ish.oncourse.solr.functions.tag

import ish.oncourse.model.Tag
import ish.oncourse.solr.model.SolrTag
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.ResultIterator
import org.apache.cayenne.query.ObjectSelect

class Functions {

    static Closure<SolrTag> getSolrTag = { Tag tag ->
        return new SolrTag().with {
            it.id = "${tag.id}"
            it.collegeId = tag.college.id
            it.name = tag.name
            it
        }
    }
    
    static Closure<Iterator<SolrTag>> getSolrTags = { ObjectContext context ->
        ResultIterator<Tag> tags = ObjectSelect.query(Tag).orderBy(Tag.NAME.asc()).iterator(context)
        return [hasNext: { return tags.hasNextRow() },
                next   : { return getSolrTag.call(tags.nextRow()) },
                remove : { tags.skipRow() }
        ] as Iterator
    }
}
