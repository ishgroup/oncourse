/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.liquibase;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * compare file names taht might has number in front, or might not
 *
 * move numbered fils in front of the list
 * all unnamed files at the end of the list (ordered as regular string)
 **/
public class NumberedFilesComparator implements Comparator<String> {
    /**
     * check if liquibase file name has vrsion in front
     * assert 'database/105.upgrade.yml' ~ NUMBERED_PATTERN 
     * assert ! ('database/replication.yml' ~ NUMBERED_PATTERN)
     */
    private static final Pattern NUMBERED_PATTERN = Pattern.compile(".*/(\\d+)\\..*");
    
    @Override
    public int compare(String a, String b) {
        Matcher aMatcher = NUMBERED_PATTERN.matcher(a);
        Matcher bMatcher = NUMBERED_PATTERN.matcher(b);

        if (aMatcher.matches()) {
            if (bMatcher.matches()) {
                //compare two numbered files 93.upgrade.yml <=> 105.upgrade.yml
                return Integer.valueOf(aMatcher.group(1)).compareTo(Integer.valueOf(bMatcher.group(1)));
            } else {
                //numbered (93.upgrade.yml) file always has more weight than non numbered (quartz.yml)
                return -1;
            }
        } else if (bMatcher.matches()) {
            //non numbered (quartz.yml) file always has less weight than numbered (93.upgrade.yml)
            return 1;
        } else {
            //compare two non numbered files quartz.yml <=> replication.yml
            return a.compareTo(b);
        }
    }
}
