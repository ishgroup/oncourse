import { ProductContainer } from './ProductContainer';

export class ContactNodeRequest {
  contactId: string;

  classIds: string[];

  waitingCourseIds: string[];

  products: ProductContainer[];

  promotionIds: string[];
}
