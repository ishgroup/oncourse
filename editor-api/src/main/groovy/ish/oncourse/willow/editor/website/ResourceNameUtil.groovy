package ish.oncourse.willow.editor.website

import groovy.transform.CompileStatic
import ish.oncourse.model.WebContent
import ish.oncourse.model.WebNodeType
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.PersistentObject
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.Property
import org.apache.cayenne.query.ObjectSelect

//@CompileStatic
class ResourceNameUtil {
    
    static <T extends PersistentObject> String getAvailableName(Name name, ObjectContext ctx, Expression... qualifiers) {
        Integer nextNumber = 1
        String likeName = "$name.defaultName (%)"
        List<String> namesList = (ObjectSelect.columnQuery(name.clazz, name.property)
                .where(name.property.like(likeName)) 
                & qualifiers).select(ctx)
        
        if (!namesList.empty) {
            String regex = /${name.defaultName} \(\d+\)/
            Integer integer = namesList.findAll { it ==~ regex }
                    .collect { it.find(/\d+/).toInteger() }
                    .max() as Integer
            
            nextNumber = integer ? ++integer: 1
        }
        return likeName.replace('%', nextNumber.toString())
    }
    
    static enum Name {
        
        BLOCK_NAME(WebContent, WebContent.NAME, 'New block'),
        THEME_NAME(WebNodeType, WebNodeType.NAME, 'New theme')
        
        private Class<? extends PersistentObject> clazz
        private Property<String> property
        private String defaultName


        private Name(Class<? extends PersistentObject> clazz, Property<String> property, String defaultName) {
            this.clazz = clazz
            this.property = property
            this.defaultName = defaultName
        }

        Class<? extends PersistentObject> getClazz() {
            return clazz
        }

        Property<String> getProperty() {
            return property
        }

        String getDefaultName() {
            return defaultName
        }
    }
}
