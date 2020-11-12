"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.linkCommand = void 0;
var MarkdownUtil_1 = require("../../util/MarkdownUtil");
exports.linkCommand = {
    buttonProps: { "aria-label": "Add a link" },
    execute: function (_a) {
        var initialState = _a.initialState, textApi = _a.textApi;
        // Adjust the selection to encompass the whole word if the caret is inside one
        var newSelectionRange = MarkdownUtil_1.selectWord({
            text: initialState.text,
            selection: initialState.selection
        });
        var state1 = textApi.setSelectionRange(newSelectionRange);
        // Replaces the current selection with the bold mark up
        var state2 = textApi.replaceSelection("[" + state1.selectedText + "](url)");
        // Adjust the selection to not contain the **
        textApi.setSelectionRange({
            start: state2.selection.end - 6 - state1.selectedText.length,
            end: state2.selection.end - 6
        });
    },
    handleKeyCommand: function (e) { return (e.ctrlKey || e.metaKey) && e.key == "k"; }
};
