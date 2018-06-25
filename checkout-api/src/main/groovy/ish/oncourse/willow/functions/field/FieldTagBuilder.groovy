package ish.oncourse.willow.functions.field

import ish.common.types.YesNoOptions
import ish.common.util.DisplayableExtendedEnumeration
import ish.oncourse.common.field.FieldProperty
import ish.oncourse.common.field.PropertyGetSetFactory
import ish.oncourse.model.Contact
import ish.oncourse.model.Field
import ish.oncourse.model.Tag
import ish.oncourse.model.WebSite
import ish.oncourse.services.tag.GetMailingLists
import ish.oncourse.services.tag.GetRequirementForType
import ish.oncourse.services.tag.GetTagByPath
import ish.oncourse.services.tag.GetTagLeafs
import ish.oncourse.willow.model.common.Item
import ish.oncourse.willow.model.field.DataType
import org.apache.cayenne.query.QueryCacheStrategy
import org.apache.commons.lang3.StringUtils

import static ish.oncourse.common.field.FieldProperty.*

class FieldTagBuilder {
    
    Field field

    Contact contact
    WebSite webSite
    
    ish.oncourse.willow.model.field.Field build() {

        ish.oncourse.willow.model.field.Field res = null

        FieldProperty prop = getByKey(field.property)
        switch (prop) {
            case TAG_GROUP :
                Tag tag = GetTagByPath.valueOf(contact.objectContext, webSite, field.property.replace(PropertyGetSetFactory.TAG_FIELD_PATTERN, StringUtils.EMPTY)).get()
                List<Tag> leafs = GetTagLeafs.valueOf(tag).get()

                if (leafs && !leafs.empty) {
                    res = new ish.oncourse.willow.model.field.Field()
                    res.enumType = GetRequirementForType.valueOf(tag, Contact.class.simpleName) ? "MULTIPLE" : "SINGLE"
                    res.dataType = DataType.TAGGROUP

                    leafs.each { item ->
                        res.enumItems  << new Item(value: item.defaultPath, key: item.defaultPath)
                    }
                }
                break
            case MAILING_LIST :
                List<Tag> mailingLists = GetMailingLists.valueOf(contact.objectContext, null, contact.college).get()
                    Tag tag = mailingLists.stream()
                            .filter { Tag l -> field.property == l.name }
                            .findAny().orElse(null)

                    if (tag) {
                        res = new ish.oncourse.willow.model.field.Field()
                        res.dataType = DataType.MAILINGLIST
                        res.enumType = DataType.MAILINGLIST.toString()
                        YesNoOptions.class.getEnumConstants().each { DisplayableExtendedEnumeration item ->
                            res.enumItems  << new Item(value: item.displayName, key: item.databaseValue.toString())
                        }
                    }
                break
        }

        if (res) {
            res.id = field.id?.toString()
            res.name = field.name
            res.description = field.description
            res.mandatory = field.mandatory
            res.key = field.property
            res.defaultValue = field.defaultValue
            res.ordering = field.order
        }

        res
    }
}
