package ish.oncourse.willow.functions.field

import ish.oncourse.common.field.FieldProperty
import ish.oncourse.common.field.PropertyGetSetFactory
import ish.oncourse.model.Contact
import ish.oncourse.model.Field
import ish.oncourse.model.Tag
import ish.oncourse.model.TagGroupRequirement
import ish.oncourse.model.WebSite
import ish.oncourse.services.tag.GetMailingLists
import ish.oncourse.services.tag.GetRequirementForType
import ish.oncourse.services.tag.GetTagByPath
import ish.oncourse.services.tag.GetTagLeafs
import ish.oncourse.willow.model.common.Item
import ish.oncourse.willow.model.field.DataType
import org.apache.commons.lang3.StringUtils

import static ish.oncourse.common.field.FieldProperty.*

class FieldTagBuilder {
    
    Field field

    Contact contact
    WebSite webSite
    
    List<ish.oncourse.willow.model.field.Field> build() {

        List<ish.oncourse.willow.model.field.Field> res = new ArrayList<>()

        FieldProperty prop = getByKey(field.property)
        switch (prop) {
            case TAG :
                Tag tag = GetTagByPath.valueOf(contact.objectContext, webSite, field.property.replace(PropertyGetSetFactory.TAG_PATTERN, StringUtils.EMPTY)).get()
                List<Tag> leafs = GetTagLeafs.valueOf(tag).get()
                TagGroupRequirement requirement = GetRequirementForType.valueOf(tag, Contact.class.simpleName).get()
                if (requirement.allowsMultipleTags) {
                    leafs.forEach { Tag t ->
                        ish.oncourse.willow.model.field.Field f = fill(field)
                        f.dataType = DataType.TAGGROUP_M
                        f.name = t.defaultPath
                        f.key = String.format("%s%s", PropertyGetSetFactory.TAG_M_PATTERN, f.name)
                        res << f
                    }
                } else {
                    ish.oncourse.willow.model.field.Field f = fill(field)
                    f.dataType = DataType.TAGGROUP_S
                    f.key = String.format("%s%s", PropertyGetSetFactory.TAG_S_PATTERN, f.name)

                    leafs.each { item ->
                        f.enumItems  << new Item(value: item.defaultPath, key: item.defaultPath)
                    }
                    res << f
                }
                break
            case MAILING_LIST:
                List<Tag> mailingLists = GetMailingLists.valueOf(contact.objectContext, null, contact.college).get()
                Tag tag = mailingLists.stream()
                        .filter { Tag l -> field.property.replace(PropertyGetSetFactory.MAILING_LIST_FIELD_PATTERN, StringUtils.EMPTY) == l.name }
                        .findAny().orElse(null)

                if (tag) {
                    ish.oncourse.willow.model.field.Field f = fill(field)
                    f.dataType = DataType.MAILINGLIST
                    f.key = String.format("%s%s", PropertyGetSetFactory.MAILING_LIST_FIELD_PATTERN, f.name)
                    res << f
                }
                break
        }
        res
    }


    private ish.oncourse.willow.model.field.Field fill(Field field) {
        ish.oncourse.willow.model.field.Field res = new ish.oncourse.willow.model.field.Field()

        res.id = field.id?.toString()
        res.name = field.name
        res.description = field.description
        res.mandatory = field.mandatory
        res.key = field.property
        res.defaultValue = field.defaultValue
        res.ordering = field.order

        res
    }
}
