package ish.oncourse.test.context

import com.github.javafaker.Faker
import ish.common.types.PostcodeType
import ish.oncourse.model.PostcodeDb
import org.apache.cayenne.ObjectContext

/**
 * Created by alex on 11/13/17.
 */
class CPostcodeDb {
    private ObjectContext objectContext
    PostcodeDb postcodeDb

    private static Faker faker = DataContext.faker

    private CPostcodeDb(){}

    static CPostcodeDb instance(ObjectContext context, Long postcode){
        CPostcodeDb cPostcodeDb = new CPostcodeDb()
        cPostcodeDb.objectContext = context

        cPostcodeDb.postcodeDb = cPostcodeDb.objectContext.newObject(PostcodeDb)
        cPostcodeDb.postcodeDb.postcode = postcode
        cPostcodeDb.postcodeDb.suburb = 'some suburb'
        cPostcodeDb.postcodeDb.state = faker.address().stateAbbr()
        cPostcodeDb.postcodeDb.dc = faker.address().state()
        cPostcodeDb.postcodeDb.lat = faker.number().randomDouble(7, -37, 51)
        cPostcodeDb.postcodeDb.lon = faker.number().randomDouble(7, -5, 151)
        cPostcodeDb.postcodeDb.ishVersion = 1529L
        cPostcodeDb.postcodeDb.type = PostcodeType.DELIVERY_AREA
        cPostcodeDb
    }

    CPostcodeDb build(){
        objectContext.commitChanges()
        this
    }
}
