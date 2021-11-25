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
        
    }

    @Override
    public String getFormatErrorMessage(String tag) {
        return "The {tutors} tag '" + tag + "' doesn't match {tutors tagName:\"name\" }";
    }
}
