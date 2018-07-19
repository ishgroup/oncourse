package ish.oncourse.services.tag;

import ish.oncourse.model.College;
import ish.oncourse.model.Queueable;
import ish.oncourse.model.Tag;
import ish.oncourse.model.TagGroupRequirement;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;

import java.util.List;

public class GetTagGroupsFor {

    private ObjectContext context;
    private QueryCacheStrategy strategy;
    private College college;
    private String entityIdentiier;
    private boolean isWebVisibleOnly;

    private GetTagGroupsFor() {}

    public static GetTagGroupsFor valueOf(ObjectContext context, QueryCacheStrategy strategy, College college, String entityIdentifier, boolean isWebVisibleOnly) {
        GetTagGroupsFor obj = new GetTagGroupsFor();
        obj.context = context;
        obj.strategy = strategy;
        obj.college = college;
        obj.entityIdentiier = entityIdentifier;
        obj.isWebVisibleOnly = isWebVisibleOnly;
        return obj;
    }

    public List<Tag> get() {

        Expression expr = Tag.COLLEGE.eq(college)
                .andExp(Tag.TAG_GROUP_REQUIREMENTS.dot(TagGroupRequirement.ENTITY_IDENTIFIER).eq(entityIdentiier));

        if (isWebVisibleOnly) {
            expr = expr.andExp(Tag.IS_WEB_VISIBLE.isTrue());
        }

        ObjectSelect select = ObjectSelect.query(Tag.class).where(expr);
        if (strategy != null) {
           select = select.cacheStrategy(strategy);
        }

        return select.select(context);
    }
}
