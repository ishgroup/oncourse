"use strict";

var _interopRequireDefault = require("@babel/runtime/helpers/interopRequireDefault");

exports.__esModule = true;
exports["default"] = createValues;

var _extends2 = _interopRequireDefault(require("@babel/runtime/helpers/extends"));

var _reactRedux = require("react-redux");

function createValues(_ref) {
  var getIn = _ref.getIn;
  return function (config) {
    var _prop$getFormState$co = (0, _extends2["default"])({
      prop: 'values',
      getFormState: function getFormState(state) {
        return getIn(state, 'form');
      }
    }, config),
        form = _prop$getFormState$co.form,
        prop = _prop$getFormState$co.prop,
        getFormState = _prop$getFormState$co.getFormState;

    return (0, _reactRedux.connect)(function (state) {
      var _ref2;

      return _ref2 = {}, _ref2[prop] = getIn(getFormState(state), form + ".values"), _ref2;
    } // ignore dispatch
    );
  };
}