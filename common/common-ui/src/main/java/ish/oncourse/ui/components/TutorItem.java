package ish.oncourse.ui.components;

import ish.oncourse.model.Course;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.IRichtextConverter;
import ish.oncourse.util.ValidationErrors;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;
import java.util.stream.Collectors;

public class TutorItem extends ISHCommon {

    @Inject
    private IWebSiteService webSiteService;

    @Inject
    private IBinaryDataService binaryDataService;

    @Inject
    private IRichtextConverter textileConverter;

    @Parameter
    @Property
    private Tutor tutor;

    @Property
    private TutorRole role;

    @Property
    private String profilePictureUrl;

    @Property
    private List<Course> courses;

    @Property
    private Course course;

    @SetupRender
    void beginRender() {
        profilePictureUrl = binaryDataService.getProfilePictureUrl(tutor.getContact());
        courses = tutor.getCurrentVisibleTutorRoles().stream()
                .map( role -> role.getCourseClass().getCourse())
                .distinct()
                .collect(Collectors.toList());
    }

    public boolean isHasClasses() {
        return !tutor.getCurrentVisibleTutorRoles().isEmpty();
    }

    public List<TutorRole> getCurrentVisibleTutorRoles() {
        return tutor.getCurrentVisibleTutorRoles();
    }


    public String getResume() {
        String resume = StringUtils.trimToNull(tutor.getResume());
        if (resume != null) {
            return textileConverter.convertCustomText(resume, new ValidationErrors());
        } else {
            return String.format("%s. Tutor of %s", tutor.getFullName(), webSiteService.getCurrentCollege().getName());
        }
    }


}
