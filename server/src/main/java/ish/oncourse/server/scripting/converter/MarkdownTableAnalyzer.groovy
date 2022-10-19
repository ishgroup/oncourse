/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.server.scripting.converter

import org.apache.commons.lang3.StringUtils

/**@
 * Syntactical analyzer
 * Replaces system separators in markdown table templates for correct rendering
 * The analyzer is built on the basis of a finite automaton and an adjacency matrix
 * The adjacency matrix allows to define all possible states(symbols) corresponding to the syntax of table templates
 */
class MarkdownTableAnalyzer {

    private static final String TEST_SEPARATOR = "<__!br!__>"
    private static final String HTML_SEPARATOR = "<br>"

    private String text

    private MarkdownTableAnalyzer() {}

    static MarkdownTableAnalyzer valueOf(String text) {
        MarkdownTableAnalyzer analyzer = new MarkdownTableAnalyzer();
        analyzer.text = text
        analyzer
    }

    String getContentWithFixedSeparators() {

        StringBuilder textBuilder = new StringBuilder()
        StringBuilder tableBuilder = new StringBuilder()

        Token token = Token.NONE
        ExecutionStage state = ExecutionStage.RUNNING

        for (int i = 0; i < text.length(); i++) {

            def symbol = text[i]
            switch (token) {
                case Token.NONE:
                    if (symbol == Token.Border.value) {
                        token = Token.Border
                        state = ExecutionStage.FIND_TOKEN
                    }
                    break
                case Token.Border:
                case Token.SPACE:
                case Token.BOTTOM_BORDER:
                case Token.SYMBOL:
                case Token.SEPARATOR:
                case Token.BOTTOP_BORDER_END:
                    for (int ruleIndex = 0; ruleIndex < adjacencyMatrix.length; ruleIndex++) {
                        if (adjacencyMatrix[token.rowIndex][ruleIndex] == 1 && isMatch(symbol, Token.valueOf(ruleIndex))) {
                            state = ExecutionStage.TOKEN_MATCH
                            token = Token.valueOf(ruleIndex)
                            break
                        }
                    }
                    state = state == state.TOKEN_MATCH ? ExecutionStage.FIND_TOKEN :
                            tableBuilder.size() == 0 ? ExecutionStage.RUNNING : ExecutionStage.RESET
            }

            switch (state) {
                case ExecutionStage.FIND_TOKEN:
                    if (symbol == Token.SEPARATOR.value && token != Token.SEPARATOR) {
                        tableBuilder.append(TEST_SEPARATOR)
                    } else {
                        tableBuilder.append(symbol)
                    }
                    break
                case ExecutionStage.RESET:
                    textBuilder.append(convertToString(tableBuilder))
                    tableBuilder.delete(0, tableBuilder.length())
                    token = Token.NONE
                    state = ExecutionStage.RUNNING
                case ExecutionStage.RUNNING:
                    textBuilder.append(symbol)
            }
        }
        state = ExecutionStage.FINISH
        textBuilder.append(convertToString(tableBuilder))
        textBuilder.toString()
    }

    private static boolean isMatch(String symbol, Token nextToken) {
        switch (nextToken) {
            case Token.Border:
                return symbol == Token.Border.value
                break
            case Token.SPACE:
                return symbol == Token.SPACE.value
                break
            case Token.SYMBOL:
                return true // allow any symbols since the cell content is difficult to determine
                break
            case Token.BOTTOM_BORDER:
                return symbol == Token.BOTTOM_BORDER.value
                break
            case Token.BOTTOP_BORDER_END:
                return symbol == Token.BOTTOP_BORDER_END.value
            case Token.SEPARATOR:
                return symbol == Token.SEPARATOR.value
        }
        return false
    }

    private static String convertToString(StringBuilder tableBuilder) {
        isMarkdownTableTemplate(tableBuilder.toString()) ?
                tableBuilder.toString().replaceAll(TEST_SEPARATOR + TEST_SEPARATOR, HTML_SEPARATOR)
                        .replaceAll(TEST_SEPARATOR, StringUtils.EMPTY) :
                tableBuilder.toString().replaceAll(TEST_SEPARATOR + TEST_SEPARATOR, System.lineSeparator())
                        .replaceAll(TEST_SEPARATOR, StringUtils.EMPTY)

    }

    /**@
     * Allows to determine that the text obtained during the analysis is a markdown table template
     * The check is carried out by checking the table header (the first 2 rows of the table) for compliance
     * with the markdown table template
     */
    private static boolean isMarkdownTableTemplate(String table) {
        def rows = table.split(Token.SEPARATOR.value).toList()
        if (rows.size() < 2) return false
        return rows.get(0) =~ "^\\|.+\\|" && rows.get(1) =~ "\\|\\s-+[\\s:]\\|"

    }

    /**
     * Service enum that allows to control analyzer state
     */
    private enum ExecutionStage {
        NOT_START,
        RESET,
        RUNNING,
        FIND_TOKEN,
        TOKEN_MATCH,
        FINISH
    }

    /**@
     * Сontains list of available tokens defining table synax
     * Lexical token is a string with an assigned and thus identified meaning
     * Rules for transitions between tokens are described through the adjacency matrix in adjacencyMatrix
     * @param value - lexical token meaning
     * @param rowIndex - index of row corresponding to the token in adjacencyMatrix
     */
    private enum Token {
        NONE('', -1),
        Border('|', 0),
        SPACE(StringUtils.SPACE, 1),
        BOTTOM_BORDER('-', 2),
        SYMBOL("", 3),
        SEPARATOR("\n", 4),
        BOTTOP_BORDER_END(":", 5)

        private final String value
        private int rowIndex

        private Token(String value, int rowIndex) {
            this.value = value
            this.rowIndex = rowIndex
        }

        static Token valueOf(int rowIndex) {
            values().find { token -> token.rowIndex == rowIndex }
        }
    }

    /**@
     * An adjacency matrix allows representing a graph of state with a V × V matrix M = [f(i, j)] where
     * each element f(i, j) contains the attributes of the edge (i, j)
     * An adjacency matrix is one of the main data structures that are used to represent graphs
     * Each line and column defines a token. The list of valid tokens is described in enum Token.
     Each intersection of a row and a column determines rule of jump between tokens:
     1 - rule is allowed.
     In analize string is acceptable that after current token (an identified string value) to be followed by
     the token corresponding to the row/column.
     0 - rule is disabled.
     If there is a jump in the parsed sequence, that sequence does not conform to the parser syntax.
     */
    private static final Integer[][] adjacencyMatrix = [
        //   | ' ' t  -  \n :
            [0, 1, 0, 0, 1, 0], // |
            [1, 0, 1, 1, 0, 0], // ' '
            [0, 1, 1, 0, 0, 1], // -
            [0, 1, 0, 1, 0, 0], // text
            [1, 0, 0, 0, 0, 0], // \n ;
            [1, 0, 0, 0, 1, 0] // :
    ] as Integer[][]
}

