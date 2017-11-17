package ish.oncourse.entityBuilder

import ish.oncourse.model.College
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Room
import ish.oncourse.model.Site
import org.apache.cayenne.ObjectContext

/**
 * Created by alex on 11/15/17.
 */
class SiteBuilder {
    private ObjectContext objectContext
    private Site site

    private SiteBuilder(ObjectContext context){
        objectContext = context
    }

    SiteBuilder newDefaultRoom(){
        RoomBuilder.instance(objectContext, site).build()
        this
    }

    SiteBuilder newDefaultRoom(CourseClass courseClass){
        RoomBuilder.instance(objectContext, site).addCourseClass(courseClass).build()
        this
    }

    SiteBuilder addRoom(Room room){
        site.addToRooms(objectContext.localObject(room))
        this
    }

    SiteBuilder isWebVisible(boolean visible){
        site.isWebVisible = visible
        this
    }

    SiteBuilder name(String name){
        site.name = name
        this
    }

    SiteBuilder isVirtual(boolean virtual){
        site.isVirtual = virtual
        this
    }

    SiteBuilder postcode(String postcode){
        site.postcode = postcode
        this
    }

    SiteBuilder state(String state){
        site.state = state
        this
    }

    SiteBuilder suburb(String suburb){
        site.suburb = suburb
        this
    }

    SiteBuilder timeZone(String timeZone){
        site.timeZone = timeZone
        this
    }

    Site build(){
        objectContext.commitChanges()
        site
    }

    static SiteBuilder instance(ObjectContext context, College college){
        SiteBuilder builder = new SiteBuilder(context)
        builder.createDefaultSite(college)
        builder
    }

    private SiteBuilder createDefaultSite(College college){
        site = objectContext.newObject(Site)
        site.college = college
        site.isVirtual = false
        this
    }
}
