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
var React = require("react");
var ToolbarButton_1 = require("./ToolbarButton");
var defaultHeaderButtonProps = {
    tabIndex: -1
};
var ToolbarDropdown = /** @class */ (function (_super) {
    __extends(ToolbarDropdown, _super);
    function ToolbarDropdown(props) {
        var _this = _super.call(this, props) || this;
        _this.handleGlobalClick = function (e) {
            if (_this.clickedOutside(e)) {
                _this.closeDropdown();
            }
        };
        _this.openDropdown = function () {
            _this.setState({
                open: true
            });
        };
        _this.clickedOutside = function (e) {
            var target = e.target;
            return (_this.state.open &&
                _this.dropdown &&
                _this.dropdownOpener &&
                !_this.dropdown.contains(target) &&
                !_this.dropdownOpener.contains(target));
        };
        _this.handleOnClickCommand = function (e, command) {
            var onCommand = _this.props.onCommand;
            onCommand(command);
            _this.closeDropdown();
        };
        _this.handleClick = function () {
            if (!_this.state.open)
                _this.openDropdown();
            else
                _this.closeDropdown();
        };
        _this.state = {
            open: false
        };
        return _this;
    }
    ToolbarDropdown.prototype.componentDidMount = function () {
        document.addEventListener("click", this.handleGlobalClick, false);
    };
    ToolbarDropdown.prototype.componentWillUnmount = function () {
        document.removeEventListener("click", this.handleGlobalClick, false);
    };
    ToolbarDropdown.prototype.closeDropdown = function () {
        this.setState({
            open: false
        });
    };
    ToolbarDropdown.prototype.render = function () {
        var _this = this;
        var _a = this.props, getIcon = _a.getIcon, commands = _a.commands, readOnly = _a.readOnly;
        var open = this.state.open;
        var items = commands.map(function (command, index) {
            return (React.createElement(ToolbarButton_1.ToolbarButton, { key: "header-item" + index, name: command.name, buttonProps: command.buttonProps, buttonContent: command.icon ? command.icon(getIcon) : getIcon(command.name), onClick: function (e) { return _this.handleOnClickCommand(e, command); }, readOnly: readOnly }));
        });
        var dropdown = open ? (React.createElement("ul", { className: "react-mde-dropdown", ref: function (ref) {
                _this.dropdown = ref;
            } }, items)) : null;
        var _b = this.props, buttonContent = _b.buttonContent, buttonProps = _b.buttonProps;
        var finalButtonProps = __assign(__assign({}, defaultHeaderButtonProps), (buttonProps || {}));
        return (React.createElement("li", { className: "mde-header-item" },
            React.createElement("button", __assign({ type: "button" }, finalButtonProps, { ref: function (ref) {
                    _this.dropdownOpener = ref;
                }, onClick: this.handleClick, disabled: readOnly }), buttonContent),
            dropdown));
    };
    return ToolbarDropdown;
}(React.Component));
exports.ToolbarDropdown = ToolbarDropdown;
