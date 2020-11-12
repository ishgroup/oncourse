import _inheritsLoose from "@babel/runtime/helpers/inheritsLoose";
import ExtendableError from 'es6-error';
var __FLAG__ = '@@redux-form/submission-error-flag';
export var SubmissionError =
/*#__PURE__*/
function (_ExtendableError) {
  _inheritsLoose(SubmissionError, _ExtendableError);

  /** @private */
  function SubmissionError(errors) {
    var _this;

    _this = _ExtendableError.call(this, 'Submit Validation Failed') || this;
    _this.errors = errors;
    return _this;
  }

  return SubmissionError;
}(ExtendableError);
SubmissionError.__FLAG__ = __FLAG__;
export function isSubmissionError(error) {
  return (error && error.constructor && error.constructor.__FLAG__ === __FLAG__) === true;
}