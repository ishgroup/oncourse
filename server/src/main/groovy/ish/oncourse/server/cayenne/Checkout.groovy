/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cayenne

import com.sun.istack.NotNull
import ish.math.Money
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._Checkout

import javax.annotation.Nonnull
import javax.annotation.Nullable
/**
 * A shopping cart record typically created by a user who abandoned their cart during checkout on the onCourse website
 */
@API
@QueueableEntity
class Checkout extends _Checkout implements Queueable {
    private static String CONTACTS_CART_KEY = "contacts"
    private static String PRODUCTS_CART_KEY = "products"
    private static String CLASSES_CART_KEY = "classes"

    /**
     * @return the date and time this record was created
     */
    @API @Override @Nonnull
    Date getCreatedOn() {
        return super.getCreatedOn()
    }

    /**
     * @return the date and time this record was modified
     */
    @API @Override @Nonnull
    Date getModifiedOn() {
        return super.getModifiedOn()
    }


    /**
     * Note: all products with ids from shopping cart should be replicated
     * @return list of all products, whose ids shopping cart contains
     */
    @API
    @NotNull
    List<Product> getShoppingCartProducts() {
        return contactRelations.findAll {it instanceof CheckoutProductRelation}
                .collect{(it as CheckoutProductRelation).relatedObject}
    }

    /**
     * Note: all products with ids from shopping cart should be replicated
     * @return list of all course classes, whose ids shopping cart contains
     */
    @API
    @NotNull
    List<CourseClass> getShoppingCartClasses() {
        return contactRelations.findAll {it instanceof CheckoutCourseClassRelation}
                .collect {(it as CheckoutCourseClassRelation).relatedObject}
    }


    /**
     * @param productId - angel id of product
     * @return quantity of product with this id into shopping cart or null, if product not found
     */
    @API
    @Nullable
    Integer getShoppingCartProductQuantity(Long productId){
        def productRelation =  contactRelations.find {
            it instanceof CheckoutProductRelation && it.relatedObject.id == productId
        }
        return productRelation ? (productRelation as CheckoutProductRelation).quantity : null
    }

    /**
     * @return the value of te shopping cart
     */
    @API @Override @Nonnull
    Money getTotalValue() {
        return super.getTotalValue()
    }

    /**
     * @return the contact set as the payer in the shopping cart
     */
    @API @Override @Nullable
    Contact getPayer() {
        return super.getPayer()
    }
}
