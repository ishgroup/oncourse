/**
 * @license
 * Copyright 2017 Palantir Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
"use strict";
var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var tsutils_1 = require("tsutils");
var ts = require("typescript");
var Lint = require("../index");
var Rule = (function (_super) {
    __extends(Rule, _super);
    function Rule() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    /* tslint:enable:object-literal-sort-keys */
    Rule.FAILURE_STRING = function (cbText) {
        return "No need to wrap '" + cbText + "' in another function. Just use it directly.";
    };
    Rule.prototype.apply = function (sourceFile) {
        return this.applyWithFunction(sourceFile, walk);
    };
    return Rule;
}(Lint.Rules.AbstractRule));
/* tslint:disable:object-literal-sort-keys */
Rule.metadata = {
    ruleName: "no-unnecessary-callback-wrapper",
    description: (_a = ["\n            Replaces `x => f(x)` with just `f`.\n            To catch more cases, enable `only-arrow-functions` and `arrow-return-shorthand` too."], _a.raw = ["\n            Replaces \\`x => f(x)\\` with just \\`f\\`.\n            To catch more cases, enable \\`only-arrow-functions\\` and \\`arrow-return-shorthand\\` too."], Lint.Utils.dedent(_a)),
    optionsDescription: "Not configurable.",
    options: null,
    optionExamples: ["true"],
    type: "style",
    typescriptOnly: false,
};
exports.Rule = Rule;
function walk(ctx) {
    return ts.forEachChild(ctx.sourceFile, cb);
    function cb(node) {
        var fn = detectRedundantCallback(node);
        if (fn) {
            var fix = [
                Lint.Replacement.deleteFromTo(node.getStart(), fn.getStart()),
                Lint.Replacement.deleteFromTo(fn.getEnd(), node.getEnd()),
            ];
            ctx.addFailureAtNode(node, Rule.FAILURE_STRING(fn.getText()), fix);
        }
        else {
            return ts.forEachChild(node, cb);
        }
    }
}
// Returns the `f` in `x => f(x)`.
function detectRedundantCallback(node) {
    if (!tsutils_1.isArrowFunction(node)) {
        return undefined;
    }
    var body = node.body, parameters = node.parameters;
    if (!tsutils_1.isCallExpression(body)) {
        return undefined;
    }
    var args = body.arguments, expression = body.expression;
    if (expression.kind === ts.SyntaxKind.PropertyAccessExpression) {
        // Allow `x => obj.f(x)`
        return undefined;
    }
    var argumentsSameAsParameters = parameters.length === args.length && parameters.every(function (_a, i) {
        var dotDotDotToken = _a.dotDotDotToken, name = _a.name;
        var arg = args[i];
        if (dotDotDotToken) {
            // Use SpreadElementExpression for ts2.0 compatibility
            if (!(tsutils_1.isSpreadElement(arg) || arg.kind === ts.SyntaxKind.SpreadElementExpression)) {
                return false;
            }
            arg = arg.expression;
        }
        return tsutils_1.isIdentifier(name) && tsutils_1.isIdentifier(arg) && name.text === arg.text;
    });
    return argumentsSameAsParameters ? expression : undefined;
}
var _a;
