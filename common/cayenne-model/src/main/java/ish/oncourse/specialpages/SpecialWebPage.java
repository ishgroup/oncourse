package ish.oncourse.specialpages;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

/**
 * Set of predefined Tapestry page templates
 */
public enum SpecialWebPage implements DisplayableExtendedEnumeration<Integer> {

    /**
     * Tutors page
     *
     * Database value: 1, path: ui/tutors/Tutors
     */
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
