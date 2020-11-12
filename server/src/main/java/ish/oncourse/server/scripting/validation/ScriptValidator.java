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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Special validator which checks that script has valid lines.
 */
public class ScriptValidator {

    private static final String EMPTY_STRING = "";

    private Map<String, Class<? extends BaseToken>> regexes;
    private String scriptContent;
    private List<BaseToken> cards;
    private boolean operationWasEndedCorrectly;

    public ScriptValidator(String scriptContent) {
        this.scriptContent = scriptContent != null ? scriptContent : "";
        this.cards = new ArrayList<>();
        initCardsMap();
        this.operationWasEndedCorrectly = tokenizeScript();
    }

    private void initCardsMap() {
        regexes = new HashMap<>();
//        regexes.put(EmailCard.EMAIL_CARD_START, EmailCard.class);
        regexes.put(ImportCard.IMPORT_PATTERN, ImportCard.class);
        regexes.put(QueryCard.QUERY_CARD_START_REGEX, QueryCard.class);
    }

    private boolean tokenizeScript() {
        var lines = scriptContent.split("\n");
        var currentToken = getToken(ScriptCard.class);

        // Exclude last line
        for (var i = 0; i < lines.length - 1; i++) {
            var line = lines[i];

            // find type of the line
            var matchedCards = regexes.keySet().stream()
                    .filter(key -> lineMatchesRegex(line.trim(), key))
                    .collect(Collectors.toList());

            if (matchedCards.size() == 1) {
                // we found start of Import, Query or Email card
                if ((currentToken.getClass().equals(ScriptCard.class)) && !(currentToken.getContent().equals(EMPTY_STRING))) {
                    cards.add(currentToken);
                }
                currentToken = getToken(regexes.get(matchedCards.get(0)));
            } else {
                // ignore empty lines, script start and comments
                if ((line.trim().equals(EMPTY_STRING))
                        || (lineMatchesRegex(line, BaseToken.ONE_LINE_COMMENT_REGEX))) {
                    continue;
                }
            }
            if (!currentToken.appendContent(line.trim())) {
                continue;
            }

            if (currentToken.isComplete()) {
                cards.add(currentToken);
                currentToken = getToken(ScriptCard.class);
            }

        }
        if ((currentToken.getClass().equals(ScriptCard.class)) && !(currentToken.getContent().equals(EMPTY_STRING))) {
            cards.add(currentToken);
        }

        return (cards.size() != 0) || (scriptHasCorrectDefinition());
    }

    /**
     * Checks line with some regular expression
     * @param line which will be checked
     * @param regex for line
     * @return true if line matches regex, false - if doesn't
     */
    private boolean lineMatchesRegex(String line, String regex) {
        var pattern = Pattern.compile(regex);
        var matcher = pattern.matcher(line);
        return matcher.matches();
    }

    /**
     * Returns token by type
     * @param tokenClass which will be initialized
     * @return concrete token instance
     */
    private BaseToken getToken(Class tokenClass) {
        if (tokenClass.equals(EmailCard.class)) {
            return new EmailCard();
        } else if (tokenClass.equals(ImportCard.class)) {
            return new ImportCard();
        } else if (tokenClass.equals(QueryCard.class)) {
            return new QueryCard();
        } else if (tokenClass.equals(ScriptCard.class)) {
            return new ScriptCard();
        } else {
            throw new RuntimeException("Invalid token class!");
        }
    }

    /**
     * Checks that script has concrete type of cards
     * @param cardClass which will find between cards
     * @return search result
     */
    private boolean hasConcreteCard(Class cardClass) {
        return cards.stream()
                .anyMatch(card -> card.getClass().equals(cardClass));
    }

    // Checks, that script has correct structure:
    // def run(args) {...}
    public boolean scriptHasCorrectDefinition() {
        return lineMatchesRegex(scriptContent.trim(), BaseToken.SCRIPT_STRUCTURE_REGEX);
    }

}
