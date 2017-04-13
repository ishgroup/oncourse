package ish.oncourse.linktransform.functions;

import ish.oncourse.model.*;
import ish.oncourse.services.course.functions.ApplyCourseCacheSettings;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.query.ObjectSelect;
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
     * @param context context
     * @param webSite web site object
     * @param entity entity that will be checked
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
     * @return <code>true</code> if website not assigned to root tag,
     * <code>true</code> if entity assigned to root tag or child of root that declared in website root tag
     * <code>false</code> if tag of entity assigned to other website
     * <code>false</code> if entity doesn't belong to taggable type
     */
    public boolean get() {
        // if website has no root tag - entity is available
        if(webSite.getCoursesRootTagName() == null || webSite.getCoursesRootTagName().equals(StringUtils.EMPTY)){
            return true;
        }
        Tag webSiteRootTag = GetTagByPath.valueOf(context, webSite, webSite.getCoursesRootTagName()).get();
        // if entity belongs to one of taggable types and tags contains tag with name webSite.rootTagName
        if (isTaggableEntity(entity) && webSiteRootTag != null) {

            List<Tag> attachedTags = null;
            ObjectSelect query = ObjectSelect.query(Tag.class).and(getQualifier(webSite.getCollege(), entity));

            try {
                attachedTags = context.performQuery(
                        ApplyCourseCacheSettings.valueOf(query).apply());
            } catch (Exception e) {
                logger.error("Query resulted in Exception thrown. Query: {}", query, e);
                //TODO: Should the exception be rethrown to indicate error condition to the client code?
            }
            if (attachedTags != null && !attachedTags.isEmpty()) {
                for (Tag tag : attachedTags) {
                    do {
                        if (tag.getId() == webSiteRootTag.getId()) {
                            return true;
                        }
                        tag = tag.getParent();
                    } while (tag != null);
                }
            }
        }
        return false;
    }

    /**
     * Expression which select all tags assigned to entity in scope of college
     *
     * @param college college
     * @param entity  entity object
     * @return expression
     */
    private Expression getQualifier(College college, Queueable entity) {
        return Tag.COLLEGE.eq(college).andExp(Tag.TAGGABLE_TAGS.dot(TaggableTag.TAGGABLE).dot(Taggable.ENTITY_IDENTIFIER).eq(entity.getClass().getSimpleName())
                .andExp(Tag.TAGGABLE_TAGS.dot(TaggableTag.TAGGABLE).dot(Taggable.ENTITY_WILLOW_ID).eq(entity.getId())));
    }

    /**
     * Check entity for type that we can use with tags
     * @param entity
     * @return
     */
    private boolean isTaggableEntity(Queueable entity) {
        Class entityClass = entity.getClass();
        if (!entityClass.equals(BinaryInfo.class) &&
                !entityClass.equals(Contact.class) &&
                !entityClass.equals(Course.class) &&
                !entityClass.equals(CourseClass.class) &&
                !entityClass.equals(Session.class) &&
                !entityClass.equals(Site.class) &&
                !entityClass.equals(Tag.class) &&
                !entityClass.equals(Tutor.class)) {
            logger.error(String.format("%s is not taggable entity.", entityClass.getSimpleName()));
            return false;
        }
        return true;
    }
}
