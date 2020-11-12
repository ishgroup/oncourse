"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.headerCommand = void 0;
var MarkdownUtil_1 = require("../../util/MarkdownUtil");
function setHeader(initialState, api, prefix) {
    // Adjust the selection to encompass the whole word if the caret is inside one
    var newSelectionRange = MarkdownUtil_1.selectWord({
        text: initialState.text,
        selection: initialState.selection
    });
    var state1 = api.setSelectionRange(newSelectionRange);
    // Add the prefix to the selection
    var state2 = api.replaceSelection("" + prefix + state1.selectedText);
    // Adjust the selection to not contain the prefix
    api.setSelectionRange({
        start: state2.selection.end - state1.selectedText.length,
        end: state2.selection.end
    });
}
exports.headerCommand = {
    buttonProps: { "aria-label": "Add header" },
    execute: function (_a) {
        var initialState = _a.initialState, textApi = _a.textApi;
        setHeader(initialState, textApi, "### ");
    }
};
