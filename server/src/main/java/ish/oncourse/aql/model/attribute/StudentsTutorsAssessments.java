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

package ish.oncourse.aql.model.attribute;

import ish.oncourse.aql.model.Entity;
import ish.oncourse.aql.model.EntityFactory;
import ish.oncourse.aql.model.SyntheticAttributeDescriptor;
import ish.oncourse.server.cayenne.*;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.util.Optional;

public class StudentsTutorsAssessments implements SyntheticAttributeDescriptor {

    private EntityFactory factory;

    public StudentsTutorsAssessments(EntityFactory factory) {
        this.factory = factory;
    }

    @Override
    public Class<? extends Persistent> getEntityType() {
        return Contact.class;
    }

    @Override
    public String getAttributeName() {
        return "allRelatedContactsToAssessment";
    }

    @Override
    public Optional<Entity> nextEntity() {
        return Optional.of(factory.createEntity(Assessment.class));
    }

    @Override
    public SimpleNode spawnNode() {
        return new SyntheticCompoundNodeTemplate(getAttributeName(),
                Contact.STUDENT.outer()
                        .dot(Student.ENROLMENTS.outer())
                        .dot(Enrolment.COURSE_CLASS.outer())
                        .dot(CourseClass.ASSESSMENT_CLASSES.outer())
                        .dot(AssessmentClass.ASSESSMENT.outer()).getExpression(),
                Contact.TUTOR.outer()
                        .dot(Tutor.COURSE_CLASS_ROLES.outer())
                        .dot(CourseClassTutor.COURSE_CLASS.outer())
                        .dot(CourseClass.ASSESSMENT_CLASSES.outer())
                        .dot(AssessmentClass.ASSESSMENT.outer()).getExpression()
        );
    }
}
