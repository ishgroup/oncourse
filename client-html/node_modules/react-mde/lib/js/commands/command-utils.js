"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.extractKeyActivatedCommands = void 0;
/**
 * Returns a flat array of commands that can be activated by the keyboard.
 * When keydowns happen, these commands 'handleKeyCommand' will be executed, in this order,
 * and the first that returns true will be executed.
 */
function extractKeyActivatedCommands(commandMap) {
    var result = [];
    for (var command in commandMap) {
        if (commandMap.hasOwnProperty(command)) {
            if (commandMap[command].handleKeyCommand) {
                result.push(command);
            }
        }
    }
    return result;
}
exports.extractKeyActivatedCommands = extractKeyActivatedCommands;
