import { IAction } from "../../../../common/actions/IshAction";
import {
  CLEAR_COURSE_CLASS_SALES,
  CLEAR_SALES,
  GET_SALES,
  GET_SALES_FULFILLED,
  GET_SALES_REJECTED,
  SET_SALE_DETAILS
} from "../actions";
import { SaleState } from "./state";

export const saleReducer = (state: SaleState = {}, action: IAction): any => {
  switch (action.type) {
    case SET_SALE_DETAILS: {
      return {
        ...state,
        ...action.payload
      };
    }

    case CLEAR_SALES:
    case CLEAR_COURSE_CLASS_SALES: {
      return {
        ...state,
        ...action.payload,
        error: false
      };
    }

    case GET_SALES: {
      return {
        ...state,
        error: false,
        pending: true
      };
    }

    case GET_SALES_FULFILLED: {
      const { items } = action.payload;

      return {
        ...state,
        pending: false,
        items: items.length ? items : null
      };
    }

    case GET_SALES_REJECTED: {
      return {
        ...state,
        error: true,
        pending: false,
        items: []
      };
    }

    default:
      return state;
  }
};
