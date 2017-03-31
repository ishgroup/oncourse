package ish.oncourse.solr.functions.tag

import ish.oncourse.model.Tag
import ish.oncourse.model.Taggable
import ish.oncourse.model.TaggableTag
import ish.oncourse.solr.model.SolrTag
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.ResultIterator
import org.apache.cayenne.query.ObjectSelect

class Functions {
    
    private static final String COURSE_IDENTIFIER= 'Course'
    
    static Closure<SolrTag> getSolrTag = { Tag tag ->
        return new SolrTag().with {
            it.id = "${tag.id}"
            it.collegeId = tag.college.id
            it.name = tag.name
            it
        }
    }
    
    static Closure<Iterator<SolrTag>> getSolrTags = { ObjectContext context ->
        ResultIterator<Tag> tags = ObjectSelect.query(Tag)
                .where(Tag.IS_WEB_VISIBLE.eq(true))
                .and(Tag.TAGGABLE_TAGS.dot(TaggableTag.TAGGABLE).dot(Taggable.ENTITY_IDENTIFIER).eq(COURSE_IDENTIFIER))
                .iterator(context)
        
        return [hasNext: { return tags.hasNextRow() },
                next   : { return getSolrTag.call(tags.nextRow()) },
                remove : { tags.skipRow() }
        ] as Iterator
    }
}
