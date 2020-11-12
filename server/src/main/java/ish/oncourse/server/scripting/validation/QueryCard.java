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

import java.util.Arrays;
import java.util.List;

public class QueryCard extends SequenceToken {

    public static final String QUERY_CARD_START_REGEX = "([\\s\\S]{0,})def ([a-zA-Z_]+)([a-zA-Z0-9_]{0,}) = query \\{";
    private static final String ENTITY_REGEX = "entity\\s+([a-zA-Z]+)";
    private static final String QUERY_REGEX = "query\\s+\\\"([\\w\\W\\d\\D\\s\\S])+\\\"";
    private static final String CONTEXT_REGEX = "context args\\.context";
    private static final String END_OF_QUERY_CARD_REGEX = "\\}";

    private static final List<String> querySequence = Arrays.asList(
            QUERY_CARD_START_REGEX,
            ENTITY_REGEX,
            QUERY_REGEX,
            CONTEXT_REGEX,
            END_OF_QUERY_CARD_REGEX
    );

    public QueryCard() {
        super(querySequence);
    }
}
