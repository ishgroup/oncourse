package ish.oncourse.enrol.checkout;

import ish.oncourse.model.CorporatePass;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;

import java.util.Date;
import java.util.List;

import static ish.oncourse.enrol.checkout.PurchaseController.Message.corporatePassNotEnabled;

public class ActionAddCorporatePass extends APurchaseAction {

    private String password;

    private CorporatePass corporatePass;

    @Override
    protected void makeAction() {
        if (!getModel().getContacts().contains(corporatePass.getContact())) {
            getModel().addContact(corporatePass.getContact());
        }
        ActionChangePayer actionChangePayer = new ActionChangePayer();
        actionChangePayer.setController(getController());
        actionChangePayer.setContact(corporatePass.getContact());
        actionChangePayer.action();

        getModel().setCorporatePass(corporatePass);
    }

    @Override
    protected void parse() {
        password = getParameter().getValue(String.class);
    }

    @Override
    protected boolean validate() {
        if (!getController().isCorporatePassPaymentEnabled()) {
            getController().addError(corporatePassNotEnabled);
            return false;
        }
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
        if (passes.size() != 1) {
            getController().addError(PurchaseController.Message.corporatePassNotFound);
            return false;
        }
        corporatePass = passes.get(0);

        if (!ClassesValidate.valueOf(corporatePass, getController()).validate()) {
            return false;
        }

        if (!ProductsValidate.valueOf(corporatePass, getController()).validate()) {
            return false;
        }


        return true;
    }
}