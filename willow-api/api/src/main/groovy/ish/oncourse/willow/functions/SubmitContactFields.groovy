package ish.oncourse.willow.functions

import ish.oncourse.cayenne.FieldInterface
import ish.oncourse.common.field.ContextType
import ish.oncourse.common.field.FieldProperty
import ish.oncourse.common.field.PropertyGetSet
import ish.oncourse.common.field.PropertyGetSetFactory
import ish.oncourse.model.Contact
import ish.oncourse.willow.model.field.Field


class SubmitContactFields {
    
    
    void submitContactFields(Contact contact, List<Field> fields) {

        PropertyGetSetFactory factory = new PropertyGetSetFactory('ish.oncourse.common')
        PropertyGetSet getSet 
        
        fields.each { f ->
            ContextType contextType = FieldProperty.getByKey(f.key).contextType
            Object context =  ContactDetailsBuilder.getContext(contextType, contact)
            getSet = factory.get(f as FieldInterface, context)
        }
    }
}
