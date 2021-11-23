/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

module.exports = {
    "extends": ['airbnb-typescript'],
    "env": {
        "es6": true
    },
    "parser": "@typescript-eslint/parser",
    "parserOptions": {
        "project": "tsconfig.json",
        "sourceType": "module"
    },
    "plugins": [
        "import",
        "@typescript-eslint"
    ],
    "rules": {
        "jsx-a11y/aria-role": "off",
        "import/no-unresolved": "off",
        "import/extensions": "off",
        "import/prefer-default-export": "off",
        "@typescript-eslint/dot-notation": "off",
        "@typescript-eslint/indent": "off",
        "@typescript-eslint/comma-dangle": "off",
        "@typescript-eslint/member-delimiter-style": [
            "off",
            {
                "multiline": {
                    "delimiter": "none",
                    "requireLast": true
                },
                "singleline": {
                    "delimiter": "semi",
                    "requireLast": false
                }
            }
        ],
        "@typescript-eslint/no-this-alias": "error",
        "@typescript-eslint/quotes": "off",
        "@typescript-eslint/semi": [
            "off",
            null
        ],
        "@typescript-eslint/type-annotation-spacing": "off",
        "no-param-reassign": "off",
        "no-nested-ternary": "off",
        "arrow-parens": [
            "error",
            "as-needed"
        ],
        "default-case": "off",
        "dot-notation": "off",
        "camelcase": "error",
        "comma-dangle": "off",
        "consistent-return": "off",
        "curly": [
            "error",
            "multi-line"
        ],
        "eol-last": "off",
        "eqeqeq": [
            "error",
            "smart"
        ],
        "id-blacklist": [
            "error",
            "any",
            "Number",
            "number",
            "String",
            "string",
            "Boolean",
            "boolean",
            "Undefined"
        ],
        "id-match": "error",
        "implicit-arrow-linebreak": "off",
        // "linebreak-style": "off",
        "max-len": [
            "error",
            {
                "code": 140
            }
        ],
        "new-parens": "off",
        "newline-per-chained-call": "off",
        "no-restricted-syntax": "off",
        "no-await-in-loop": "off",
        "no-prototype-builtins": "off",
        "no-duplicate-imports": "error",
        "no-eval": "error",
        "no-extra-semi": "off",
        "no-irregular-whitespace": "off",
        "no-multiple-empty-lines": [
            "error",
            {
                "max": 1
            }
        ],
        "no-new-wrappers": "error",
        "no-plusplus": "off",
        "no-trailing-spaces": "off",
        "no-underscore-dangle": "error",
        "no-unused-expressions": "off",
        "no-shadow": "off",
        "no-var": "error",
        "object-shorthand": "error",
        "one-var": [
            "error",
            "never"
        ],
        "prefer-const": "error",
        "prefer-destructuring": "off",
        "prefer-template": "off",
        "quote-props": "off",
        "radix": "error",
        "react/no-this-in-sfc": "off",
        "react/require-default-props": "off",
        "react/prop-types": "off",
        "react/state-in-constructor": "off",
        "react/sort-comp": "off",
        "react/destructuring-assignment": "off",
        "react/no-did-update-set-state": "off",
        "react/jsx-props-no-spreading": "off",
        "react/jsx-boolean-value": "off",
        "semi": "error",
        "space-before-function-paren": "off",
        "space-in-parens": [
            "off",
            "never"
        ],
        "spaced-comment": "error",
        "func-names": "off",
    },
    "settings": {
        "import/parsers": {
            "@typescript-eslint/parser": [".ts", ".tsx"]
        },
        "import/resolver": {
            "typescript": {
                "directory": ["tsconfig.json", "tsconfig.dev.json", "tsconfig.test.json"]
            }
        }
    }
};
