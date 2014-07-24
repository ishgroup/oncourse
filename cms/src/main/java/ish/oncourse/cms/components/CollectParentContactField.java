package ish.oncourse.cms.components;

import ish.oncourse.services.preference.PreferenceController;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class CollectParentContactField {

    @Inject
    @Property
    private PreferenceController preferenceController;

    @Property
    private Integer studentAgeWhenNeedParent;

    @Property
    private Boolean collectParentDetails;

    @SetupRender
    void setupRender() {
        studentAgeWhenNeedParent = preferenceController.getStudentAgeWhenNeedParent();
        collectParentDetails = preferenceController.isCollectParentDetails();
    }

    public void save() {
        preferenceController.setStudentAgeWhenNeedParent(studentAgeWhenNeedParent != null ? studentAgeWhenNeedParent: PreferenceController.DEFAULT_studentAgeWhenNeedParent);
        preferenceController.setCollectParentDetails(collectParentDetails != null? collectParentDetails: false);
    }
}
