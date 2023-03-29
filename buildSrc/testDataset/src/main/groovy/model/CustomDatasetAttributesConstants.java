/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package model;import java.util.Calendar;

public class CustomDatasetAttributesConstants {
    public static final int FUTURE_YEAR_NUMBER = Calendar.getInstance().get(Calendar.YEAR) + 2;

    public static final int PREVIOUS_YEAR_NUMBER = Calendar.getInstance().get(Calendar.YEAR) - 1;
}
