package ish.oncourse.enrol.components.checkout;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

import java.util.List;

public class NameList {

    @Parameter(required = true)
    @Property
    private List<String> names;

    @Parameter(required = true)
    @Property
    private String className;

    @Property
    private String name;
}
