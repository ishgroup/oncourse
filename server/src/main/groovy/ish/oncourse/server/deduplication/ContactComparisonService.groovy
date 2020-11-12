/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.deduplication

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.Contact
/**
 * Compare two contact records to determine if they are duplicates
 */
@CompileStatic
class ContactComparisonService {

    Contact c1, c2

    ContactComparisonService(Contact c1, Contact c2) {
        setContacts(c1, c2)
    }

    /**
     * Replace the contact records in this comparison with a new pair
     * @param c1
     * @param c2
     */
    void setContacts(Contact c1, Contact c2) {
        this.c1 = c1
        this.c2 = c2
    }

    /**
     * Get the score as a fraction of the total possible score. 1.0 would be a perfect match.
     */
    float getScore() {
        def total_score = 0.0f
        def maximum_score = 0.0f

        total_score += getEmailScore(c2.email, c1.email) * 10
        maximum_score += 10

        total_score += jaccardScore(c2.firstName, c1.firstName) * 3
        maximum_score += 3

        total_score += jaccardScore(c2.lastName, c1.lastName) * 5
        maximum_score += 5

        total_score += getScore(c2.dateOfBirth, c1.dateOfBirth) * 15
        maximum_score += 15

        total_score += jaccardScore(c2.suburb, c1.suburb) * 3
        maximum_score += 3

        total_score += jaccardScore(c2.street, c1.street) * 8
        maximum_score += 8

        total_score += getPhoneScore(c2.homePhone, c1.homePhone) * 7
        maximum_score += 7

        total_score += getPhoneScore(c2.workPhone, c1.workPhone) * 4
        maximum_score += 4

        total_score += getPhoneScore(c2.mobilePhone, c1.mobilePhone) * 12
        maximum_score += 12

        total_score += getScore(c2.gender, c1.gender) * 1
        maximum_score += 1

        return total_score / maximum_score
    }

    /**
     * Get a score out of 1.0 for an email. The username part is more important than the domain
     * @param s1
     * @param s2
     * @return
     */
    static float getEmailScore(String s1, String s2) {
        if (!s1 || !s2) {
            return 0.0f
        }
        def score_user = jaccardScore(s1.tokenize('@').first(), s2.tokenize('@').first())
        def score_domain = jaccardScore(s1.tokenize('@').last(), s2.tokenize('@').last())

        return score_user*0.8 + score_domain*0.2
    }

    static float getPhoneScore(String o1, String o2) {
        if (!o1 || !o2) {
            return 0.0f
        }
        if (o1 == o2) {
            return 1.0f
        }
        return 0.0f
    }

    static float getScore(Object o1, Object o2) {
        if (!o1 || !o2) {
            return 0.0f
        }
        if (o1 == o2) {
            return 1.0f
        }
        return 0.0f
    }

    /**
     * A measure of similarity between two strings. 1 is exactly the same.
     *
     * @param s1
     * @param s2
     * @return
     */
    static float jaccardScore(String s1, String s2) {
        if (!s1 || !s2) {
            return 0.0f
        }
        if (s1.equalsIgnoreCase(s2)) {
            return 1.0f
        }
        def leftNgrams = DuplicateFinder.ngram(s1.toLowerCase(), 2, true)
        def rightNgrams = DuplicateFinder.ngram(s2.toLowerCase(), 2, true)
        int missing = 0, total_size

        if (leftNgrams.size() > rightNgrams.size()) {
            total_size = leftNgrams.size()
            for (item in leftNgrams) {
                if (!rightNgrams.contains(item)) {
                    missing++
                }
            }
        } else {
            total_size = rightNgrams.size()
            for (item in rightNgrams) {
                if (!leftNgrams.contains(item)) {
                    missing++
                }
            }
        }
        return (total_size - missing) / total_size
    }

}
