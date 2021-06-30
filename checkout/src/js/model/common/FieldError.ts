
export class FieldError {
  /**
   * index of field with error
   */
  index: number;

  /**
   * Name of field with error
   */
  name: string;

  /**
   * Errors for particular field, or empty array
   */
  error: string;
}

