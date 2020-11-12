"use strict";

exports.__esModule = true;
exports["default"] = createGetFormNames;

function createGetFormNames(_ref) {
  var getIn = _ref.getIn,
      keys = _ref.keys;
  return function (getFormState) {
    return function (state) {
      var nonNullGetFormState = getFormState || function (state) {
        return getIn(state, 'form');
      };

      return keys(nonNullGetFormState(state));
    };
  };
}