package ish.oncourse.willow.service

import ish.oncourse.willow.model.*

import javax.validation.constraints.NotNull
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

class ShoppingCartService {
    @GET()
    @Path("/sc/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    ShoppingCart getShoppingCart(@NotNull @PathParam("id") String id) {
        new ShoppingCart().with {
            it.id = UUID.randomUUID()
            it.contact = new Contact().with { c ->
                c.id = 1
                c.firstName = 'Andrei'
                c.lastName = 'Koira'
                c.email = 'andrey.koyro@objectstyle.com'
                c
            }
            it.classes.add(new CourseClass().with { cc ->
                cc.id = 1
                cc.course = new Course().with { co ->
                    co.id = 1
                    co.code = 'C1'
                    co.name = 'Course 1'
                    co.description = 'Course 1 description'
                    co
                }
                cc.code = '1'
                cc
            })
            it.products.add(new Product().with { p ->
                p.id = 1
                p.name = 'Product 1'
                p.sku = 'P1'
                p
            })
            it.vouchers.add(new Voucher().with { v ->
                v.id = 1
                v.sku = 'V1'
                v
            })
            it.promotions.add(new Promotion().with { p ->
                p.id = 1
                p.code = 'P1'
                p.name = 'Promotion 1'
                p
            })
            it
        }
    }
}
