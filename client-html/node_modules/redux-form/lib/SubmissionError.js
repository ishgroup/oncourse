"use strict";

var _interopRequireDefault = require("@babel/runtime/helpers/interopRequireDefault");

exports.__esModule = true;
exports.isSubmissionError = isSubmissionError;
exports.SubmissionError = void 0;

var _inheritsLoose2 = _interopRequireDefault(require("@babel/runtime/helpers/inheritsLoose"));

var _es6Error = _interopRequireDefault(require("es6-error"));

var __FLAG__ = '@@redux-form/submission-error-flag';

var SubmissionError =
/*#__PURE__*/
function (_ExtendableError) {
  (0, _inheritsLoose2["default"])(SubmissionError, _ExtendableError);

  /** @private */
  function SubmissionError(errors) {
    var _this;

    _this = _ExtendableError.call(this, 'Submit Validation Failed') || this;
    _this.errors = errors;
    return _this;
  }

  return SubmissionError;
}(_es6Error["default"]);

exports.SubmissionError = SubmissionError;
SubmissionError.__FLAG__ = __FLAG__;

function isSubmissionError(error) {
  return (error && error.constructor && error.constructor.__FLAG__ === __FLAG__) === true;
}