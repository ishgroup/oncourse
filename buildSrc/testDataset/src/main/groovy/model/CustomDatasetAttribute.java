/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package model;public enum CustomDatasetAttribute {

    FUTURE_YEAR("#future_year", String.valueOf(CustomDatasetAttributesConstants.FUTURE_YEAR_NUMBER)),

    PREVIOUS_YEAR("#previous_year", String.valueOf(CustomDatasetAttributesConstants.PREVIOUS_YEAR_NUMBER));

    private final String attributeMarker;
    private final String attributeValue;

    CustomDatasetAttribute(String attributeMarker, String attributeValue) {
        this.attributeMarker = attributeMarker;
        this.attributeValue = attributeValue;
    }

    public String getAttributeMarker() {
        return attributeMarker;
    }

    public String getAttributeValue() {
        return attributeValue;
    }
}
