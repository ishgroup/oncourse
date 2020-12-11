package ish.oncourse.services.textile.validator;

import ish.oncourse.services.textile.TextileType;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.TutorsTextileAttributes;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationFailureType;

import java.util.Map;

public class TutorsTextileValidator extends AbstractTextileValidator {
    
    
    @Override
    protected void initValidator() {
        textileType = TextileType.TUTORS;
    }

    @Override
    protected void specificTextileValidate(String tag, ValidationErrors errors) {
        Map<String, String> tagParams = TextileUtil.getTagParams(tag, textileType.getAttributes());
        String tagName = tagParams.get(TutorsTextileAttributes.TAG_NAME.getValue());
        String id = tagParams.get(TutorsTextileAttributes.TAG_NAME.getValue());

        if (tagName == null && id == null) {
            errors.addFailure("Tag name or single tutor id should be provided",
                    ValidationFailureType.SYNTAX);
        }
        
    }

    @Override
    public String getFormatErrorMessage(String tag) {
        return "The {tutors} tag '" + tag + "' doesn't match {tutors tagName:\"name\" }";
    }
}
