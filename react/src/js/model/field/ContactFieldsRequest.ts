import { FieldSet } from "./../field/FieldSet";

export class ContactFieldsRequest {

  /**
   * Requested contact id
   */
  contactId?: string;

  /**
   * Requested classe's ids
   */
  classIds?: string[];

  /**
   * Requested product's ids
   */
  productIds?: string[];
  fieldSet?: FieldSet;
}

