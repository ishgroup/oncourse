import { FieldSet } from "./../field/FieldSet";

export class CreateContactParams {

  /**
   * First Name of Contact
   */
  firstName: string;

  /**
   * Last Name of Contact
   */
  lastName: string;

  /**
   * Email of Contact
   */
  email: string;

  fieldSet: FieldSet;
}

