import { Field } from "./../field/Field";

export class FieldHeading {

  /**
   * Heading name
   */
  name?: string;

  /**
   * Heading description
   */
  description?: string;

  /**
   * Fields set related to field heading
   */
  fields?: Field[];

  /**
   * order position
   */
  ordering?: number;
}

