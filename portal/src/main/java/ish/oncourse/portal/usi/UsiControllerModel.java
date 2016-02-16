package ish.oncourse.portal.usi;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class UsiControllerModel implements Serializable {
    private Contact contact;

    private Step step;

    private boolean skipContactInfo = false;

    private ValidationResult validationResult = new ValidationResult();

    public Contact getContact() {
        return contact;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    public void commitChanges() {
        contact.getObjectContext().commitChanges();
    }

    public List<Enrolment> getVETEnrolments() {
        Expression expression = ExpressionFactory.greaterExp(Enrolment.COURSE_CLASS.getName() + "." + CourseClass.END_DATE.getName(), new Date()).
                andExp(ExpressionFactory.matchExp(Enrolment.COURSE_CLASS.getName() + "." + CourseClass.COURSE.getName() + "." + Course.IS_VETCOURSE.getName(), true));
        if (contact.getStudent() != null) {
            return expression.filterObjects(contact.getStudent().getEnrolments());
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }

    public boolean isSkipContactInfo() {
        return skipContactInfo;
    }

    private void intSkipContactInfo() {
	    skipContactInfo = StringUtils.trimToNull(contact.getStreet()) != null &&
			    StringUtils.trimToNull(contact.getSuburb()) != null &&
			    StringUtils.trimToNull(contact.getPostcode()) != null &&
			    contact.getDateOfBirth() != null &&
                contact.getStudent().getCountryOfBirth() != null &&
			    StringUtils.trimToNull(contact.getStudent().getTownOfBirth()) != null &&
			    contact.getIsMale() != null && StringUtils.trimToNull(contact.getState()) != null &&
			    contact.getCountry() != null &&
			    StringUtils.trimToNull(contact.getMobilePhoneNumber()) != null &&
			    StringUtils.trimToNull(contact.getHomePhoneNumber()) != null &&
			    StringUtils.trimToNull(contact.getBusinessPhoneNumber()) != null &&
			    StringUtils.trimToNull(contact.getStudent().getSpecialNeeds()) != null;
    }


    public static UsiControllerModel valueOf(Contact contact) {
        UsiControllerModel model = new UsiControllerModel();
        model.contact = contact;
        model.intSkipContactInfo();
        return model;
    }
}
