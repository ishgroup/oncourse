package ish.oncourse.linktransform.functions;

import ish.oncourse.model.Tag;
import ish.oncourse.model.WebSite;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pavel on 4/13/17.
 */
public class GetTagByPath {

    private static final Logger logger = LogManager.getLogger();

    private ObjectContext context;
    private WebSite webSite;
    private String path;

    public static GetTagByPath valueOf(ObjectContext context, WebSite webSite, String path) {
        GetTagByPath result = new GetTagByPath();
        result.context = context;
        result.webSite = webSite;
        result.path = path;
        return result;
    }

    public Tag get(){
        return getTagByFullPath(context, webSite, path);
    }

    private Tag getTagByFullPath(ObjectContext context, WebSite webSite, String path) {
        if (path == null) {
            return null;
        }
        if (path.startsWith("/")) {
            path = path.replaceFirst("/", "");
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        if (path.equals("")) {
            return null;
        }
        String tagNames[] = path.split("/");

        Tag rootTag = null;
        Tag subjectsTag = getSubjectsTag(context, webSite);
        int i = 0;
        if (tagNames[0].equalsIgnoreCase(Tag.SUBJECTS_TAG_NAME)) {
            rootTag = subjectsTag;
            // don't process subjects tag in loop
            i = 1;
        }
        if (rootTag == null) {
            if (subjectsTag != null && subjectsTag.hasChildWithName(tagNames[0])) {
                rootTag = subjectsTag;
            } else {
                rootTag = getTagGroupByName(context, webSite, tagNames[0]);
                //don't need to process tag group if we have it
                i = 1;
            }
        }

        if (rootTag == null) {
            return null;
        }
        for (; i < tagNames.length; i++) {
            Tag tag = getChildWithName(rootTag, tagNames[i]);
            if (tag == null) {
                return null;
            } else {
                rootTag = tag;
            }

        }

        return rootTag;
    }

    private Tag getChildWithName(Tag tag, String childName){
        List<Tag> tags = findByQualifier(context,
                Tag.COLLEGE.eq(webSite.getCollege())
                        .andExp(Tag.PARENT.eq(tag))
                        .andExp(Tag.NAME.eq(childName)));
        return (tags.size() > 0) ? tags.get(0) : null;
    }

    private Tag getSubjectsTag(ObjectContext context, WebSite webSite) {
        List<Tag> tags = findByQualifier(context,
                Tag.COLLEGE.eq(webSite.getCollege()).andExp(Tag.NAME.eq(Tag.SUBJECTS_TAG_NAME)));
        return (tags.size() > 0) ? tags.get(0) : null;
    }

    private Tag getTagGroupByName(ObjectContext context, WebSite webSite, String name) {
        final List<Tag> tags = findByQualifier(context,
                Tag.COLLEGE.eq(webSite.getCollege())
                    .andExp(Tag.NAME.likeIgnoreCase(name))
                    .andExp(Tag.IS_TAG_GROUP.eq(Boolean.TRUE)));
        return (tags.size() > 0) ? tags.get(0) : null;
    }

    public List<Tag> findByQualifier(ObjectContext context, Expression qualifier) {

        try {
        List<Tag> results = ObjectSelect.query(Tag.class, qualifier).
                cacheStrategy(QueryCacheStrategy.SHARED_CACHE, Tag.class.getSimpleName()).
                select(context);

            if (results == null) {
                results = new ArrayList<>();
            }

            if (results.isEmpty()) {
                if (logger.isInfoEnabled()) {
                    logger.info("Query returned no results.");
                }
            }

            return results;

        } catch (Exception e) {
            logger.error("Query resulted in Exception thrown.", e);
            //TODO: Should the exception be rethrown to indicate error condition to the client code?
        }
        return null;
    }
}
