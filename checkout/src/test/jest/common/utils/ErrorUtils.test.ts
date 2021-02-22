import {CommonError, ValidationError}  from "../../../../js/model";
import * as ErrorUtils from "../../../../js/common/utils/ErrorUtils";

test('backend error handler', () => {

  const validationError:Object = {
    formErrors: ["test"],
    fieldsErrors: ["test"],
  };

  const commonError:Object = {
    code: 1,
    message: "Message",
  };

  const plainText:Object = "Plain Text Error";

  expect(true).toBe(ErrorUtils.isValidationError(validationError));
  expect(false).toBe(ErrorUtils.isValidationError(commonError));
  expect(false).toBe(ErrorUtils.isValidationError(plainText));

  expect(false).toBe(ErrorUtils.isCommonError(validationError));
  expect(true).toBe(ErrorUtils.isCommonError(commonError));
  expect(false).toBe(ErrorUtils.isCommonError(plainText));

  expect(false).toBe(ErrorUtils.isPlainTextError(validationError));
  expect(false).toBe(ErrorUtils.isPlainTextError(commonError));
  expect(true).toBe(ErrorUtils.isPlainTextError(plainText));

  expect(true).toBe(ErrorUtils.isValidationError(new ValidationError()));

  expect(true).toBe(ErrorUtils.isCommonError(new CommonError()));
});

