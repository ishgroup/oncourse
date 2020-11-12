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

package ish.oncourse.server.print

import ish.math.Money

import java.text.DecimalFormat
import java.text.FieldPosition
import java.text.NumberFormat
import java.text.ParsePosition

/**
 * This Format is used in jasperReport engine and helps to format ish.math.Money types correctly
 * Main logic is in "format(Object number, StringBuffer toAppendTo, FieldPosition pos)"
 * Logic: add special format behavior for ish.math.Money value type
 */
class AngelNumberFormat extends NumberFormat {

    private NumberFormat formatter

    private AngelNumberFormat(){
        super()
    }

    static NumberFormat valueOf(String pattern, Locale locale){
        AngelNumberFormat provider = new AngelNumberFormat()
        provider.init(pattern, locale)
        provider
    }

    private init(String pattern, Locale locale) {
        formatter = locale ? getInstance(locale) : getInstance()

        if (formatter instanceof DecimalFormat) {
            ((DecimalFormat) formatter).applyPattern(pattern)
        }
    }

    @Override
    StringBuffer format(Object number, StringBuffer toAppendTo, FieldPosition pos) {
        return formatter.format(number instanceof Money ? ((Money) number).toBigDecimal() : number, toAppendTo, pos)
    }

    @Override
    StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
        return formatter.format(number, toAppendTo, pos)
    }

    @Override
    StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
        return formatter.format(number, toAppendTo, pos)
    }

    @Override
    Number parse(String source, ParsePosition parsePosition) {
        return formatter.parse(source, parsePosition)
    }
}
