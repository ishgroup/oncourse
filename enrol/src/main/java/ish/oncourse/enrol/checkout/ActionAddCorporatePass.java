package ish.oncourse.enrol.checkout;

import ish.oncourse.model.CorporatePass;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

import java.util.Date;
import java.util.List;

public class ActionAddCorporatePass extends  APurchaseAction{

    private String password;

    private CorporatePass corporatePass;
    @Override
    protected void makeAction() {
		getController().getModel().removeAllProductItems(getModel().getPayer());
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
        SelectQuery selectQuery = new SelectQuery(CorporatePass.class);
        Expression expression = ExpressionFactory.matchExp(CorporatePass.PASSWORD_PROPERTY, password);
        expression = expression.andExp(ExpressionFactory.matchExp(CorporatePass.COLLEGE_PROPERTY, getModel().getCollege()));
        expression = expression.andExp(ExpressionFactory.greaterOrEqualExp(CorporatePass.EXPIRY_DATE_PROPERTY, new Date())
                .orExp(ExpressionFactory.matchExp(CorporatePass.EXPIRY_DATE_PROPERTY, null)));
        selectQuery.setQualifier(expression);
        ObjectContext context = getModel().getObjectContext();
        List<CorporatePass> passes = context.performQuery(selectQuery);
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

    private boolean contains(List<CourseClass> validClasses, CourseClass courseClass)
    {
        for (CourseClass aClass : validClasses) {
            if (aClass.getId().equals(courseClass.getId()))
                return true;
        }
        return false;
    }


}
