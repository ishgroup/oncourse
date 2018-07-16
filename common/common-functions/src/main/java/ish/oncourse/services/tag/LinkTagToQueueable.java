package ish.oncourse.services.tag;

import ish.oncourse.model.*;
import org.apache.cayenne.ObjectContext;

public class LinkTagToQueueable {

    private ObjectContext context;
    private Queueable target;
    private Tag tag;

    private LinkTagToQueueable() {}

    public static LinkTagToQueueable valueOf(ObjectContext context, Contact target, Tag tag) {
        LinkTagToQueueable obj = new LinkTagToQueueable();
        obj.context = context;
        obj.target = target;
        obj.tag = tag;
        return obj;
    }

    public void apply() {
        if (!GetIsTagAssignedTo.valueOf(context, tag, target).get()) {
            Taggable taggable = context.newObject(Taggable.class);
            taggable.setEntityIdentifier(target.getClass().getSimpleName());
            taggable.setCollege(tag.getCollege());
            taggable.setEntityWillowId(target.getId());
            taggable.setEntityAngelId(target.getAngelId());

            TaggableTag taggableTag = context.newObject(TaggableTag.class);
            taggableTag.setTaggable(taggable);
            taggableTag.setTag(tag);
            taggableTag.setCollege(tag.getCollege());
        }
    }
}
