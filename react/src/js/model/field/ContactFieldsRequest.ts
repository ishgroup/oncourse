import {FieldSet} from "./../web/FieldSet";

export class ContactFieldsRequest {

  /**
   * Requested contact id
   */
  contactId?: string;

  /**
   * Requested classe's ids
   */
  classesIds?: string[];
  fieldSet?: FieldSet;
}

