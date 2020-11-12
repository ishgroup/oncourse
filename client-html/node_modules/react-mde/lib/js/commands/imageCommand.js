"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var MarkdownUtil_1 = require("../util/MarkdownUtil");
exports.imageCommand = {
    name: "image",
    buttonProps: { "aria-label": "Add image" },
    execute: function (state0, api) {
        // Select everything
        var newSelectionRange = MarkdownUtil_1.selectWord({
            text: state0.text,
            selection: state0.selection
        });
        var state1 = api.setSelectionRange(newSelectionRange);
        // Replaces the current selection with the image
        var imageTemplate = state1.selectedText || "https://example.com/your-image.png";
        api.replaceSelection("![](" + imageTemplate + ")");
        // Adjust the selection to not contain the **
        api.setSelectionRange({
            start: 4 + state1.selection.start,
            end: 4 + state1.selection.start + imageTemplate.length
        });
    }
};
