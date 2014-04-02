package ish.oncourse.portal.components.courseclass;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.CourseClass;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.cookies.ICookiesService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static ish.oncourse.portal.services.PortalUtils.COOKIE_NAME_lastLoginTime;
import static ish.oncourse.portal.services.PortalUtils.DATE_FORMAT_EEE_MMM_dd_hh_mm_ss_z_yyyy;

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

    private Date lastLoginDate;

    @SetupRender
    boolean setupRender() {
        if (courseClass == null) {
            return false;
        }

        String sd = cookieService.getCookieValue(COOKIE_NAME_lastLoginTime);
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_EEE_MMM_dd_hh_mm_ss_z_yyyy);
		
		if ( StringUtils.trimToNull(sd) != null) {
			try {
				lastLoginDate = format.parse(sd);
			} catch (Exception ex) {
				throw new IllegalArgumentException(ex);
			}
		}

        materials = portalService.getAttachedFiles(courseClass);
        return true;
    }

    public String getContextPath() {
        return request.getContextPath();
    }

    public boolean isNew(Date material) {
		if (lastLoginDate == null) {
			return true;
		} else {
			return material.after(lastLoginDate);
		}
	}

	public String getMaterialUrl() {
		return binaryDataService.getUrl(material);
	}

	public String getSize() {
		return FileUtils.byteCountToDisplaySize(material.getByteSize());
	}
}
