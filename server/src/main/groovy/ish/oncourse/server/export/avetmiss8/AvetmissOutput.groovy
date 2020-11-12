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

package ish.oncourse.server.export.avetmiss8

import ish.util.DatePattern
import ish.util.StringUtil
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.text.Format
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Since we cannot extend StringBuilder, this class is used to provide equivalent functionality,
 * and enhance that with functions for exporting fixed length text
 */
class AvetmissOutput {

    private static final Logger logger = LogManager.getLogger()
    private StringBuilder output

    private static final Format dateFormatter = new SimpleDateFormat("ddMMyyyy")
    private static final DateTimeFormatter localDateFormatter = DateTimeFormatter.ofPattern(DatePattern.ddMMyyyy)

    AvetmissOutput() {
        output = new StringBuilder(256)
    }

    @Override
    String toString() {
        return output.toString()
    }

    /**
     * Append a boolean value.
     *
     * @param data - the original data
     */
    protected void append(boolean data) {
        append(1, data ? "Y" : "N")
    }

    /**
     * Append a boolean value.
     *
     * @param data - the original data
     */
    protected void append(Boolean data) {
        if (data == null) {
            append(1, "@")
        } else {
            append(1, data ? "Y" : "N")
        }
    }

    /**
     * Append Date.
     *
     * @param data - the original data
     */
    protected void append(Date data) {
        if (data == null) {
            append(8, "00000000")
        } else {
            append(8, dateFormatter.format(data))
        }
    }

    /**
     * Append LocalDate.
     *
     * @param data - the original data
     */
    protected void append(LocalDate data) {
        if (data == null) {
            append(8, "00000000")
        } else {
            append(8, localDateFormatter.format(data))
        }
    }

    /**
     * Pads data with spaces and appends the bytes from a string of length <code>len</code>.
     *
     * @param len - the total width of characters allowed/adjusted to
     * @param data - the original data
     */
    protected void append(int len, String data) {
        append(len, data, ' ')
    }

    /**
     * Aligns data to the right and pads with 0 to length <code>len</code>.
     *
     * @param len - the total width of characters allowed/adjusted to
     * @param data - the original data
     */
    protected void append(int len, int data) {
        append(len, String.valueOf(data), '0', true)
    }

    /**
     * Aligns data to the right and pads with 0 to length <code>len</code>.
     *
     * @param len - the total width of characters allowed/adjusted to
     * @param data - the original data
     */
    protected void append(int len, Integer data) {
        if (data == null) {
            append(len, "", '@')
        } else {
            append(len, String.valueOf(data), '0', true)
        }
    }

    /**
     * Aligns data to the right and pads with 0 to length <code>len</code>.
     *
     * @param len - the total width of characters allowed/adjusted to
     * @param n - the original data
     */
    protected void append(int len, Number n) {
        append(len, n == null ? "" : n.toString(), '0', true)
    }

    /**
     * Aligns data to the left, padding if necessary and appends the bytes from a string of length <code>len</code>.
     *
     * @param padCharacter The character which is used to pad strings smaller than the output length.
     * @param len The total width of characters allowed/adjusted to
     */
    protected void append(int len, String data, String padCharacter) {
        append(len, data, padCharacter, false)
    }

    protected void append(int len, Object o, String padCharacter) {
        append(len, o == null ? null : o.toString(), padCharacter)
    }

    /**
     * Pads <code>data</code> to <code>len</code> characters aligning the original data as per <code>alignRight</code>.
     *
     * @param len - the total width of characters allowed/adjusted to
     * @param padCharacter - the character to pad with
     * @param data - the original data
     * @param alignRight - whether to align the original data to the right or not
     */
    protected void append(int len, String data, String padCharacter, boolean alignRight) {
        if (data == null) {
            append(len, "", padCharacter, alignRight)
        } else {
            output.append(StringUtil.padField(len, data.toUpperCase(), padCharacter as char, alignRight))
        }
    }

    protected void append(int len, Object o, String padCharacter, boolean alignRight) {
        append(len, o == null ? "" : o.toString(), padCharacter, alignRight)
    }

    /**
     * Just like the normal append, but don't uppercase the output
     * @param len
     * @param data
     */
    void appendCaseSensitive(int len, String data) {
        if (data == null) {
            append(len, "", ' ', false)
        } else {
            output.append(StringUtil.padField(len, data, ' ' as char, false))
        }
    }

    /**
     * Format a string as a phone number, stripping any illegal characters. We also add the Australian area code if necessary
     * @param data the original data
     * @param postcode the student's postcode
     */
    protected void telephoneAppend(String phone, String postcode) {
        if (phone != null) {
            phone = phone.replaceAll("[^0-9]", "")
        }

        // if the number doesn't already start with 0 we need to add an area code
        if (phone.length() > 0 && phone[0] != '0') {
            def statecode = avetmissPostCodeID(postcode)
            if (statecode == "01") {
                phone = "02" + phone // NSW
            }
            if (statecode == "02") {
                phone = "03" + phone  // VIC
            }
            if (statecode == "03") {
                phone = "07" + phone  // QLD
            }
            if (statecode == "04") {
                phone = "08" + phone  // SA
            }
            if (statecode == "05") {
                phone = "08" + phone  // WA
            }
            if (statecode == "06") {
                phone = "03" + phone  // TAS
            }
            if (statecode == "07") {
                phone = "08" + phone  // NT
            }
        }
        append(20, phone, ' ')
    }

    /**
     * Get the AVETMISS state code from a particular postcode.
     *
     * @param postCodeString Four digit Australian postcode.
     * @return AVETMISS state code or 99 for unknown.
     */
    protected static String avetmissPostCodeID(String postCodeString) {
        int postcode
        try {
            postcode = Integer.parseInt(postCodeString)
        } catch (NumberFormatException ignored) {
            logger.debug("Cannot parse postcode '{}' to int", postCodeString)
            return "99"
        }

        if (postcode < 1 || postcode > 9999) {
            return "99"
        }
        int postcode_thousands = (postcode / 1000) as int

        if (postCodeString.matches("260\\d") || postCodeString.matches("261[0-8]") || postCodeString.matches("29\\d{2}") || postCodeString.matches("02\\d{2}")) {
            // ACT - 2600 - 2618 and 29xx typical postcodes
            // 02xx Canberra PO Box/large users
            return "08"

        } else if (postcode_thousands == 1 || postcode_thousands == 2) {
            // NSW - 2xxx typical postcodes
            // 1xxx Sydney PO Box/large users
            // Exceptions ACT - 260x
            // 2610 - 2618
            // 29xx
            return "01"

        } else if (postcode_thousands == 3 || postcode_thousands == 8) {
            // VIC - 3xxx typical postcodes
            // 8xxx Melbourne PO Box/large users
            return "02"

        } else if (postcode_thousands == 4 || postcode_thousands == 9) {
            // QLD - 4xxx typical postcodes
            // 9xxx Brisbane PO Box/large users
            return "03"

        } else if (postcode_thousands == 5) {
            // SA - 5xxx typical postcodes
            return "04"

        } else if (postcode_thousands == 6) {
            // WA - 6xxx typical postcodes
            return "05"

        } else if (postcode_thousands == 7) {
            // TAS - 7xxx
            return "06"

        } else if (postcode >= 800 && postcode < 1000) {
            // NT - 08xx and 09xx typical postcodes
            return "07"

        } else if (postCodeString.matches("\\d{4}")) {
            // Assume that postcode is Australian as it has 4 digits
            // (ideally we would have a country field to do this too)
            return "09"
        }

        // Assume postcode is overseas as it has more/less than 4 digits
        return "99"
    }

}
