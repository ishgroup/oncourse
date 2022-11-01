/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cayenne

import com.sun.istack.NotNull
import groovy.json.JsonSlurper
import ish.math.Money
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._Checkout
import org.apache.cayenne.query.ObjectSelect

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

    @API @Nullable
    Map getData() {
        def cart = super.getShoppingCart()
        if (!cart) return null

        return new JsonSlurper().parseText(cart) as Map
    }

    private List<Map> getContactsMap(){
        return data?.get(CONTACTS_CART_KEY) as List<Map>
    }


    /**
     * Note: all products with ids from shopping cart should be replicated
     * @return list of all products, whose ids shopping cart contains
     */
    @API
    @NotNull
    List<Product> getShoppingCartProducts() {
        def contactsMaps = contactsMap
        def productsIds = (contactsMaps.collect {((it.get(PRODUCTS_CART_KEY) as List<Map>)*.get("id")).collect {Long.parseLong(it as String)}}
                .flatten() as List<Long>).findAll()
        return ObjectSelect.query(Product)
                .where(Product.WILLOW_ID.in(productsIds)).select(context)
    }

    /**
     * Note: all products with ids from shopping cart should be replicated
     * @return list of all course classes, whose ids shopping cart contains
     */
    @API
    @NotNull
    List<CourseClass> getShoppingCartClasses() {
        def contactsMaps = contactsMap
        def classesIds = (contactsMaps.collect {((it.get(CLASSES_CART_KEY) as List<Map>)*.get("id")).collect {Long.parseLong(it as String)}}
                .flatten() as List<Long>).findAll()
        return ObjectSelect.query(CourseClass)
                .where(Product.WILLOW_ID.in(classesIds)).select(context)
    }


    /**
     * @param productId - angel id of product
     * @return quantity of product with this id into shopping cart or null, if product not found
     */
    @API
    @Nullable
    Integer getShoppingCartProductQuantity(Long productId){
        def contactsMaps = contactsMap
        def products = (contactsMaps.collect {it.get(PRODUCTS_CART_KEY)as List<Map>}.flatten() as List<Map>).findAll()
        return products.find {Long.parseLong(it.get("id") as String).equals(productId)}?.get("quantity") as Integer
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
