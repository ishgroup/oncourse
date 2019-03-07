package ish.oncourse.specialpages;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

import static ish.oncourse.specialpages.RequestMatchType.*;

/**
 * Set of predefined Tapestry page templates
 */
public enum SpecialWebPage implements DisplayableExtendedEnumeration<Integer> {

    /**
     * Tutors page
     *
     * Database value: 1, path: ui/Tutors
     */
    @API
    TUTORS(1, "ui/Tutors", EXACT),

    COURSES_SKELETON(2, "ui/CoursesSkeleton", STARTS_WITH);

    private int value;
    private String templatePath;
    private RequestMatchType matchType;

    SpecialWebPage(Integer value, String templatePath, RequestMatchType matchType) {
        this.value = value;
        this.templatePath = templatePath;
        this.matchType = matchType;
    }

    @Override
    public String getDisplayName() {
        return this.templatePath;
    }

    @Override
    public Integer getDatabaseValue() { return this.value; }

    public String getTemplatePath() { return this.templatePath; }

    public RequestMatchType getMatchType() { return this.matchType; }
}
