package ish.oncourse.services.tag;

import ish.oncourse.model.Queueable;
import ish.oncourse.model.Tag;
import ish.oncourse.model.Taggable;
import ish.oncourse.model.TaggableTag;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;

public class GetIsTagAssignedTo {

    private ObjectContext context;
    private Tag tag;
    private Queueable q;

    private GetIsTagAssignedTo() {}

    public static GetIsTagAssignedTo valueOf(ObjectContext context, Tag tag, Queueable q) {
        GetIsTagAssignedTo obj = new GetIsTagAssignedTo();
        obj.context = context;
        obj.tag = tag;
        obj.q = q;
        return obj;
    }

    public boolean get() {
        return !ObjectSelect.query(Taggable.class)
                .where(Taggable.ENTITY_WILLOW_ID.eq(q.getId())
                        .andExp(Taggable.TAGGABLE_TAGS.dot(TaggableTag.TAG).eq(tag)))
                .select(context)
                .isEmpty();
    }
}
