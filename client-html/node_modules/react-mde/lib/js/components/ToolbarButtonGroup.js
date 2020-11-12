"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.ToolbarButtonGroup = void 0;
var React = require("react");
var ClassNames_1 = require("../util/ClassNames");
exports.ToolbarButtonGroup = function (props) {
    return (React.createElement("ul", { className: ClassNames_1.classNames("mde-header-group", { hidden: props.hidden }) }, props.children));
};
