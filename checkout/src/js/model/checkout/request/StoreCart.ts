export interface StoreCartCustomField {
  id: string;
  value: string;
}

export interface StoreCartCustomFieldGroup {
  fields: StoreCartCustomField[]
}

export interface StoreCartItem {
  id: string;
  selected: boolean;
  quantity?: number;
  customFields?: StoreCartCustomFieldGroup[];
}

export interface StoreCartContact {
  contactId: string;
  applications?: StoreCartItem[];
  classes?: StoreCartItem[];
  waitingCourses?: StoreCartItem[];
  products?: StoreCartItem[];
}

export interface StoreCartRequest {
  payerId: string;
  checkoutURL: string;
  contacts: StoreCartContact[];
  total?: string;
  promotionIds?: string[];
}
