package ish.oncourse.willow.service

import groovy.transform.CompileStatic
import ish.oncourse.model.College
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.junit.Test
import org.mockito.Mockito

@CompileStatic
class CayenneTest {

    @Test
    void test_4_0_M5() {
        ObjectContext context = Mockito.mock(ObjectContext)

        ObjectSelect.query(ish.oncourse.model.Product.class).where(ish.oncourse.model.Product.COLLEGE.eq(new College())).select(context).each { ish.oncourse.model.Product p->
            println p.id
        }
    }
}
