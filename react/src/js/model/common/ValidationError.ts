import {FieldError} from "./../common/FieldError";

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

