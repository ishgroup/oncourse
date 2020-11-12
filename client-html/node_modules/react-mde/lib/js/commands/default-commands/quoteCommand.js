"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.quoteCommand = void 0;
var MarkdownUtil_1 = require("../../util/MarkdownUtil");
exports.quoteCommand = {
    buttonProps: { "aria-label": "Insert a quote" },
    execute: function (_a) {
        var initialState = _a.initialState, textApi = _a.textApi;
        // Adjust the selection to encompass the whole word if the caret is inside one
        var newSelectionRange = MarkdownUtil_1.selectWord({
            text: initialState.text,
            selection: initialState.selection
        });
        var state1 = textApi.setSelectionRange(newSelectionRange);
        var breaksBeforeCount = MarkdownUtil_1.getBreaksNeededForEmptyLineBefore(state1.text, state1.selection.start);
        var breaksBefore = Array(breaksBeforeCount + 1).join("\n");
        var breaksAfterCount = MarkdownUtil_1.getBreaksNeededForEmptyLineAfter(state1.text, state1.selection.end);
        var breaksAfter = Array(breaksAfterCount + 1).join("\n");
        // Replaces the current selection with the quote mark up
        textApi.replaceSelection(breaksBefore + "> " + state1.selectedText + breaksAfter);
        var selectionStart = state1.selection.start + breaksBeforeCount + 2;
        var selectionEnd = selectionStart + state1.selectedText.length;
        textApi.setSelectionRange({
            start: selectionStart,
            end: selectionEnd
        });
    }
};
