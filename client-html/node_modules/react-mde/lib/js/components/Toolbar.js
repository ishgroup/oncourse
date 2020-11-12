"use strict";
var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
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
exports.Toolbar = void 0;
var React = require("react");
var ClassNames_1 = require("../util/ClassNames");
var ToolbarButtonGroup_1 = require("./ToolbarButtonGroup");
var ToolbarButton_1 = require("./ToolbarButton");
var Toolbar = /** @class */ (function (_super) {
    __extends(Toolbar, _super);
    function Toolbar() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        _this.handleTabChange = function (tab) {
            var onTabChange = _this.props.onTabChange;
            onTabChange(tab);
        };
        return _this;
    }
    Toolbar.prototype.render = function () {
        var _this = this;
        var l18n = this.props.l18n;
        var _a = this.props, classes = _a.classes, children = _a.children, buttons = _a.buttons, onCommand = _a.onCommand, readOnly = _a.readOnly, disablePreview = _a.disablePreview, writeButtonProps = _a.writeButtonProps, previewButtonProps = _a.previewButtonProps, buttonProps = _a.buttonProps;
        if ((!buttons || buttons.length === 0) && !children) {
            return null;
        }
        var writePreviewTabs = (React.createElement("div", { className: "mde-tabs" },
            React.createElement("button", __assign({ type: "button", className: ClassNames_1.classNames({ selected: this.props.tab === "write" }), onClick: function () { return _this.handleTabChange("write"); } }, writeButtonProps), l18n.write),
            React.createElement("button", __assign({ type: "button", className: ClassNames_1.classNames({ selected: this.props.tab === "preview" }), onClick: function () { return _this.handleTabChange("preview"); } }, previewButtonProps), l18n.preview)));
        return (React.createElement("div", { className: ClassNames_1.classNames("mde-header", classes) },
            !disablePreview && writePreviewTabs,
            buttons.map(function (commandGroup, i) { return (React.createElement(ToolbarButtonGroup_1.ToolbarButtonGroup, { key: i, hidden: _this.props.tab === "preview" }, commandGroup.map(function (c, j) {
                return (React.createElement(ToolbarButton_1.ToolbarButton, { key: j, name: c.commandName, buttonContent: c.buttonContent, buttonProps: __assign(__assign({}, (buttonProps || {})), c.buttonProps), onClick: function () { return onCommand(c.commandName); }, readOnly: readOnly, buttonComponentClass: c.buttonComponentClass }));
            }))); })));
    };
    return Toolbar;
}(React.Component));
exports.Toolbar = Toolbar;
