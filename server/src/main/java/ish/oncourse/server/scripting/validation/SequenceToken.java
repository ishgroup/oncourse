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

package ish.oncourse.server.scripting.validation;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Token which appends his content based on which sequence of token he can have.
 */
public abstract class SequenceToken extends BaseToken {

    private List<String> querySequence;
    private Iterator<String> iterator;

    public SequenceToken(List<String> querySequence) {
        this.querySequence = querySequence;
        this.iterator = querySequence.iterator();
    }

    @Override
    public boolean appendContent(String appendedContent) {
        if (iterator.hasNext()) {
            var lineRegex = iterator.next();
            var pattern = Pattern.compile(lineRegex);
            var matcher = pattern.matcher(appendedContent);
            var result = matcher.matches();
            if (result) {
                content = content.concat(appendedContent);
                content = content.concat("\n");
            }
            return result;
        } else {
            return false;
        }
    }

    @Override
    public boolean isComplete() {
        return !iterator.hasNext();
    }
}
