export class FieldError {
  /**
   * order position
   */
  index: number;

  /**
   * Name of fiels with error
   */
  name: string;

  /**
   * Errors for particular field, or empty array
   */
  error: string;
}
