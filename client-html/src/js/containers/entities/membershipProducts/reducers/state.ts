import {
  ContactRelationType,
  MembershipDiscount,
  MembershipProduct
} from "@api/model";

export interface MembershipProductState {
  discountsPending?: boolean;
  discountItems?: MembershipDiscount[];
  contactRelationTypes?: ContactRelationType[];
  items?: MembershipProduct[];
  search?: string;
  loading?: boolean;
  rowsCount?: number;
}
