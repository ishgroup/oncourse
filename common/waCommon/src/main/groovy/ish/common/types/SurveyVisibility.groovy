package ish.common.types

import ish.common.util.DisplayableExtendedEnumeration

/**
 * Visibility of surveys on web
 */
enum SurveyVisibility implements DisplayableExtendedEnumeration<Integer> {

    /**
     * Database value: 0
     *
     * Testimonial awaiting review
     */
    REVIEW(0, 'Waiting review'),

    /**
     * Database value: 1
     *
     * Testimonial is shown on web
     */
    TESTIMONIAL(1, 'Public testimonial'),

    /**
     * Database value: 2
     *
     * Testimonial is hidden on web
     */
    NOT_TESTIMONIAL(2, 'Not testimonial'),

    /**
     * Database value: 3
     *
     * Testimonial is hidden by student 
     */
    STUDENT_HIDDEN(3, 'Hidden by student')

    private int value
    private String displayName
    
    private SurveyVisibility(int value, String displayName) {
        this.value = value
        this.displayName = displayName
    }

    @Override
    String getDisplayName() {
        return displayName
    }

    @Override
    Integer getDatabaseValue() {
        return value
    }
}
