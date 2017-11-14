package ish.oncourse.solr.model

import com.github.javafaker.Faker
import ish.common.types.PostcodeType
import ish.oncourse.model.PostcodeDb
import org.apache.cayenne.ObjectContext

/**
 * Created by alex on 11/13/17.
 */
class PostcodeDbContext {
    ObjectContext context
    private static Faker faker = new Faker()

    PostcodeDb postcodeDb(Long postcode){
        context.newObject(PostcodeDb).with {
            it.postcode = postcode
            it.suburb = 'some suburb'
            it.state = faker.address().stateAbbr()
            it.dc = faker.address().state()
            it.lat = faker.number().randomDouble(7, -37, 51)
            it.lon = faker.number().randomDouble(7, -5, 151)
            it.ishVersion = 1529L
            it.type = PostcodeType.DELIVERY_AREA
            context.commitChanges()
            it
        }
    }
}
