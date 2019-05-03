package ish.oncourse.model;

import ish.oncourse.model.auto._WebSiteLayout;
import ish.oncourse.utils.QueueableObjectUtils;
import ish.oncourse.utils.ResourceNameValidator;
import org.apache.cayenne.validation.ValidationResult;

public class WebSiteLayout extends _WebSiteLayout {

    public Long getId() {
        return QueueableObjectUtils.getId(this);
    }


    protected void validateForSave(ValidationResult validationResult) {
        super.validateForSave(validationResult);
        String error = ResourceNameValidator.valueOf().validate(getLayoutKey());
        if (error != null) {
            validationResult.addFailure(ValidationFailure.validationFailure(this, WebSiteLayout.LAYOUT_KEY.getName(), error));
        }
    }
}
