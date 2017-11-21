package ish.oncourse.test.context

import ish.oncourse.model.College
import ish.oncourse.model.Site
import org.apache.cayenne.ObjectContext

/**
 * Created by alex on 11/15/17.
 */
class CSite {
    private ObjectContext objectContext
    Site site

    private CSite(){}

    CSite isWebVisible(boolean visible){
        site.isWebVisible = visible
        this
    }

    CSite name(String name){
        site.name = name
        this
    }

    CSite isVirtual(boolean virtual){
        site.isVirtual = virtual
        this
    }

    CSite postcode(String postcode){
        site.postcode = postcode
        this
    }

    CSite state(String state){
        site.state = state
        this
    }

    CSite suburb(String suburb){
        site.suburb = suburb
        this
    }

    CSite timeZone(String timeZone){
        site.timeZone = timeZone
        this
    }

    CSite build(){
        objectContext.commitChanges()
        this
    }

    static CSite instance(ObjectContext context, College college){
        CSite builder = new CSite()
        builder.objectContext = context

        builder.site = builder.objectContext.newObject(Site)
        builder.site.college = college
        builder.site.isVirtual = false
        builder.objectContext.commitChanges()

        builder
    }
}
