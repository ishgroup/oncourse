package ish.oncourse.enrol.checkout;

import ish.oncourse.model.CorporatePass;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;

import java.util.List;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class ClassesValidate {
    private PurchaseController controller;
    private CorporatePass corporatePass;

    public boolean validate() {
        List<CourseClass> validClasses = corporatePass.getValidClasses();

        if (validClasses.isEmpty()) {
            //if no classes specified, all the classes should pass as valid
            return true;
        }

        List<Enrolment> enrolments = controller.getModel().getAllEnabledEnrolments();
        for (Enrolment enrolment : enrolments) {
            if (!contains(validClasses, enrolment.getCourseClass())) {
                controller.addError(PurchaseController.Message.corporatePassInvalidCourseClass,
                        controller.getClassName(enrolment.getCourseClass()));
                return false;
            }
        }
        return true;
    }

    private boolean contains(List<CourseClass> validClasses, CourseClass courseClass) {
        for (CourseClass aClass : validClasses) {
            if (aClass.getId().equals(courseClass.getId()))
                return true;
        }
        return false;
    }

    public static ClassesValidate valueOf(CorporatePass corporatePass, PurchaseController controller) {
        ClassesValidate classesValidate = new ClassesValidate();
        classesValidate.corporatePass = corporatePass;
        classesValidate.controller = controller;
        return classesValidate;
    }

}