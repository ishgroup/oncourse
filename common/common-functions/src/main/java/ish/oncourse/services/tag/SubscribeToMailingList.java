package ish.oncourse.services.tag;

import ish.oncourse.model.*;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;

import java.util.Date;

public class SubscribeToMailingList {

    private ObjectContext context;
    private Contact contact;
    private Tag mailingList;

    private SubscribeToMailingList() {}

    public static SubscribeToMailingList valueOf(ObjectContext context, Contact contact, Tag mailingList) {
        SubscribeToMailingList obj = new SubscribeToMailingList();
        obj.context = context;
        obj.contact = contact;
        obj.mailingList = mailingList;
        return obj;
    }

    public void subscribe() {
        if (!GetIsTagAssignedTo.valueOf(context, mailingList, contact).get()) {
            Date date = new Date();

            College college = context.localObject(contact.getCollege());

            Tag list = context.localObject(mailingList);

            Taggable taggable = context.newObject(Taggable.class);
            taggable.setCollege(college);
            taggable.setCreated(date);
            taggable.setModified(date);
            taggable.setEntityIdentifier(Contact.class.getSimpleName());
            taggable.setEntityWillowId(contact.getId());
            taggable.setEntityAngelId(contact.getAngelId());

            TaggableTag taggableTag = context.newObject(TaggableTag.class);
            taggableTag.setCollege(college);
            taggableTag.setTag(list);
            taggable.addToTaggableTags(taggableTag);
        }
    }
}
