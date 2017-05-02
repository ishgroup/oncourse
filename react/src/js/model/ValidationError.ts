import { FieldErrors } from "./FieldErrors";

export class ValidationError {

  /**
   * Global error
   */
  formErrors: string[];

  /**
   * Array of fields errors, or empty array
   */
  fieldsErrors: FieldErrors[];
}

