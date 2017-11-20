package ish.oncourse.test.context

import ish.oncourse.model.Room
import ish.oncourse.model.Site
import org.apache.cayenne.ObjectContext

/**
 * Created by alex on 11/15/17.
 */
class CRoom {
    private ObjectContext objectContext
    Room room

    private CRoom(){}

    CRoom name(String name){
        room.name = name
        this
    }

    CRoom capacity(int capacity){
        room.capacity = capacity
        this
    }

    CRoom directions(String directions){
        room.directions = directions
        this
    }

    CRoom directionsTextile(String directionsTextile){
        room.directionsTextile = directionsTextile
        this
    }

    CRoom facilities(String facilities){
        room.facilities = facilities
        this
    }

    CRoom facilitiesTextile(String facilitiesTextile){
        room.facilities = facilitiesTextile
        this
    }

    CRoom build(){
        objectContext.commitChanges()
        this
    }

    static CRoom instance(ObjectContext context, Site site){
        CRoom builder = new CRoom()
        builder.objectContext = context

        builder.room = builder.objectContext.newObject(Room)
        builder.room.site = builder.objectContext.localObject(site)
        builder.room.college = site.college

        builder
    }
}
