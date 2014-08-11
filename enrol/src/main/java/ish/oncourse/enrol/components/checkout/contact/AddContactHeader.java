package ish.oncourse.enrol.components.checkout.contact;

import ish.oncourse.enrol.checkout.contact.AddContactDelegate;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class AddContactHeader {
    @Parameter(required = true)
    @Property
    private AddContactDelegate delegate;
}
