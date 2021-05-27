import {FieldHeading} from "../field/FieldHeading";

export class Voucher {
  contactId?: string;
  productId?: string;
  warnings?: string[];
  errors?: string[];
  price?: number;
  total?: number;
  value?: number;
  classes?: string[];
  selected?: boolean;
  isEditablePrice?: boolean;
  quantity?: number;
  allowRemove?: boolean;
  relatedClassId?: string;
  relatedProductId?: string;
  fieldHeadings?: FieldHeading[];
}

