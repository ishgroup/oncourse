"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var MarkdownUtil_1 = require("../util/MarkdownUtil");
exports.quoteCommand = {
    name: "quote",
    buttonProps: { "aria-label": "Insert a quote" },
    execute: function (state0, api) {
        // Adjust the selection to encompass the whole word if the caret is inside one
        var newSelectionRange = MarkdownUtil_1.selectWord({
            text: state0.text,
            selection: state0.selection
        });
        var state1 = api.setSelectionRange(newSelectionRange);
        var breaksBeforeCount = MarkdownUtil_1.getBreaksNeededForEmptyLineBefore(state1.text, state1.selection.start);
        var breaksBefore = Array(breaksBeforeCount + 1).join("\n");
        var breaksAfterCount = MarkdownUtil_1.getBreaksNeededForEmptyLineAfter(state1.text, state1.selection.end);
        var breaksAfter = Array(breaksAfterCount + 1).join("\n");
        // Replaces the current selection with the quote mark up
        api.replaceSelection(breaksBefore + "> " + state1.selectedText + breaksAfter);
        var selectionStart = state1.selection.start + breaksBeforeCount + 2;
        var selectionEnd = selectionStart + state1.selectedText.length;
        api.setSelectionRange({
            start: selectionStart,
            end: selectionEnd
        });
    }
};
