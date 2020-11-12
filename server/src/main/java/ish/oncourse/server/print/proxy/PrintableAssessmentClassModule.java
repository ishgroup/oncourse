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

package ish.oncourse.server.print.proxy;

import ish.common.types.NodeSpecialType;
import ish.oncourse.server.cayenne.AssessmentClass;
import ish.oncourse.server.cayenne.AssessmentClassModule;
import ish.oncourse.server.cayenne.Module;
import ish.oncourse.server.cayenne.Tag;
import ish.print.PrintableObject;

import java.util.List;
import java.util.stream.Collectors;

public class PrintableAssessmentClassModule implements PrintableObject {

    private static final String UNKNOWN_TYPE = "Unknown type";

    private AssessmentClass assessmentClass;
    private Module module;

    public static final String ASSESSMENT_CLASS_PROPERTY = "assessmentClass";
    public static final String MODULE_PROPERTY = "module";
    public static final String VALUE_PROPERTY = "value";

    private PrintableAssessmentClassModule() {

    }

    public static PrintableAssessmentClassModule valueOf(AssessmentClass assessmentClass, Module module) {
        var printableAssessmentClassModule = new PrintableAssessmentClassModule();
        printableAssessmentClassModule.assessmentClass = assessmentClass;
        printableAssessmentClassModule.module = module;
        return printableAssessmentClassModule;
    }


    @Override
    public Object getValueForKey(String key) {
        switch (key) {
            case ASSESSMENT_CLASS_PROPERTY:
                return assessmentClass;
            case MODULE_PROPERTY:
                return module;
            case VALUE_PROPERTY:
                return getValue();
            default:
                return null;
        }

    }

    @Override
    public String getShortRecordDescription() {
        return assessmentClass.getAssessment().getCode() + module.getNationalCode();
    }

    private String getValue() {
        var assessmentClassModule = assessmentClass.getAssessmentClassModules().stream()
                .filter(acm -> module.getNationalCode().equals(acm.getModule().getNationalCode())).findAny().orElse(null);

        if (assessmentClassModule == null) {
            //if no relation return null
            return null;
        } else {
            var tagList = assessmentClass.getAssessment().getTags().stream()
                    .filter(tag -> NodeSpecialType.ASSESSMENT_METHOD.equals(tag.getRoot().getSpecialType()))
                    .collect(Collectors.toList());
            if (tagList.isEmpty()) {
                //if no special type tags return unknown type
                return UNKNOWN_TYPE;
            } else {
                return tagList.stream()
                        .map(Tag::getName)
                        .collect(Collectors.joining(" / "));
            }
        }
    }
}
