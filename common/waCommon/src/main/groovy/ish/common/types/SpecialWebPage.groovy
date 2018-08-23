package ish.common.types

import ish.common.util.DisplayableExtendedEnumeration
import ish.oncourse.API

enum SpecialWebPage implements DisplayableExtendedEnumeration<Integer> {

    @API
    TUTORS(1, "ui/Tutors")

    private int value
    private String templatePath

    SpecialWebPage(Integer value, String templatePath) {
        this.value = value
        this.templatePath = templatePath
    }

    @Override
    String getDisplayName() {
        return this.templatePath
    }

    @Override
    Integer getDatabaseValue() { return this.value }

    String getTemplatePath() { return this.templatePath }
}
