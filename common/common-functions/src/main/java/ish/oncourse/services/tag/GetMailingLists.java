package ish.oncourse.services.tag;

import ish.common.types.NodeSpecialType;
import ish.oncourse.model.College;
import ish.oncourse.model.Tag;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.Query;
import org.apache.cayenne.query.QueryCacheStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetMailingLists {

    private ObjectContext context;
    private QueryCacheStrategy strategy;
    private College college;

    private GetMailingLists() {}

    public static GetMailingLists valueOf(ObjectContext context, QueryCacheStrategy strategy, College college) {
        GetMailingLists obj = new GetMailingLists();
        obj.context = context;
        obj.strategy = strategy;
        obj.college = college;
        return obj;
    }

    public List<Tag> get() {
        Tag parent = getParentTag(context, strategy, college);

        if (parent != null) {
            return new ArrayList<>(getTag(context, strategy, college, parent));
        }
        return Collections.emptyList();
    }

    private Expression getSiteQualifier(College college) {
        return Tag.COLLEGE.eq(college).andExp(Tag.IS_WEB_VISIBLE.eq(true));
    }

    private Tag getParentTag(ObjectContext context, QueryCacheStrategy strategy, College college) {
        ObjectSelect<Tag> parentQuery = ObjectSelect.query(Tag.class)
                .where(Tag.COLLEGE.eq(college)
                        .andExp(Tag.SPECIAL_TYPE.eq(NodeSpecialType.MAILING_LISTS))
                        .andExp(Tag.PARENT.isNull()));

        if (strategy != null) {
            parentQuery.cacheStrategy(strategy);
        }

        return parentQuery.selectFirst(context);
    }

    private List<Tag> getTag(ObjectContext context, QueryCacheStrategy strategy, College college, Tag parent) {
        ObjectSelect<Tag> tagQuery = ObjectSelect.query(Tag.class).where(getSiteQualifier(college))
                .and(Tag.PARENT.eq(parent));

        if (strategy != null) {
            tagQuery.cacheStrategy(strategy);
        }

        return tagQuery.select(context);
    }
}
