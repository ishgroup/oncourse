"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var MarkdownUtil_1 = require("../util/MarkdownUtil");
exports.codeCommand = {
    name: "code",
    buttonProps: { "aria-label": "Insert code" },
    execute: function (state0, api) {
        // Adjust the selection to encompass the whole word if the caret is inside one
        var newSelectionRange = MarkdownUtil_1.selectWord({
            text: state0.text,
            selection: state0.selection
        });
        var state1 = api.setSelectionRange(newSelectionRange);
        // when there's no breaking line
        if (state1.selectedText.indexOf("\n") === -1) {
            api.replaceSelection("`" + state1.selectedText + "`");
            // Adjust the selection to not contain the **
            var selectionStart_1 = state1.selection.start + 1;
            var selectionEnd_1 = selectionStart_1 + state1.selectedText.length;
            api.setSelectionRange({
                start: selectionStart_1,
                end: selectionEnd_1
            });
            return;
        }
        var breaksBeforeCount = MarkdownUtil_1.getBreaksNeededForEmptyLineBefore(state1.text, state1.selection.start);
        var breaksBefore = Array(breaksBeforeCount + 1).join("\n");
        var breaksAfterCount = MarkdownUtil_1.getBreaksNeededForEmptyLineAfter(state1.text, state1.selection.end);
        var breaksAfter = Array(breaksAfterCount + 1).join("\n");
        api.replaceSelection(breaksBefore + "```\n" + state1.selectedText + "\n```" + breaksAfter);
        var selectionStart = state1.selection.start + breaksBeforeCount + 4;
        var selectionEnd = selectionStart + state1.selectedText.length;
        api.setSelectionRange({
            start: selectionStart,
            end: selectionEnd
        });
    }
};
