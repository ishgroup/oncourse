import {Concession} from "./../checkout/concession/Concession";
import {Field} from "./../field/Field";

export class SubmitFieldsRequest {

  /**
   * Submited contact id
   */
  contactId?: string;

  /**
   * Array of fields
   */
  fields?: Field[];

  /**
   * Contact's concession
   */
  concession?: Concession;
}

