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

package ish.oncourse.server.print;

import net.sf.jasperreports.engine.util.DefaultFormatFactory;
import org.apache.commons.lang3.StringUtils;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Goal: to make jasper report's format patterns work correct with ish.math.Money type
 */
public class AngelFormatFactory extends DefaultFormatFactory{

    @Override
    public NumberFormat createNumberFormat(String pattern, Locale locale) {
        return StringUtils.isBlank(pattern) ? null : AngelNumberFormat.valueOf(pattern, locale);
    }
}
