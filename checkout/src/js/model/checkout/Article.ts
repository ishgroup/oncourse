import {FieldHeading} from "../field/FieldHeading";

export class Article {
  contactId?: string;
  productId?: string;
  warnings?: string[];
  errors?: string[];
  price?: number;
  total?: number;
  selected?: boolean;
  quantity?: number;
  allowRemove?: boolean;
  relatedClassId?: string;
  relatedProductId?: string;
  fieldHeadings?: FieldHeading[];
}

