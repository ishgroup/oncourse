package ish.oncourse.test.context

import com.github.javafaker.Faker
import ish.oncourse.model.College
import ish.oncourse.model.Room
import ish.oncourse.model.Site
import org.apache.cayenne.ObjectContext

/**
 * Created by alex on 11/15/17.
 */
class CRoom {
    private ObjectContext objectContext
    private Faker faker = DataContext.faker

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

    CRoom timeZone(String timeZone){
        room.site.timeZone = timeZone
        this
    }

    CRoom build(){
        objectContext.commitChanges()
        this
    }

    CRoom location(BigDecimal longitude, BigDecimal latitude){
        room.site.longitude = longitude
        room.site.latitude = latitude
        this
    }

    static CRoom instance(ObjectContext context, Site site){
        CRoom builder = new CRoom()
        builder.objectContext = context
        builder.createRoom(site)

        builder
    }

    static CRoom instance(ObjectContext context, College college){
        CRoom builder = new CRoom()
        CSite cSite = CSite.instance(context, college)
        builder.objectContext = context
        builder.createRoom(cSite.site)

        builder
    }

    private CRoom createRoom(Site site){
        room = objectContext.newObject(Room)
        room.name = faker.address().fullAddress()
        room.site = objectContext.localObject(site)
        room.college = site.college
        
        this
    }
}
