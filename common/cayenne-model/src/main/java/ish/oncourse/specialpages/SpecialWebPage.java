package ish.oncourse.specialpages;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

public enum SpecialWebPage implements DisplayableExtendedEnumeration<Integer> {

    @API
    TUTORS(1, "ui/tutors/Tutors");

    private int value;
    private String templatePath;

    SpecialWebPage(Integer value, String templatePath) {
        this.value = value;
        this.templatePath = templatePath;
    }

    @Override
    public String getDisplayName() {
        return this.templatePath;
    }

    @Override
    public Integer getDatabaseValue() { return this.value; }

    public String getTemplatePath() { return this.templatePath; }
}
