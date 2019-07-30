package ish.oncourse.willow.checkout.functions

import ish.oncourse.model.College
import org.apache.cayenne.BaseDataObject
import org.apache.cayenne.ObjectContext

abstract class Get<T extends BaseDataObject> {

    ObjectContext context
    College college
    String id

    Get(ObjectContext context, College college, String id) {
        this.context =  context
        this.college = college
        this.id = id
    }
    
    abstract T get()
}
