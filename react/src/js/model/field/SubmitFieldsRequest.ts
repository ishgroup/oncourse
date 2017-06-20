import {Field} from "../model/../field/Field";

export class SubmitFieldsRequest {

  /**
   * Submited contact id
   */
  contactId?: string;

  /**
   * Array of fields
   */
  fields?: Field[];
}

