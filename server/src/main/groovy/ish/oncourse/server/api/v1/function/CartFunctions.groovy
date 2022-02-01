/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.function

import groovy.json.JsonSlurper
import ish.oncourse.server.api.v1.model.CartDTO
import ish.oncourse.server.api.v1.model.CartIdsDTO
import ish.oncourse.server.cayenne.Checkout

import java.time.ZoneOffset

class CartFunctions {
    static CartDTO toRestCart(Checkout checkout){
        new CartDTO().with{dto ->
            dto.createdOn = checkout.createdOn?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()
            dto.totalValue = checkout.totalValue.toBigDecimal()
            dto.id = checkout.id
            dto
        }
    }

    static CartIdsDTO cartDataIdsOf(Checkout checkout){
        def cartAsJson = checkout.shoppingCart
        def cartIds = new CartIdsDTO()
        if(cartAsJson == null)
            return cartIds

        def cartAsMap = new JsonSlurper().parseText(cartAsJson) as Map
        cartIds.payerId = parseAsLong(cartAsMap.payerId)

        def cartContacts = (cartAsMap.contacts as List<Map>)
        cartIds.contactIds = mapToIds(cartContacts, "contactId")

        cartIds.classIds = mapToIds(flatMapByKey(cartContacts, "classes"))
        cartIds.waitingCoursesIds = mapToIds(flatMapByKey(cartContacts,"waitingCourses"))
        cartIds.productIds = mapToIds(flatMapByKey(cartContacts,"products"))
        return cartIds
    }

    private static List<Map> flatMapByKey(List<Map> objects, String key){
        objects.collect {it.containsKey(key) ? it.get(key) : new ArrayList<>() as List<Map>}.flatten() as List<Map>
    }

    private static List<Long> mapToIds(List<Map> objects, String key = "id"){
        objects.collect {parseAsLong(it.get(key))} as List<Long>
    }

    private static Long parseAsLong(Object value){
        Long.parseLong(value as String)
    }
}
