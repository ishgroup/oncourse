/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { MembershipProduct } from "@api/model";
import { MembershipProductState } from "./state";
import { IAction } from "../../../../common/actions/IshAction";
import {
  CLEAR_DISCOUNTS_SEARCH,
  CLEAR_PLAIN_MEMBERSHIP_PRODUCTS_SEARCH,
  GET_MEMBERSHIP_PRODUCT_CONTACT_RELATION_TYPES_FULFILLED,
  GET_MEMBERSHIP_PRODUCT_DISCOUNTS_FULFILLED,
  GET_PLAIN_MEMBERSHIP_PRODUCTS,
  GET_PLAIN_MEMBERSHIP_PRODUCTS_FULFILLED,
  SET_PLAIN_MEMBERSHIP_PRODUCTS_SEARCH
} from "../actions";

class MembershipProductStateClass implements MembershipProductState {
  discountsPending = false;

  discountItems = [];

  contactRelationTypes = [];

  items: MembershipProduct[] = [];

  search: string = "";

  loading: boolean = false;

  rowsCount: number = 5000;
}

export const membershipProductReducer = (
  state: MembershipProductState = new MembershipProductStateClass(),
  action: IAction<any>
): any => {
  switch (action.type) {
    case GET_MEMBERSHIP_PRODUCT_DISCOUNTS_FULFILLED: {
      return {
        ...state,
        ...action.payload
      };
    }

    case GET_MEMBERSHIP_PRODUCT_CONTACT_RELATION_TYPES_FULFILLED: {
      return {
        ...state,
        ...action.payload
      };
    }

    case CLEAR_DISCOUNTS_SEARCH: {
      return {
        ...state,
        ...action.payload
      };
    }

    case GET_PLAIN_MEMBERSHIP_PRODUCTS: {
      return {
        ...state,
        loading: true
      };
    }

    case SET_PLAIN_MEMBERSHIP_PRODUCTS_SEARCH: {
      return {
        ...state,
        ...action.payload
      };
    }

    case CLEAR_PLAIN_MEMBERSHIP_PRODUCTS_SEARCH: {
      return {
        ...state,
        ...action.payload
      };
    }

    case GET_PLAIN_MEMBERSHIP_PRODUCTS_FULFILLED: {
      const { items, offset, pageSize } = action.payload;
      const updated = offset ? state.items.concat(items) : items;
      return {
        ...state,
        loading: false,
        items: updated,
        rowsCount: pageSize < 100 ? pageSize : 5000
      };
    }

    default:
      return state;
  }
};
