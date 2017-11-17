package ish.oncourse.entityBuilder

import ish.oncourse.model.College
import ish.oncourse.model.Room
import ish.oncourse.model.Session
import ish.oncourse.model.Site
import org.apache.cayenne.ObjectContext

/**
 * Created by alex on 11/15/17.
 */
class RoomBuilder {
    private ObjectContext objectContext
    private Room room

    private RoomBuilder(ObjectContext context){
        objectContext = context
    }

    RoomBuilder newDefaultSite(College college){
        room.site = SiteBuilder.instance(objectContext, college).build()
        this
    }

    RoomBuilder newSite(String siteName, College college){
        room.site = SiteBuilder.instance(objectContext, college).name(siteName).build()
        this
    }

    RoomBuilder name(String name){
        room.name = name
        this
    }

    RoomBuilder capacity(int capacity){
        room.capacity = capacity
        this
    }

    RoomBuilder directions(String directions){
        room.directions = directions
        this
    }

    RoomBuilder directionsTextile(String directionsTextile){
        room.directionsTextile = directionsTextile
        this
    }

    RoomBuilder facilities(String facilities){
        room.facilities = facilities
        this
    }

    RoomBuilder facilitiesTextile(String facilitiesTextile){
        room.facilities = facilitiesTextile
        this
    }

    RoomBuilder addSession(Session session){
        room.addToSessions(objectContext.localObject(session))
        this
    }

    Room build(){
        objectContext.commitChanges()
        room
    }

    static RoomBuilder instance(ObjectContext context, Site site){
        RoomBuilder builder = new RoomBuilder(context)
        builder.createDefaultRoom(site)
        builder
    }

    private RoomBuilder createDefaultRoom(Site site){
        room = objectContext.newObject(Room)
        room.site = objectContext.localObject(site)
        room.college = site.college
        this
    }
}
