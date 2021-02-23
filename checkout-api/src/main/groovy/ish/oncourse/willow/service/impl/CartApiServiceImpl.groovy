package ish.oncourse.willow.service.impl

import com.google.inject.Inject
import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.model.Checkout
import ish.oncourse.model.College
import ish.oncourse.willow.model.common.ValidationError
import ish.oncourse.willow.service.CartApi
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response

class CartApiServiceImpl implements CartApi {

    private CayenneService cayenneService
    private CollegeService collegeService

    @Inject
    ContactApiServiceImpl(CayenneService cayenneService, CollegeService collegeService) {
        this.cayenneService = cayenneService
        this.collegeService = collegeService
    }

    @Override
    void create(String id, String shoppingCart) {
        ObjectContext context = cayenneService.newContext()
        College college = collegeService.college
        Checkout checkout = findCheckoutById(context, id) ?: context.newObject(Checkout)
        checkout.UUID = id
        checkout.shoppingCart = shoppingCart
        checkout.college = context.localObject(college)
        context.commitChanges()
    }

    @Override
    void delete(String id) {
        ObjectContext context = cayenneService.newContext()
        Checkout checkout = findCheckoutById(context, id)
        if (checkout) {
            context.deleteObject(checkout)
            context.commitChanges()
        }
    }

    @Override
    String get(String id) {
        Checkout checkout = findCheckoutById(cayenneService.newContext(), id)
        return checkout?.shoppingCart
    }

    private Checkout findCheckoutById(ObjectContext context, String id) {
        College college = collegeService.college
        ObjectSelect.query(Checkout)
                .where(Checkout.COLLEGE.eq(college).andExp(Checkout.UUID_.eq(id)))
                .selectFirst(context)
    }
}

