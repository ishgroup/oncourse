package ish.oncourse.services.textile.validator;

import ish.oncourse.services.textile.TextileType;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.DurationTextileAttribute;
import ish.oncourse.solr.query.Duration;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationFailureType;

import java.util.Map;

public class DurationTextileValidator extends AbstractTextileValidator {
    
    public DurationTextileValidator() {
    }

    @Override
    protected void initValidator() {
        textileType = TextileType.DURATION;
    }

    @Override
    protected void specificTextileValidate(String tag, ValidationErrors errors) {
        Map<String, String> tagParams = TextileUtil.getTagParams(tag, textileType.getAttributes());
        
        String display = tagParams.get(DurationTextileAttribute.DISPLAY.getValue());
        String duration = tagParams.get(DurationTextileAttribute.DISPLAY.getValue());
        
        if (display == null || duration == null) {
            errors.addFailure("The duration: " + tag + " must contain the required attribute: display and duration or site", ValidationFailureType.SYNTAX);
        }

        String error = Duration.validate(duration);
        
        if (error != null) {
            errors.addFailure("The duration duration attribute '" + tag + " has error: " + error, ValidationFailureType.SYNTAX);
        }
    }

    @Override
    public String getFormatErrorMessage(String tag) {
        return "The duration tag '" + tag + "' doesn't match {duration display:\"One day course\" duration:\">1\"}";
    }
}
