package ish.oncourse.enrol.checkout;

import ish.oncourse.model.CorporatePass;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;

import java.util.Date;
import java.util.List;

import static ish.oncourse.enrol.checkout.PurchaseController.Message.corporatePassNotEnabled;

public class ActionAddCorporatePass extends  APurchaseAction{

    private String password;

    private CorporatePass corporatePass;
    @Override
    protected void makeAction() {
		getModel().setPayer(corporatePass.getContact());
        getModel().setCorporatePass(corporatePass);
    }

    @Override
    protected void parse() {
        password = getParameter().getValue(String.class);
    }

    @Override
    protected boolean validate() {
        /**
         * select all CorporatePass with this password and expiry date is greater or equal current date
         * or is null in this college
         */
        ObjectContext context = getModel().getObjectContext();
        List<CorporatePass> passes = ObjectSelect.query(CorporatePass.class).
		        where(CorporatePass.PASSWORD.eq(password)).
		        and(CorporatePass.COLLEGE.eq(getModel().getCollege())).
		        and(CorporatePass.EXPIRY_DATE.gte(new Date()).orExp(CorporatePass.EXPIRY_DATE.isNull())).
		        select(context);
	    
        /**
         * Found pass should be only one
         */
        if (passes.size() != 1)
        {
            getController().addError(PurchaseController.Message.corporatePassNotFound);
            return false;
        }
        corporatePass = passes.get(0);

        List<CourseClass> validClasses  = corporatePass.getValidClasses();
        List<Enrolment> enrolments =  getModel().getAllEnabledEnrolments();
        for (Enrolment enrolment : enrolments) {
            if (!contains(validClasses, enrolment.getCourseClass()))
            {
                getController().addError(PurchaseController.Message.corporatePassInvalidCourseClass,
                        getController().getClassName(enrolment.getCourseClass()));
                return false;
            }
        }
        return true;
    }

    private boolean contains(List<CourseClass> validClasses, CourseClass courseClass) {
		if (!getController().isCorporatePassPaymentEnabled())
		{
			getController().addError(corporatePassNotEnabled);
			return false;
		}

    	if (validClasses.isEmpty()) {
    		//if no classes specified, all the classes should pass as valid
    		return true;
    	}
        for (CourseClass aClass : validClasses) {
            if (aClass.getId().equals(courseClass.getId()))
                return true;
        }
        return false;
    }


}
