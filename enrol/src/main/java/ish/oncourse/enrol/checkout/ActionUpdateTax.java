/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.enrol.checkout;

import ish.oncourse.model.*;

import java.util.List;


public class ActionUpdateTax extends APurchaseAction {
    @Override
    protected void makeAction() {

        List<Contact> contacts = getModel().getContacts();
        List<CourseClass> classes = getModel().getClasses();
        List<Product> products = getModel().getProducts();


        /**
         * create disabled enrolments or applications which were deleted after an user pressed ProceedToPayment
         */
        for (Contact contact : contacts) {
            for (CourseClass courseClass : classes) {
                Enrolment enrolment = getModel().getEnrolmentBy(contact, courseClass);
                if (enrolment != null) {

                    ActionDisableEnrolment disableEnrolmentAction = new ActionDisableEnrolment();
                    disableEnrolmentAction.setEnrolment(enrolment);
                    disableEnrolmentAction.setController(getController());
                    disableEnrolmentAction.makeAction();
                    
                    ActionEnableEnrolment enableEnrolmentAction = new ActionEnableEnrolment();
                    enableEnrolmentAction.setEnrolment(disableEnrolmentAction.getEnrolment());
                    enableEnrolmentAction.setController(getController());
                    enableEnrolmentAction.makeAction();                
                }

            }
            for (Product product : products) {
                if(!(product instanceof VoucherProduct)) {
                    ProductItem productItem = getModel().getProductItemBy(contact, product);
                    if (productItem != null) {
                        ActionDisableProductItem actionDisableProductItem = new ActionDisableProductItem();
                        actionDisableProductItem.setProductItem(productItem);
                        actionDisableProductItem.setController(getController());
                        actionDisableProductItem.makeAction();

                        ActionEnableProductItem enableProductItem = new ActionEnableProductItem();
                        enableProductItem.setProductItem(actionDisableProductItem.getProductItem());
                        enableProductItem.setController(getController());
                        enableProductItem.makeAction();
                    }
                }
            }
        }
        
    }


       

    @Override
    protected void parse() {

    }

    @Override
    protected boolean validate() {
        return true;
    }
}
