package ish.oncourse.willow.functions

import groovy.transform.CompileStatic
import ish.oncourse.cayenne.FieldInterface
import ish.oncourse.common.field.ContextType
import ish.oncourse.common.field.FieldProperty
import ish.oncourse.common.field.PropertyGetSet
import ish.oncourse.common.field.PropertyGetSetFactory
import ish.oncourse.model.Contact
import ish.oncourse.willow.model.field.Field

import static ish.oncourse.willow.functions.ContactDetailsBuilder.getContext

@CompileStatic
class SubmitContactFields {
    
    
    void submitContactFields(Contact contact, List<Field> fields) {

        PropertyGetSetFactory factory = new PropertyGetSetFactory('ish.oncourse.common')
        PropertyGetSet getSet 
        
        fields.each { f ->
            ContextType contextType = FieldProperty.getByKey(f.key).contextType
            Object context =  getContext.call(contextType, contact)
            getSet = factory.get(f as FieldInterface, context)
        }
    }
}
