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
        Checkout checkout = findCheckoutById(id) ?: context.newObject(Checkout)
        checkout.UUID = id
        checkout.shoppingCart = shoppingCart
        checkout.college = college
        context.commitChanges()
    }

    @Override
    void delete(String id) {
        Checkout checkout = getCheckout(id)
        checkout.getObjectContext().deleteObject(checkout)
    }

    @Override
    String get( String id) {
        Checkout checkout = getCheckout(id)
        return checkout.shoppingCart
    }

    private Checkout getCheckout(String id) {
        Checkout checkout = findCheckoutById(id)
        if (!checkout) {
            ValidationError validationError = new ValidationError()
            validationError.formErrors << "There is no shopping cart with id: " + id
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity(validationError).build())
        }
        checkout
    }

    private Checkout findCheckoutById(String id) {
        ObjectSelect.query(Checkout).where(Checkout.UUID_.eq(id)).selectOne(cayenneService.newContext())
    }
}

