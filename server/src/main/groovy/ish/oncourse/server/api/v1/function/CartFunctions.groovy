/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.function

import groovy.json.JsonSlurper
import ish.common.types.ProductType
import ish.common.types.TypesUtil
import ish.oncourse.server.api.v1.model.CartContactIdsDTO
import ish.oncourse.server.api.v1.model.CartDTO
import ish.oncourse.server.api.v1.model.CartIdsDTO
import ish.oncourse.server.api.v1.model.CartObjectDataDTO
import ish.oncourse.server.api.v1.model.ProductTypeDTO
import ish.oncourse.server.cayenne.Checkout
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Product
import ish.oncourse.server.cayenne.WaitingList
import ish.oncourse.server.cayenne.glue.CayenneDataObject
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Property
import org.apache.cayenne.query.ObjectSelect

import java.time.ZoneOffset

class CartFunctions {
    public static final String CLASSES_KEY = "classes"
    public static final String WAITING_KEY = "waitingCourses"
    public static final String PRODUCTS_KEY = "products"

    private static Map<String, Class<? extends CayenneDataObject>> classes = new HashMap<String, Class<? extends CayenneDataObject>>() {
        {
            put(CLASSES_KEY, CourseClass.class)
            put(WAITING_KEY, WaitingList.class)
            put(PRODUCTS_KEY, Product.class)
        }
    }

    static CartDTO toRestCart(Checkout checkout) {
        new CartDTO().with { dto ->
            dto.createdOn = checkout.createdOn?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()
            dto.totalValue = checkout.totalValue.toBigDecimal()
            dto.id = checkout.id
            dto
        }
    }

    static CartIdsDTO cartDataIdsOf(Checkout checkout) {
        def cartAsMap = cartAsMapOf(checkout)
        if(cartAsMap == null)
            return new CartIdsDTO()

        new CartIdsDTO().with { it ->
            it.payerId = checkout.payer.getId()
            it.contacts = cartContactIdsOf(cartAsMap.contacts as List<Map>, checkout.getContext())
            it
        }
    }

    static List<CartContactIdsDTO> contactCartsOf(Checkout checkout, Long contactWillowId, String column) {
        contactCartsOf(checkout, contactWillowId, Set.of(column))
    }

    static List<CartContactIdsDTO> contactCartsOf(Checkout checkout, Long contactId, Set<String> columns = classes.keySet()){
        def checkoutCart = cartAsMapOf(checkout)

        if (checkoutCart == null)
            return new ArrayList<CartContactIdsDTO>()

        def suitableContacts = (checkoutCart.contacts as List<Map>).findAll { parseAsLong(it.contactId) == contactId }
        return cartContactIdsOf(suitableContacts, checkout.getContext(), columns)
    }

    static Map cartAsMapOf(Checkout checkout){
        def cartAsJson = checkout.shoppingCart
        if (cartAsJson == null)
            return null

        new JsonSlurper().parseText(cartAsJson) as Map
    }

    private static List<CartContactIdsDTO> cartContactIdsOf(List<Map> cartContacts, ObjectContext context, Set<String> columns = classes.keySet()) {
        return cartContacts.collect { cartContactIdsOf(it, context, columns) }
    }

    private static CartContactIdsDTO cartContactIdsOf(Map cartContact, ObjectContext context, Set<String> columns) {
        def cartContactIds = new CartContactIdsDTO()
        cartContactIds.contactId = cayenneObjectFrom(parseAsLong(cartContact.contactId), Contact, context).getId()

        if(columns.contains(CLASSES_KEY))
            cartContactIds.classIds = mapToCartObjects(cartContact, CLASSES_KEY, context)

        if(columns.contains(PRODUCTS_KEY))
            cartContactIds.productIds = mapToCartObjects(cartContact, PRODUCTS_KEY, context)

        if(columns.contains(WAITING_KEY))
            cartContactIds.waitingCoursesIds = mapToCartObjects(cartContact, WAITING_KEY, context)
        cartContactIds
    }

    private static List<CartObjectDataDTO> mapToCartObjects(Map cartContact, String listKey, ObjectContext context) {
        def maps = mapToSubList(cartContact, listKey)
        Class<? extends CayenneDataObject> myClass = classes.get(listKey)
        maps.collect { mapToCartObject(it, myClass, context) }
    }

    private static List<Map> mapToSubList(Map map, String listKey) {
        map.containsKey(listKey) ? map.get(listKey) as List<Map> : new ArrayList<Map>()
    }

    private static CartObjectDataDTO mapToCartObject(Map data, Class<? extends CayenneDataObject> angelClass, ObjectContext context) {
        new CartObjectDataDTO().with { it ->
            it.selected = data.selected
            def cayenneObject = cayenneObjectFrom(parseAsLong(data.id), angelClass, context)
            it.id = cayenneObject.getValueForKey("id") as Long
            if (angelClass.equals(Product.class)) {
                it.productType = ProductTypeDTO.fromValue(TypesUtil.getEnumForDatabaseValue((cayenneObject as Product).type, ProductType).toString())
                if (data.containsKey("quantity"))
                    it.quantity = parseAsLong(data.quantity)
            }
            it
        }
    }

    private static Long parseAsLong(Object value) {
        Long.parseLong(value as String)
    }

    private static <T extends CayenneDataObject> T cayenneObjectFrom(Long willowId, Class<T> angelClass, ObjectContext context) {
        def willowIdProperty = Property.create("willowId", Long.class)
        return ObjectSelect.query(angelClass)
                .where(willowIdProperty.eq(willowId))
                .selectOne(context)
    }
}
