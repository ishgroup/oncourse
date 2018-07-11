package ish.oncourse.willow.functions.field

import ish.oncourse.model.Field

class MergeFields {

    private List<Field> fieldsToMerge

    MergeFields(List<Field> fieldsToMerge) {
        this.fieldsToMerge = fieldsToMerge
    }

    Set<Field> getFields() {
        fieldsToMerge.groupBy { it.property }                      // group by unique key to map like [key1: [field1, field2,...], key2: [field3, field4,...],... ]
                .collect { k, v ->  v.sort { !it.mandatory }[0] }  // get first mandatory field (if mandatory field there) from each list
                .toSet()
    }
}
