package ish.oncourse.portal.components.courseclass;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.CourseClass;
import ish.oncourse.services.binary.IBinaryDataService;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.List;

/**
 * User: artem
 * Date: 10/16/13
 * Time: 10:17 AM
 */
public class ClassResources {


    @Parameter
    @Property
    private CourseClass courseClass;

    @Inject
    private IBinaryDataService binaryDataService;

    @Property
    private List<BinaryInfo> materials;

    @Property
    private BinaryInfo material;

    @Inject
    private Request request;

    @SetupRender
    boolean setupRender() {
        if (courseClass == null) {
            return false;
        }
        materials = binaryDataService.getAttachedFiles(courseClass.getId(), CourseClass.class.getSimpleName(), false);
        return true;
    }

    public String getContextPath() {
        return request.getContextPath();
    }
}
