"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var boldCommand_1 = require("./boldCommand");
exports.boldCommand = boldCommand_1.boldCommand;
var italicCommand_1 = require("./italicCommand");
exports.italicCommand = italicCommand_1.italicCommand;
var strikeThroughCommand_1 = require("./strikeThroughCommand");
exports.strikeThroughCommand = strikeThroughCommand_1.strikeThroughCommand;
var headerCommand_1 = require("./headerCommand");
exports.headerCommand = headerCommand_1.headerCommand;
var linkCommand_1 = require("./linkCommand");
exports.linkCommand = linkCommand_1.linkCommand;
var quoteCommand_1 = require("./quoteCommand");
exports.quoteCommand = quoteCommand_1.quoteCommand;
var codeCommand_1 = require("./codeCommand");
exports.codeCommand = codeCommand_1.codeCommand;
var imageCommand_1 = require("./imageCommand");
exports.imageCommand = imageCommand_1.imageCommand;
var listCommands_1 = require("./listCommands");
exports.checkedListCommand = listCommands_1.checkedListCommand;
exports.orderedListCommand = listCommands_1.orderedListCommand;
exports.unorderedListCommand = listCommands_1.unorderedListCommand;
var getDefaultCommands = function () { return [
    {
        commands: [headerCommand_1.headerCommand, boldCommand_1.boldCommand, italicCommand_1.italicCommand, strikeThroughCommand_1.strikeThroughCommand]
    },
    {
        commands: [linkCommand_1.linkCommand, quoteCommand_1.quoteCommand, codeCommand_1.codeCommand, imageCommand_1.imageCommand]
    },
    {
        commands: [listCommands_1.unorderedListCommand, listCommands_1.orderedListCommand, listCommands_1.checkedListCommand]
    }
]; };
exports.getDefaultCommands = getDefaultCommands;
