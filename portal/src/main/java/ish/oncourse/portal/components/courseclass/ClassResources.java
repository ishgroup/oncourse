package ish.oncourse.portal.components.courseclass;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.CourseClass;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.cookies.ICookiesService;
import org.apache.commons.io.FileUtils;
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

    @Property
    private List<BinaryInfo> materials;

    @Property
    private BinaryInfo material;

    @Inject
    private ICookiesService cookieService;

    @Inject
    private Request request;

	@Inject
	private IAuthenticationService authenticationService;

	@Inject
	private IPortalService portalService;
	
	@Inject
	private IBinaryDataService binaryDataService;

    @SetupRender
    boolean setupRender() {
        if (courseClass == null) {
            return false;
        }

        materials = portalService.getResourcesBy(courseClass);
        return true;
    }

    public String getContextPath() {
        return request.getContextPath();
    }

    public boolean isNew() {
        /**
         * we added material.getModified() != null condition to exlude NPE when some old material has null value in create Modified.
         * TODO The condition should be deleted after 21309 will be closed
         */
        return material.getModified() != null && material.getModified().after(portalService.getLastLoginTime());
	}

	public String getMaterialUrl() {
		return binaryDataService.getUrl(material);
	}

	public String getSize() {
		return FileUtils.byteCountToDisplaySize(material.getByteSize());
	}
}
