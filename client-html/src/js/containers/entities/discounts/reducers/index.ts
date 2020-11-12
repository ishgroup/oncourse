import { IAction } from "../../../../common/actions/IshAction";
import {
  CLEAR_DISCOUNTS,
  GET_DISCOUNT_CONTACT_RELATION_TYPES_FULFILLED,
  GET_DISCOUNT_COS_ACCOUNTS_FULFILLED,
  GET_DISCOUNT_MEMBERSHIPS_FULFILLED,
  GET_DISCOUNTS_FULFILLED
} from "../actions";
import { DiscountsState } from "./state";

export const discountsReducer = (state: DiscountsState = {}, action: IAction<any>): any => {
  switch (action.type) {
    case GET_DISCOUNTS_FULFILLED: {
      return {
        ...state,
        ...action.payload
      };
    }

    case GET_DISCOUNT_MEMBERSHIPS_FULFILLED: {
      return {
        ...state,
        ...action.payload
      };
    }

    case GET_DISCOUNT_CONTACT_RELATION_TYPES_FULFILLED: {
      return {
        ...state,
        ...action.payload
      };
    }

    case GET_DISCOUNT_COS_ACCOUNTS_FULFILLED: {
      return {
        ...state,
        ...action.payload
      };
    }

    case CLEAR_DISCOUNTS: {
      return action.payload;
    }

    default:
      return state;
  }
};
