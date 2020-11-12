"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.getDefaultSaveImageCommandName = exports.getDefaultCommandMap = exports.getDefaultToolbarCommands = void 0;
var headerCommand_1 = require("./headerCommand");
var boldCommand_1 = require("./boldCommand");
var italicCommand_1 = require("./italicCommand");
var strikeThroughCommand_1 = require("./strikeThroughCommand");
var linkCommand_1 = require("./linkCommand");
var quoteCommand_1 = require("./quoteCommand");
var codeCommand_1 = require("./codeCommand");
var listCommands_1 = require("./listCommands");
var imageCommand_1 = require("./imageCommand");
var save_image_command_1 = require("./save-image-command");
function getDefaultToolbarCommands() {
    return [
        ["header", "bold", "italic", "strikethrough"],
        ["link", "quote", "code", "image"],
        ["unordered-list", "ordered-list", "checked-list"]
    ];
}
exports.getDefaultToolbarCommands = getDefaultToolbarCommands;
function getDefaultCommandMap() {
    return {
        header: headerCommand_1.headerCommand,
        bold: boldCommand_1.boldCommand,
        italic: italicCommand_1.italicCommand,
        strikethrough: strikeThroughCommand_1.strikeThroughCommand,
        link: linkCommand_1.linkCommand,
        quote: quoteCommand_1.quoteCommand,
        code: codeCommand_1.codeCommand,
        image: imageCommand_1.imageCommand,
        "unordered-list": listCommands_1.unorderedListCommand,
        "ordered-list": listCommands_1.orderedListCommand,
        "checked-list": listCommands_1.checkedListCommand,
        "save-image": save_image_command_1.saveImageCommand
    };
}
exports.getDefaultCommandMap = getDefaultCommandMap;
function getDefaultSaveImageCommandName() {
    return "save-image";
}
exports.getDefaultSaveImageCommandName = getDefaultSaveImageCommandName;
