package ish.oncourse.linktransform.functions;

import ish.oncourse.model.*;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Functional class that checks availability entity by root tag
 * Created by pavel on 3/27/17.
 */
public class GetEntityAvailabilityByTag {

    private static final Logger logger = LogManager.getLogger();

    private ObjectContext context;
    private WebSite webSite;
    private Queueable entity;

    /**
     * Create instance
     *
     * @param context context
     * @param webSite web site object
     * @param entity  entity that will be checked
     * @return instance
     */
    public static GetEntityAvailabilityByTag valueOf(ObjectContext context, WebSite webSite, Queueable entity) {
        GetEntityAvailabilityByTag res = new GetEntityAvailabilityByTag();
        res.context = context;
        res.webSite = webSite;
        res.entity = entity;
        return res;
    }

    /**
     * Check entity availability on website by its tag
     *
     * @return <code>true</code> if website not assigned to root tag,
     * <code>true</code> if entity assigned to root tag or child of root that declared in website root tag
     * <code>false</code> if tag of entity assigned to other website
     * <code>false</code> if entity doesn't belong to taggable type
     */
    public boolean get() {
        if (StringUtils.isBlank(webSite.getCoursesRootTagName())) {
            return true;
        }
        Tag rootTag = GetTagByPath.valueOf(context, webSite, webSite.getCoursesRootTagName()).get();
        return rootTag != null ? haveRelationAnyToRoot(getTags(webSite, entity), rootTag) : true;
    }

    protected boolean haveRelationAnyToRoot(List<Tag> tags, Tag rootTag) {
        if (tags != null && !tags.isEmpty()) {
            for (Tag tag : tags) {
                do {
                    if (tag.getId() == rootTag.getId()) {
                        return true;
                    }
                    tag = tag.getParent();
                } while (tag != null);
            }
        }
        return false;
    }

    protected List<Tag> getTags(WebSite webSite, Queueable entity) {
        Expression qualifier = Tag.COLLEGE.eq(webSite.getCollege())
                .andExp(Tag.TAGGABLE_TAGS.dot(TaggableTag.TAGGABLE).dot(Taggable.ENTITY_IDENTIFIER).eq(entity.getClass().getSimpleName())
                        .andExp(Tag.TAGGABLE_TAGS.dot(TaggableTag.TAGGABLE).dot(Taggable.ENTITY_WILLOW_ID).eq(entity.getId())));

        ObjectSelect query = ObjectSelect.query(Tag.class).and(qualifier);
        query.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, entity.getClass().getSimpleName());

        return context.performQuery(query);
    }
}
