package ish.oncourse.willow.functions

import groovy.transform.CompileStatic
import ish.common.types.TypesUtil
import ish.oncourse.cayenne.FieldInterface
import ish.oncourse.common.field.ContextType
import ish.oncourse.common.field.FieldProperty
import ish.oncourse.common.field.PropertyGetSet
import ish.oncourse.common.field.PropertyGetSetFactory
import ish.oncourse.model.Contact
import ish.oncourse.model.Country
import ish.oncourse.model.Language
import ish.oncourse.util.FormatUtils
import ish.oncourse.utils.DateUtils
import ish.oncourse.willow.model.field.DataType
import ish.oncourse.willow.model.field.Field
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.BadRequestException
import static ish.oncourse.willow.functions.ContactDetailsBuilder.getContext
import static ish.oncourse.willow.model.field.DataType.*
import static ish.oncourse.willow.model.field.DataType.BOOLEAN
import static ish.oncourse.willow.model.field.DataType.STRING

@CompileStatic
class SubmitContactFields {

    final static Logger logger = LoggerFactory.getLogger(CreateOrGetContact.class)

    void submitContactFields(Contact contact, List<Field> fields, ObjectContext objectContext) {

        PropertyGetSetFactory factory = new PropertyGetSetFactory('ish.oncourse.common')
        PropertyGetSet getSet 
        
        fields.each { f ->

            if (StringUtils.trimToNull(f.value)) {

                FieldProperty  property = FieldProperty.getByKey(f.key)
                Object context =  getContext.call(property.contextType, contact)
                getSet = factory.get([property: {property.key}] as FieldInterface, context)

                switch (f.dataType) {
                    case STRING:
                        getSet.set(f.value)
                        break
                    case BOOLEAN:
                        getSet.set(Boolean.valueOf(f.value))
                        break
                    case DATE:
                    case DATETIME:
                        getSet.set(Date.parse(FormatUtils.DATE_FORMAT_ISO8601, f.value))
                        break
                    case INTEGER:
                        getSet.set(Integer.valueOf(f.value))
                        break
                    case COUNTRY:
                        getSet.set(ObjectSelect.query(Country).where(Country.NAME.eq(f.value)).selectFirst(objectContext))
                        break
                    case LANGUAGE:
                        getSet.set(ObjectSelect.query(Language).where(Language.NAME.eq(f.value)).selectFirst(objectContext))
                        break
                    case ENUM:
                        getSet.set(TypesUtil.getEnumForDatabaseValue(f.value, this.class.classLoader.loadClass(f.enumType)))
                        break
                    default:
                        logger.error("unsupported type for field value: $f")
                        throw new IllegalArgumentException()
                }
            }  else if (f.mandatory) {
                logger.error("${f.name} required: ${f}")
                throw new BadRequestException()
            }
        }

        objectContext.commitChanges()
    }
}
