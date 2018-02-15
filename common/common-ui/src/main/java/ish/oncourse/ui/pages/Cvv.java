package ish.oncourse.ui.pages;

import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.ui.base.ISHCommon;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Cvv extends ISHCommon {

    @Inject
    private PreferenceController preferenceController;

    @Property
    private boolean isAmexEnabled;

    void beginRender() {
        isAmexEnabled = preferenceController.getServicesAmexEnabled();
    }
}
