package ish.oncourse.services.tag;

import ish.oncourse.model.*;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.query.ObjectSelect;

import java.util.List;

public class UnlinkTagFromQueuable {

    private ObjectContext context;
    private Queueable target;
    private Tag tag;

    private UnlinkTagFromQueuable() {}

    public static UnlinkTagFromQueuable valueOf(ObjectContext context, Contact target, Tag tag) {
        UnlinkTagFromQueuable obj = new UnlinkTagFromQueuable();
        obj.context = context;
        obj.target = target;
        obj.tag = tag;
        return obj;
    }

    public void apply() {
        if (GetIsTagAssignedTo.valueOf(context, tag, target).get()) {
            Expression expr = Taggable.ENTITY_IDENTIFIER.eq(target.getClass().getSimpleName())
                    .andExp(Taggable.ENTITY_WILLOW_ID.eq(target.getId()))
                    .andExp(Taggable.TAGGABLE_TAGS.dot(TaggableTag.TAG).eq(tag));

            List<Taggable> taggables = ObjectSelect.query(Taggable.class).where(expr).select(context);
            taggables.forEach(taggable -> context.deleteObjects(taggable.getTaggableTags()));
            context.deleteObjects(taggables);
        }
    }
}
