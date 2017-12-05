package ish.oncourse.ui.components;

import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.model.Course;
import ish.oncourse.model.Module;
import ish.oncourse.model.Qualification;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

import java.util.List;

public class CourseModules extends ISHCommon {

    @Property
    private Module module;

    @Property
    @Parameter(required = true)
    private List<Module> modules;

    @Property
    @Parameter(required = true)
    private Qualification qualification;

    @Property
    @Parameter(required = true)
    private Course course;
}
