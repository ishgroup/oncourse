package ish.oncourse.solr.functions.tag

import io.reactivex.Observable
import ish.oncourse.model.Tag
import ish.oncourse.model.Taggable
import ish.oncourse.model.TaggableTag
import ish.oncourse.solr.RXObservableFromIterable
import ish.oncourse.solr.model.STag
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import static org.apache.cayenne.query.ObjectSelect.query

class Functions {
    private static Logger LOGGER = LogManager.logger

    private static final String COURSE_IDENTIFIER = 'Course'

    static final ObjectSelect<Tag> TagsQuery = query(Tag)
            .where(Tag.IS_WEB_VISIBLE.eq(true))
            .and(Tag.TAGGABLE_TAGS.dot(TaggableTag.TAGGABLE).dot(Taggable.ENTITY_IDENTIFIER).eq(COURSE_IDENTIFIER))

    static Closure<STag> getSTag = { Tag tag ->
        return new STag().with {
            it.id = "${tag.id}"
            it.collegeId = tag.college.id
            it.name = tag.name
            it
        }
    }

    static Closure<Observable<STag>> getSolrTags = { ObjectContext context ->
        return new RXObservableFromIterable()
                .iterable({ TagsQuery.iterator(context) })
                .mapper(getSTag)
                .logger(LOGGER)
                .observable()
    }
}
