/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cayenne

import groovy.json.JsonSlurper
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
