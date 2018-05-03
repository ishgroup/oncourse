package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.Queueable;
import ish.oncourse.model.Taggable;
import ish.oncourse.model.TaggableTag;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 *  Reduces redundant tag relations between Queueable object and Tag
 */
public class TagRelationDeduper {

    private ObjectContext context;
    private Queueable entity;

    private TagRelationDeduper() {}

    public static TagRelationDeduper valueOf(ObjectContext context, Queueable taggableEntity) {
        TagRelationDeduper obj = new TagRelationDeduper();
        obj.context = context;
        obj.entity = taggableEntity;
        return obj;
    }

    public void dedupe() {
        List<Taggable> entityTaggables = getAllOf(entity);

        if (entityTaggables.size() > 1) {
            Taggable headTaggable = defineHeadTaggable(entityTaggables);
            List<Taggable> taggableDuplicates = defineDuplicates(entityTaggables);

            relinkTaggableRelations(taggableDuplicates, headTaggable);
            List<Taggable> unusuableTaggables = taggableDuplicates;
            List<TaggableTag> unusuableTaggableTags = dedupeTaggableTags(headTaggable);

            context.deleteObjects(unusuableTaggableTags);
            context.deleteObjects(unusuableTaggables);
        }
    }

    /**
     * Find identical taggables for Queueable
     * @param entity
     * @return
     */
    protected List<Taggable> getAllOf(Queueable entity) {
        return ObjectSelect.query(Taggable.class).where(Taggable.ENTITY_WILLOW_ID.eq(entity.getId())).select(context);
    }

    /**
     * Transfer TaggableTag objects from 'src' Taggables to 'dest' Taggable.
     * @param src
     * @param dest
     */
    protected void relinkTaggableRelations(List<Taggable> src, Taggable dest) {
        src.forEach(taggable -> taggable.getTaggableTags().forEach(taggableTag -> dest.addToTaggableTags(taggableTag)));
    }

    /**
     * Find duplicates of TaggableTags in Taggable and return them.
     * @param obj
     * @return
     */
    protected List<TaggableTag> dedupeTaggableTags(Taggable obj) {
        List<TaggableTag> unusable = new ArrayList<>();

        Map<Long, List<TaggableTag>> groupedTaggableTags = obj.getTaggableTags().stream().collect(Collectors.groupingBy(taggableTag -> taggableTag.getTag().getId()));
        for(long tagId : groupedTaggableTags.keySet()) {
            List<TaggableTag> group = groupedTaggableTags.get(tagId);
            unusable.addAll(group.stream().skip(1).collect(Collectors.toList()));
        }
        return unusable;
    }

    /**
     * Define singular Taggable that should presents Queueable
     * @param entityTaggables
     * @return
     */
    protected Taggable defineHeadTaggable(List<Taggable> entityTaggables) {
        return entityTaggables.get(0);
    }

    /** Define other Taggable objects as duplicates. They should relinked and deleted
     * @param entityTaggables
     * @return
     */
    protected List<Taggable> defineDuplicates(List<Taggable> entityTaggables) {
        return entityTaggables.stream().skip(1).collect(Collectors.toList());
    }
}
