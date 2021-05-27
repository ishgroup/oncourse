import {FieldHeading} from "../field/FieldHeading";

export class Membership {
  contactId?: string;
  productId?: string;
  warnings?: string[];
  errors?: string[];
  price?: number;
  selected?: boolean;
  allowRemove?: boolean;
  relatedClassId?: string;
  relatedProductId?: string;
  fieldHeadings?: FieldHeading[];
}

