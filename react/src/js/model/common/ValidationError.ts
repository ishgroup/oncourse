import {FieldError} from "../model/../common/FieldError";

export class ValidationError {

  /**
   * Global error
   */
  formErrors: string[];

  /**
   * Array of fields errors, or empty array
   */
  fieldsErrors: FieldError[];
}

