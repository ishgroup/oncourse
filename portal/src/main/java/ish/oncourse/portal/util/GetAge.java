package ish.oncourse.portal.util;

import ish.oncourse.model.Contact;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

public class GetAge {

    private Contact c;

    private GetAge() {}

    public static GetAge valueOf(Contact contact) {
        GetAge obj = new GetAge();
        obj.c = contact;
        return obj;
    }

    public Integer get() {
        if (c.getDateOfBirth() != null)
            return Period.between(c.getDateOfBirth().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now()).getYears();
        return null;
    }
}
