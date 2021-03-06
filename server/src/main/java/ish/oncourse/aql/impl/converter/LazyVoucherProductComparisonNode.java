/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.aql.impl.converter;

import ish.oncourse.aql.impl.Op;

/**
 * Subnode which based on product comparison code.
 * Comparing will do on "name" and "sku" fields.
 */
public class LazyVoucherProductComparisonNode extends LazyProductComparisonNode {

    LazyVoucherProductComparisonNode(Op op) {
        super(op);
    }
}
