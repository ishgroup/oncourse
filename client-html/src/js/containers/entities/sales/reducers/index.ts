import { IAction } from "../../../../common/actions/IshAction";
import {
  CLEAR_COURSE_CLASS_SALES,
  CLEAR_SALES,
  GET_COURSE_CLASS_SALES,
  GET_COURSE_CLASS_SALES_FULFILLED,
  GET_SALES,
  GET_SALES_FULFILLED,
  SET_SALE_DETAILS
} from "../actions";
import { SaleState } from "./state";

export const saleReducer = (state: SaleState = {}, action: IAction<any>): any => {
  switch (action.type) {
    case CLEAR_SALES:
    case SET_SALE_DETAILS:
    case CLEAR_COURSE_CLASS_SALES: {
      return {
        ...state,
        ...action.payload
      };
    }

    case GET_COURSE_CLASS_SALES:
    case GET_SALES: {
      return {
        ...state,
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

    case GET_COURSE_CLASS_SALES_FULFILLED: {
      const { courseClassItems } = action.payload;

      return {
        ...state,
        pending: false,
        courseClassItems: courseClassItems.length ? courseClassItems : null
      };
    }

    default:
      return state;
  }
};
