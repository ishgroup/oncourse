package ish.oncourse.model;

import ish.oncourse.model.auto._WebSiteLayout;
import ish.oncourse.utils.ResourceNameValidator;
import org.apache.cayenne.validation.ValidationResult;

public class WebSiteLayout extends _WebSiteLayout {

    @Override
    protected void validateForSave(ValidationResult validationResult) {
        super.validateForSave(validationResult);
        String error = new ResourceNameValidator().validate(getLayoutKey());
        if (error != null)
            validationResult.addFailure(ValidationFailure.validationFailure(this, WebSiteLayout.LAYOUT_KEY_PROPERTY, error));

    }
}
