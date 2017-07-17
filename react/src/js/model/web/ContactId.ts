import {Contact} from "./../web/Contact";

export class ContactId {

  /**
   * Contact id
   */
  id?: string;

  /**
   * Indicates that new contact created
   */
  newContact?: boolean;

  /**
   * Indicates that parent required
   */
  parentRequired?: boolean;

  /**
   * Related parant, not null if parent required and parent exist
   */
  parent?: Contact;
}

