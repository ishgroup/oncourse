"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.MdeFontAwesomeIcon = void 0;
var React = require("react");
exports.MdeFontAwesomeIcon = function (_a) {
    var icon = _a.icon;
    var transformedIcon = icon;
    switch (icon) {
        case "header":
            transformedIcon = "heading";
            break;
        case "quote":
            transformedIcon = "quote-right";
            break;
        case "unordered-list":
            transformedIcon = "tasks";
            break;
        case "ordered-list":
            transformedIcon = "list-ol";
            break;
        case "checked-list":
            transformedIcon = "tasks";
            break;
        default:
            transformedIcon = icon;
    }
    return React.createElement("i", { className: "fas fa-" + transformedIcon, "aria-hidden": "true" });
};
