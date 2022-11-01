import { IAction } from "../../../../common/actions/IshAction";
import { GET_DISCOUNT_CONTACT_RELATION_TYPES_FULFILLED, GET_DISCOUNT_COS_ACCOUNTS_FULFILLED, } from "../actions";
import { DiscountsState } from "./state";

export const discountsReducer = (state: DiscountsState = {}, action: IAction<any>): any => {
  switch (action.type) {
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

    default:
      return state;
  }
};
