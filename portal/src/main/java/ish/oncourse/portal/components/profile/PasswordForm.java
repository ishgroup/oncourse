package ish.oncourse.portal.components.profile;

import ish.oncourse.model.Contact;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.pages.Timetable;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.util.ValidateHandler;
import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.PasswordField;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * User: artem
 * Date: 10/29/13
 * Time: 5:49 PM
 */
public class PasswordForm {

    @Persist
    @Property
    private boolean success;

    @Inject
    private Messages messages;

    @InjectComponent
    @Property
    private Form passwordForm;

    @Property
    private String password;

    @Property
    private String confirmPassword;

    @InjectComponent
    private PasswordField passwordFild;

    @InjectComponent
    private PasswordField confirmPasswordFild;

    @Property
    private String passwordErrorMessage;

    @Property
    private String confirmPasswordErrorMessage;




    @Parameter
    @Property
    private Contact contact;

    @Persist
    @Property
    private ValidateHandler validateHandler;


     @SetupRender
     void seetupRender(){
        if(validateHandler == null)
            validateHandler = new ValidateHandler();

     }

    @AfterRender
    void afterRende(){
        success=false;
    }






    @OnEvent(component = "passwordForm")
    Object passwordSubmitted() {

     if(validate()) {
        if (password != null) {
            contact.setPassword(password);

        }
         success=true;
        contact.getObjectContext().commitChanges();
     }
        return this;
    }




    boolean validate(){
        validateHandler.getErrors().clear();

        if (password != null && password.length() > 0 && confirmPassword != null && confirmPassword.length() > 0) {

            passwordErrorMessage = validatedPassword(password, false);
            if(passwordErrorMessage!=null){
            validateHandler.getErrors().put("password",passwordErrorMessage);
            passwordForm.recordError(passwordErrorMessage);
            }

            confirmPasswordErrorMessage = validatedPassword(confirmPassword, true);
            if(confirmPasswordErrorMessage!=null){
            passwordForm.recordError(confirmPasswordErrorMessage);
            validateHandler.getErrors().put("confirmpassword",confirmPasswordErrorMessage);
            }

        }


        if(confirmPassword==null){
            confirmPasswordErrorMessage = messages.get("message-confirmPasswordEmpty");
            passwordForm.recordError(confirmPasswordErrorMessage);
            validateHandler.getErrors().put("confirmpassword",confirmPasswordErrorMessage);
        }


        if(password==null){
            passwordErrorMessage = messages.get("message-passwordEmpty");
            passwordForm.recordError(passwordErrorMessage);
            validateHandler.getErrors().put("password", passwordErrorMessage);
        }


        return !passwordForm.getHasErrors();

    }



    private String validatedPassword(String aValue, boolean isConfirm) {

        int minimumPasswordChars = 4;
        if (aValue == null || aValue.length() < minimumPasswordChars) {
            return messages.format("message-passwordMinChars", minimumPasswordChars);
        }
        if (aValue.split("\\s").length != 1) {
            return messages.format("message-passwordHasSpaces");
        }
        if (isConfirm && !aValue.equals(password)) {
            return messages.get("message-confirmPasswordNotMatch");
        }
        return null;
    }

}
