
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.apache.cayenne.query.ObjectSelect


long sortOrder = ObjectSelect.query(CustomFieldType)
        .orderBy(CustomFieldType.SORT_ORDER.desc())
        .selectFirst(context)?.sortOrder?:0

def jsonSlurper = new JsonSlurper()
List<Object> obj = jsonSlurper.parse(customFieldTypes) as List<Object>

obj.each { type ->
    String key = type['fieldKey']
    if (!ObjectSelect.query(CustomFieldType).where(CustomFieldType.KEY.eq(key)).selectFirst(context)) {
        CustomFieldType customFieldType = context.newObject(CustomFieldType)
        customFieldType.key = key
        customFieldType.name = type['name']
        customFieldType.isMandatory = type['isMandatory'] as Boolean
        String entityIdentifier = type['entityIdentifier']
        customFieldType.entityIdentifier = (entityIdentifier in ["Student", "Tutor"]) ? "Contact" : entityIdentifier 
        customFieldType.dataType = DataType.valueOf(type['dataType'])
        
        customFieldType.sortOrder = ++sortOrder
        if (customFieldType.dataType in [DataType.LIST, DataType.MAP]) {
            customFieldType.defaultValue =  JsonOutput.toJson(type['choices'])
        } 
        context.commitChanges()
    } else {
        logger.warn("Field $key already exist")
    }
}

