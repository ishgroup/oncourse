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

package ish.oncourse.server.export.avetmiss

import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.util.regex.Pattern

/**
 * This class is designed to parse a street address into component parts:
 * * building name
 * * unit number
 * * street number
 * * street name
 *
 * This is a pretty ugly hack, but required by the AVETMISS 7 standard.
 *
 * Some interesting regex to look at here: http://jgeocoder.svn.sourceforge.net/viewvc/jgeocoder/jgeocoder/src/main/java/net/sourceforge/jgeocoder/us/
 */
@CompileStatic
class AddressParser {

    private static final Logger logger = LogManager.getLogger()

    private String fullAddress
    private String building
    private String unit
    private String streetNumber
    private String streetName

    private static final String PUNCT_SPACE = ",.-/\\\\ \n\r\t"

    // Units that will be followed by a number
    private static final String UNITS = "unit|suite|floor|room|lot|space|stop|trailer|box|dept|department|apartment|apt|slip|pier|hangar|shop|level"

    // Units that will not be followed by a number
    private static final String UNUMBERED_UNITS = "basement|front|lobby|lower|office|penthouse|rear|side|upper"

    private static final Pattern UNIT_PATTERN = Pattern.compile("([^\\d]+[,\\s]+)??" // building name contains anything which is not digits. This is optional and non-greedy.
            + "((?:(?:" + UNITS + ")\\s+.*)+)" //units, space, digit, then optionally more digits or comma/space. ?: means non-capturing group. We can have more than one of these groups.
            + "[,\\s]+(\\d+[a-z]*)" // street number is space/comma followed by a digit and some optional letters
            + "[,\\s]+([a-z\\s]+)", // street name
            Pattern.CASE_INSENSITIVE)

    private static final Pattern NUMBERLESS_PATTERN = Pattern.compile("(^|.*?\\W+)"
            + "(" + UNUMBERED_UNITS + ")"
            + "\\W+(\\d+\\w+)?"
            + "(.*)"
            , Pattern.CASE_INSENSITIVE)

    private static final Pattern UNITSTREET_PATTERN = Pattern.compile("([^\\d]+[,\\s]+)??" // Building
            + "(\\d+[a-z]*)[,\\s]+" // unit number
            + "(\\d+[a-z]*)" // streey number
            + "(.*)"
            , Pattern.CASE_INSENSITIVE)

    private static final Pattern STREET_PATTERN = Pattern.compile("([^\\d]+[,\\s]+)??" // Building
            + "(\\d+[a-z]*)" // street number is space/comma followed by a digit and some optional letters
            + "[,\\s]+([a-z\\s]+)", // street name
            Pattern.CASE_INSENSITIVE)

    private static final Pattern PUNCT_PATTERN = Pattern.compile("(.*),(.*)")
    private static final Pattern nonASCII = Pattern.compile("[^\\w\\s]")

    AddressParser(String fullAddress) {
        if (fullAddress != null) {
            //turn anything which isn't space or A-Z0-9a-z into commas, remove double spaces/commas, strip spaces off front and back,
            this.fullAddress = nonASCII.matcher(fullAddress).replaceAll(",").replaceAll("[\\s]+", " ").replaceAll("\\s*,+\\s*", ",").trim()
        } else {
            this.fullAddress = ""
        }
        parse()
    }

    private static final Pattern START_WITH_PO_BOX_PATTERN = Pattern.compile("^(p[.,]*o[.,\\s]+(box)*|gp*o\\s+(box)*|post\\s+office\\s+box)", Pattern.CASE_INSENSITIVE)

    /**
     * This does the heavy lifting to process the full address into parts
     */
    private void parse() {

        if (StringUtils.isBlank(fullAddress) || START_WITH_PO_BOX_PATTERN.matcher(fullAddress).find()) {
            setStreetName(null)
            setStreetNumber(null)
            setUnit(null)
            setBuilding(null)
            return
        }

        // Find a unit phrase
        // 1. start of string or some non-greedy text followed by whitespace or punctuation
        // 2. units keyword
        // 3. whitespace or punctuation followed by non-greedy characters
        // 4. more whitespace or punctuation and then the rest
        def m = UNIT_PATTERN.matcher(fullAddress)
        if (m.matches()) {
            logger.debug("Found unit keyword in {}", fullAddress)
            setBuilding(m.group(1))
            setUnit(m.group(2))
            setStreetNumber(m.group(3))
            setStreetName(m.group(4))
            return
        }

        // Find a numberless unit phrase
        m = NUMBERLESS_PATTERN.matcher(fullAddress)
        if (m.matches()) {
            logger.debug("Found numberless unit keyword in {}", fullAddress)
            setBuilding(m.group(1))
            setUnit(m.group(2))
            setStreetNumber(m.group(3))
            setStreetName(m.group(4))
            return
        }

        // Find a street number with a unit syntax
        // 1. something (non greedy)
        // 2. digits
        // * optional whitespace + punctuation + more optional whitespace
        // 3. digits and the remainder
        m = UNITSTREET_PATTERN.matcher(fullAddress)
        if (m.matches()) {
            logger.debug("Found unit number in {}", fullAddress)
            setBuilding(m.group(1))
            setUnit(m.group(2))
            setStreetNumber(m.group(3))
            setStreetName(m.group(4))
            return
        }

        // Find a street number
        m = STREET_PATTERN.matcher(fullAddress)
        if (m.matches()) {
            logger.debug("Found street number in {}", fullAddress)
            setBuilding(m.group(1))
            setUnit(null)
            setStreetNumber(m.group(2))
            setStreetName(m.group(3))
            return
        }

        // OK, no street number, how about a comma or other punctuation
        m = PUNCT_PATTERN.matcher(fullAddress)
        if (m.matches()) {
            logger.debug("Found punctuation in {}", fullAddress)
            setBuilding(m.group(1))
            setUnit(null)
            setStreetNumber(null)
            setStreetName(m.group(2))
            return
        }

        logger.debug("Found nothing: stuffing it all in the street for {}", fullAddress)
        setBuilding(null)
        setUnit(null)
        setStreetNumber(null)
        setStreetName(fullAddress)
    }

    String getBuilding() {
        return building
    }

    String getUnit() {
        return unit
    }

    String getStreetNumber() {
        return streetNumber
    }

    String getStreetName() {
        return streetName
    }


    void setBuilding(String building) {
        this.building = StringUtils.stripToEmpty(StringUtils.strip(building, PUNCT_SPACE))
    }

    void setUnit(String unit) {
        this.unit = StringUtils.stripToEmpty(StringUtils.strip(unit, PUNCT_SPACE))
    }

    void setStreetNumber(String streetNumber) {
        this.streetNumber = StringUtils.stripToEmpty(StringUtils.strip(streetNumber, PUNCT_SPACE))
    }

    void setStreetName(String streetName) {
        this.streetName = StringUtils.stripToEmpty(StringUtils.strip(streetName, PUNCT_SPACE))

        if (StringUtils.isBlank(this.streetName) || START_WITH_PO_BOX_PATTERN.matcher(this.streetName).find()) {
            this.streetName = "not specified"
        }
    }

}
