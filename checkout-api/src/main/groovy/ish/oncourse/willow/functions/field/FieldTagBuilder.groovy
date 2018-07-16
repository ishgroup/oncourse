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
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static ish.oncourse.common.field.FieldProperty.*

class FieldTagBuilder {
    
    Field field

    Contact contact
    WebSite webSite

    static Logger logger = LoggerFactory.getLogger(FieldTagBuilder.class)
    
    List<ish.oncourse.willow.model.field.Field> build() {

        List<ish.oncourse.willow.model.field.Field> res = new ArrayList<>()

        FieldProperty prop = getByKey(field.property)
        switch (prop) {
            case TAG :
                addTagField(res)
                break
            case MAILING_LIST:
                addMailingListField(res)
                break
        }
        res
    }


    private ish.oncourse.willow.model.field.Field fill(Field field) {
        ish.oncourse.willow.model.field.Field res = new ish.oncourse.willow.model.field.Field()

        res.id = field.id?.toString()
        res.description = field.description
        res.defaultValue = field.defaultValue
        res.ordering = field.order

        res
    }

    private void addTagField(List<ish.oncourse.willow.model.field.Field> res) {
        Tag tag = GetTagByPath.valueOf(contact.objectContext, webSite, field.property.replace(PropertyGetSetFactory.TAG_PATTERN, StringUtils.EMPTY)).get()
        if (tag) {
            List<Tag> leafs = GetTagLeafs.valueOf(tag).get()
            if (leafs && !leafs.isEmpty()) {
                TagGroupRequirement requirement = GetRequirementForType.valueOf(tag, Contact.class.simpleName).get()
                if (requirement) {
                    if (requirement.allowsMultipleTags) {
                        leafs.forEach { Tag t ->
                            ish.oncourse.willow.model.field.Field f = fill(field)
                            f.dataType = DataType.TAGGROUP_M
                            f.name = t.defaultPath
                            f.key = String.format("%s%s", PropertyGetSetFactory.TAG_M_PATTERN, f.name)
                            f.mandatory = field.mandatory
                            res << f
                        }
                    } else {
                        ish.oncourse.willow.model.field.Field f = fill(field)
                        f.dataType = DataType.TAGGROUP_S
                        f.name = field.name
                        f.key = String.format("%s%s", PropertyGetSetFactory.TAG_S_PATTERN, f.name)
                        f.mandatory = field.mandatory
                        leafs.each { item ->
                            f.enumItems << new Item(value: item.defaultPath, key: item.defaultPath)
                        }
                        res << f
                    }
                } else {
                    logger.error("Tag group {} does not applicable to {} entities.", tag.name, Contact.simpleName)
                }
            } else {
                logger.error("Tag group {} has zero leafs.", tag.name)
            }
        } else {
            logger.error("Tag with path {} not found.", field.property.replace(PropertyGetSetFactory.TAG_PATTERN, StringUtils.EMPTY))
        }
    }

    private void addMailingListField(List<ish.oncourse.willow.model.field.Field> res) {
        List<Tag> mailingLists = GetMailingLists.valueOf(contact.objectContext, null, contact.college).get()
        Tag tag = mailingLists.stream()
                .filter { Tag l -> field.property.replace(PropertyGetSetFactory.MAILING_LIST_FIELD_PATTERN, StringUtils.EMPTY) == l.name }
                .findAny().orElse(null)

        if (tag) {
            ish.oncourse.willow.model.field.Field f = fill(field)
            f.dataType = DataType.MAILINGLIST
            f.name = field.name
            f.key = String.format("%s%s", PropertyGetSetFactory.MAILING_LIST_FIELD_PATTERN, f.name)
            f.mandatory = false
            res << f
        } else {
            logger.error("Mailing list {} not found.", field.property.replace(PropertyGetSetFactory.MAILING_LIST_FIELD_PATTERN, StringUtils.EMPTY))
        }
    }
}
