"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var React = require("react");
var MarkdownUtil_1 = require("../util/MarkdownUtil");
function setHeader(state0, api, prefix) {
    // Adjust the selection to encompass the whole word if the caret is inside one
    var newSelectionRange = MarkdownUtil_1.selectWord({
        text: state0.text,
        selection: state0.selection
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
    name: "header",
    buttonProps: { "aria-label": "Add header" },
    children: [
        {
            name: "header-1",
            icon: function () { return React.createElement("p", { className: "header-1" }, "Header 1"); },
            execute: function (state, api) { return setHeader(state, api, "# "); }
        },
        {
            name: "header-2",
            icon: function () { return React.createElement("p", { className: "header-2" }, "Header 2"); },
            execute: function (state, api) { return setHeader(state, api, "## "); }
        },
        {
            name: "header-3",
            icon: function () { return React.createElement("p", { className: "header-3" }, "Header 3"); },
            execute: function (state, api) { return setHeader(state, api, "### "); }
        },
        {
            name: "header-4",
            icon: function () { return React.createElement("p", { className: "header-4" }, "Header 4"); },
            execute: function (state, api) {
                return setHeader(state, api, "#### ");
            }
        }
    ]
};
