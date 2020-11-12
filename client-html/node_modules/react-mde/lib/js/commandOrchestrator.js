"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (_) try {
            if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [op[0] & 2, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};
Object.defineProperty(exports, "__esModule", { value: true });
var InsertTextAtPosition_1 = require("./util/InsertTextAtPosition");
var CommandUtils_1 = require("./util/CommandUtils");
var TextAreaTextApi = /** @class */ (function () {
    function TextAreaTextApi(textAreaRef) {
        this.textAreaRef = textAreaRef;
    }
    TextAreaTextApi.prototype.replaceSelection = function (text) {
        var textArea = this.textAreaRef.current;
        InsertTextAtPosition_1.insertText(textArea, text);
        return getStateFromTextArea(textArea);
    };
    TextAreaTextApi.prototype.setSelectionRange = function (selection) {
        var textArea = this.textAreaRef.current;
        textArea.focus();
        textArea.selectionStart = selection.start;
        textArea.selectionEnd = selection.end;
        return getStateFromTextArea(textArea);
    };
    TextAreaTextApi.prototype.getState = function () {
        var textArea = this.textAreaRef.current;
        return getStateFromTextArea(textArea);
    };
    return TextAreaTextApi;
}());
exports.TextAreaTextApi = TextAreaTextApi;
function getStateFromTextArea(textArea) {
    return {
        selection: {
            start: textArea.selectionStart,
            end: textArea.selectionEnd
        },
        text: textArea.value,
        selectedText: textArea.value.slice(textArea.selectionStart, textArea.selectionEnd)
    };
}
exports.getStateFromTextArea = getStateFromTextArea;
var CommandOrchestrator = /** @class */ (function () {
    function CommandOrchestrator(commands, textArea) {
        var _this = this;
        /**
         * Tries to find a command the wants to handle the keyboard event
         */
        this.handlePossibleKeyCommand = function (e) {
            for (var _i = 0, _a = _this.keyActivatedCommands; _i < _a.length; _i++) {
                var command = _a[_i];
                if (command.handleKeyCommand(e)) {
                    _this.executeCommand(command).then(function (r) { });
                    return true;
                }
            }
            return false;
        };
        this.commands = commands;
        this.keyActivatedCommands = CommandUtils_1.extractKeyActivatedCommands(commands);
        this.textAreaRef = textArea;
        this.textApi = new TextAreaTextApi(textArea);
    }
    CommandOrchestrator.prototype.executeCommand = function (command) {
        return __awaiter(this, void 0, void 0, function () {
            var result;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        if (this.isExecuting) {
                            // The simplest thing to do is to ignore commands while
                            // there is already a command executing. The alternative would be to queue commands
                            // but there is no guarantee that the state after one command executes will still be compatible
                            // with the next one. In fact, it is likely not to be.
                            return [2 /*return*/];
                        }
                        this.isExecuting = true;
                        result = command.execute(getStateFromTextArea(this.textAreaRef.current), this.textApi);
                        return [4 /*yield*/, result];
                    case 1:
                        _a.sent();
                        this.isExecuting = false;
                        return [2 /*return*/];
                }
            });
        });
    };
    return CommandOrchestrator;
}());
exports.CommandOrchestrator = CommandOrchestrator;
