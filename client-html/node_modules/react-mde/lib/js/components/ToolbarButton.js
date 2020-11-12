"use strict";
var __assign = (this && this.__assign) || function () {
    __assign = Object.assign || function(t) {
        for (var s, i = 1, n = arguments.length; i < n; i++) {
            s = arguments[i];
            for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p))
                t[p] = s[p];
        }
        return t;
    };
    return __assign.apply(this, arguments);
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.ToolbarButton = void 0;
var React = require("react");
var defaultButtonProps = {
    tabIndex: -1
};
exports.ToolbarButton = function (props) {
    var buttonComponentClass = props.buttonComponentClass, buttonContent = props.buttonContent, buttonProps = props.buttonProps, onClick = props.onClick, readOnly = props.readOnly, name = props.name;
    var finalButtonProps = __assign(__assign({}, defaultButtonProps), (buttonProps || {}));
    var finalButtonComponent = buttonComponentClass || "button";
    return (React.createElement("li", { className: "mde-header-item" }, React.createElement(finalButtonComponent, __assign(__assign({ "data-name": name }, finalButtonProps), {
        onClick: onClick,
        disabled: readOnly,
        type: "button"
    }), buttonContent)));
};
