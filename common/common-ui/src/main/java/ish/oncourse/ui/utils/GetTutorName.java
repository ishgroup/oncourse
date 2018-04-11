package ish.oncourse.ui.utils;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Tutor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public class GetTutorName {

    private Tutor tutor;

    private GetTutorName() {
    }

    public static GetTutorName valueOf(Tutor tutor) {
        GetTutorName obj = new GetTutorName();
        obj.tutor = tutor;
        return obj;
    }

    public String get() {
        Contact contact = tutor.getContact();
        if (contact.getIsCompany()) {
            return contact.getFamilyName() != null ? contact.getFamilyName() : StringUtils.EMPTY;
        } else {
            return StringUtils.trim(
                    String.format("%s %s",
                            contact.getGivenName() != null ? contact.getGivenName() : StringUtils.EMPTY,
                            contact.getFamilyName() != null ? contact.getFamilyName() : StringUtils.EMPTY));
        }
    }
}
