"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.strikeThroughCommand = void 0;
var MarkdownUtil_1 = require("../../util/MarkdownUtil");
exports.strikeThroughCommand = {
    buttonProps: { "aria-label": "Add strikethrough text" },
    execute: function (_a) {
        var initialState = _a.initialState, textApi = _a.textApi;
        // Adjust the selection to encompass the whole word if the caret is inside one
        var newSelectionRange = MarkdownUtil_1.selectWord({
            text: initialState.text,
            selection: initialState.selection
        });
        var state1 = textApi.setSelectionRange(newSelectionRange);
        // Replaces the current selection with the strikethrough mark up
        var state2 = textApi.replaceSelection("~~" + state1.selectedText + "~~");
        // Adjust the selection to not contain the ~~
        textApi.setSelectionRange({
            start: state2.selection.end - 2 - state1.selectedText.length,
            end: state2.selection.end - 2
        });
    }
};
