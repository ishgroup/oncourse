/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cayenne

import ish.math.Money
import ish.oncourse.server.cayenne.glue._Cart

class Cart extends _Cart{
    public static final String ESTIMATED_VALUE_KEY = "estimatedValue"

    Money getEstimatedValue(){
        def productsPrice = products.collect { it.priceIncTax }.sum() as Money
        def classesPrice = classes.collect {it.feeIncGst}.sum() as Money
        return productsPrice.add(classesPrice)
    }
}
