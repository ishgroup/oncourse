"use strict";
/*!
  Copyright (c) 2018 Jed Watson.
  Licensed under the MIT License (MIT), see
  http://jedwatson.github.io/classnames
*/
Object.defineProperty(exports, "__esModule", { value: true });
exports.classNames = void 0;
function isString(classValue) {
    return typeof classValue === "string";
}
function isNonEmptyArray(classValue) {
    return Array.isArray(classValue) && classValue.length > 0;
}
function isClassDictionary(classValue) {
    return typeof classValue === "object";
}
function classNames() {
    var classValues = [];
    for (var _i = 0; _i < arguments.length; _i++) {
        classValues[_i] = arguments[_i];
    }
    var classes = [];
    for (var i = 0; i < classValues.length; i++) {
        var classValue = classValues[i];
        if (!classValue)
            continue;
        if (isString(classValue)) {
            classes.push(classValue);
        }
        else if (isNonEmptyArray(classValue)) {
            var inner = classNames.apply(null, classValue);
            if (inner) {
                classes.push(inner);
            }
        }
        else if (isClassDictionary(classValue)) {
            for (var key in classValue) {
                if (classValue.hasOwnProperty(key) && classValue[key]) {
                    classes.push(key);
                }
            }
        }
    }
    return classes.join(" ");
}
exports.classNames = classNames;
