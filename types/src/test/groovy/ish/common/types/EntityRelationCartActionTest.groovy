/*
 * Copyright ish group pty ltd 2026.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.common.types

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import static ish.common.types.EntityRelationCartAction.*

@CompileStatic
class EntityRelationCartActionTest {

    @Test
    void testAddedToCart() {
        Assertions.assertEquals([ADD_ALLOW_REMOVAL, ADD_NO_REMOVAL], ADDED_TO_CART)
        Assertions.assertTrue(ADD_ALLOW_REMOVAL.addedToCart)
        Assertions.assertTrue(ADD_NO_REMOVAL.addedToCart)
        Assertions.assertFalse(SUGGESTION.addedToCart)
        Assertions.assertFalse(NO_ACTION.addedToCart)
    }

    @Test
    void testRemovable() {
        Assertions.assertTrue(ADD_ALLOW_REMOVAL.removable)
        Assertions.assertFalse(ADD_NO_REMOVAL.removable)
    }
}
