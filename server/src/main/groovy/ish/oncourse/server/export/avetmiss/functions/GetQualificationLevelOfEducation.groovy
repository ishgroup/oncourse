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

package ish.oncourse.server.export.avetmiss.functions

class GetQualificationLevelOfEducation {

    private String qualificationLevel

    private GetQualificationLevelOfEducation() {

    }

    static GetQualificationLevelOfEducation valueOf(String qualificationLevel) {
        def function = new GetQualificationLevelOfEducation()
        function.qualificationLevel = qualificationLevel
        return function
    }


    // ------------------
    // qualification level of education identifier p83
    // 211 graduate diploma
    // 213 professtional specicialist graduate diploma
    // 221 graduate certificate
    // 222 professtional specicialist graduate certificate
    // 311 bachelor degree honours
    // 312 bachelor degree pass
    // 411 advanced diploma
    // 413 associate degree
    // 421 diploma
    // 511 cert IV
    // 514 cert III
    // 521 cert II
    // 524 cert I
    // 611 year 12
    // 613 year 11
    // 621 year 10
    // 912 non-award
    // 991 statement of attainment
    // 992 bridging course
    // 999 other
    int get() {
        def level = 999

        if(qualificationLevel == null)
            return level

        if (qualificationLevel.contains("graduate diploma")) {
            level = 211
        } else if (qualificationLevel.contains("professional specialist qualification at graduate diploma level")) {
            level = 213
        } else if (qualificationLevel.contains("graduate certificate")) {
            level = 221
        } else if (qualificationLevel.contains("professional specialist qualification at graduate certificate level")) {
            level = 222
        } else if (qualificationLevel.contains("bachelor degree (honours)")) {
            level = 311
        } else if (qualificationLevel.contains("bachelor degree (pass)")) {
            level = 312
        } else if (qualificationLevel.contains("advanced diploma")) {
            level = 411
        } else if (qualificationLevel.contains("associate degree")) {
            level = 413
        } else if (qualificationLevel.contains("diploma")) {
            level = 421
        } else if (qualificationLevel.contains("certificate iv")) {
            level = 511
        } else if (qualificationLevel.contains("certificate iii")) {
            level = 514
        } else if (qualificationLevel.contains("certificate ii")) {
            level = 521
        } else if (qualificationLevel.contains("certificate i")) {
            level = 524
        } else if (qualificationLevel.contains("year 12")) {
            level = 611
        } else if (qualificationLevel.contains("year 11")) {
            level = 613
        } else if (qualificationLevel.contains("year 10")) {
            level = 621
        } else if (qualificationLevel.contains("other non-award courses")) {
            level = 912
        } else if (qualificationLevel.contains("statement of attainment")) {
            level = 991
        } else if (qualificationLevel.contains("bridging and enabling courses not identifiable by level")) {
            level = 992
        } else if (qualificationLevel.contains("vocational graduate certificate")) {
            level = 221
        } else if (qualificationLevel.contains("vocational graduate diploma")) {
            level = 211
        } else if (qualificationLevel.contains("course in")) {
            level = 991
        } else if (qualificationLevel.contains("training program in")) {
            level = 912
        }

        return level
    }
}
