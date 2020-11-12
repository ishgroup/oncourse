"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
/**
 * Returns a flat array of commands that can be activated by the keyboard.
 * When keydowns happen, these commands 'handleKeyCommand' will be executed, in this order,
 * and the first that returns true will be executed.
 */
function extractKeyActivatedCommands(groups) {
    var result = [];
    if (!groups || !groups.length) {
        return result;
    }
    for (var _i = 0, groups_1 = groups; _i < groups_1.length; _i++) {
        var group = groups_1[_i];
        if (group.commands && group.commands.length) {
            for (var _a = 0, _b = group.commands; _a < _b.length; _a++) {
                var command = _b[_a];
                if (command.handleKeyCommand) {
                    result.push(command);
                }
            }
        }
    }
    return result;
}
exports.extractKeyActivatedCommands = extractKeyActivatedCommands;
