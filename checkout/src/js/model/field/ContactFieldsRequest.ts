import { ProductContainer } from '../checkout/request/ProductContainer';
import { FieldSet } from './FieldSet';

export class ContactFieldsRequest {
  /**
   * Requested contact id
   */
  contactId: string;

  /**
   * Requested classe's ids
   */
  classIds: string[];

  /**
   * Requested product's ids
   */
  products: ProductContainer[];

  /**
   * Requested waiting list courses ids
   */
  waitingCourseIds: string[];

  /**
   * Flag to show only mandatory fields
   */
  mandatoryOnly: boolean;

  fieldSet: FieldSet;

  isPayer: boolean;

  isParent: boolean;
}
