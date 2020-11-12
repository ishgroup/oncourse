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
package ish.messaging;

import ish.oncourse.cayenne.PersistentObjectI;
import ish.oncourse.cayenne.SiteInterface;

import java.math.BigDecimal;

/**
 */
public interface ISite extends SiteInterface, PersistentObjectI {
	String STREET_KEY = "street";
	String SUBURB_KEY = "suburb";
	String POSTCODE_KEY = "postcode";

	BigDecimal MAX_LATITUDE = new BigDecimal("90.0");
	BigDecimal MIN_LATITUDE = new BigDecimal("-90.0");
	BigDecimal MAX_LONGITUDE = new BigDecimal("180.0");
	BigDecimal MIN_LONGITUDE = new BigDecimal("-180.0");

	String getName();
}
