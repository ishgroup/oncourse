import { Discount, DiscountMembership } from "@api/model";

export interface DiscountsState {
  items?: Discount[];
  pending?: boolean;
  membershipItems?: DiscountMembership[];
  membershipPending?: boolean;
  contactRelationTypes?: any[];
  cosAccounts?: any[];
}
