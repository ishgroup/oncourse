package ish.oncourse.services.tag;

import ish.common.types.NodeSpecialType;
import ish.oncourse.model.*;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GetContactTagsSubscribed {

    private Logger logger = LogManager.getLogger();

    private ObjectContext context;
    private QueryCacheStrategy strategy;
    private College college;
    private Contact contact;

    private GetContactTagsSubscribed() {}

    public static GetContactTagsSubscribed valueOf(ObjectContext context, QueryCacheStrategy strategy, College college, Contact contact) {
        GetContactTagsSubscribed obj = new GetContactTagsSubscribed();
        obj.context = context;
        obj.strategy = strategy;
        obj.college = college;
        obj.contact = contact;
        return obj;
    }

    public List<Tag> get() {
        List<Taggable> taggableList = ObjectSelect.query(Taggable.class)
                .where(Taggable.ENTITY_IDENTIFIER.eq(Contact.class.getSimpleName()))
                .and(Taggable.ENTITY_WILLOW_ID.eq(contact.getId()))
                .and(Taggable.COLLEGE.eq(college))
                .prefetch(Taggable.TAGGABLE_TAGS.disjoint())
                .select(context);


        List<Tag> allContactTags = GetTagGroupsFor
                .valueOf(context, strategy, college, Contact.class.getSimpleName(), true)
                .get();

        List<Tag> tags = new ArrayList<>();

        for (final Taggable t : taggableList) {
            for (final TaggableTag tg : t.getTaggableTags()) {
                Tag tag = tg.getTag();
                if (allContactTags.stream().anyMatch(m -> m.getId().equals(tag.getId()))) {
                    if(!tags.contains(tag)) {
                        tags.add(tag);
                    } else {
                        logger.error("Contact willowId:{} has more than one relation to MailingList (Tag) willowId:{}", contact.getId(), tag.getId());
                    }
                }
            }
        }

        return tags;
    }
}
