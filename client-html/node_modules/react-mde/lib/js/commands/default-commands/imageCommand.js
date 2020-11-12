"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.imageCommand = void 0;
var MarkdownUtil_1 = require("../../util/MarkdownUtil");
exports.imageCommand = {
    buttonProps: { "aria-label": "Add image" },
    execute: function (_a) {
        var initialState = _a.initialState, textApi = _a.textApi;
        // Replaces the current selection with the whole word selected
        var state1 = textApi.setSelectionRange(MarkdownUtil_1.selectWord({
            text: initialState.text,
            selection: initialState.selection
        }));
        // Replaces the current selection with the image
        var imageTemplate = state1.selectedText || "https://example.com/your-image.png";
        textApi.replaceSelection("![](" + imageTemplate + ")");
        // Adjust the selection to not contain the **
        textApi.setSelectionRange({
            start: state1.selection.start + 4,
            end: state1.selection.start + 4 + imageTemplate.length
        });
    }
};
