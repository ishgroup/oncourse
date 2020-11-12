import { Tax } from "@api/model";
import { IAction } from "../../../../common/actions/IshAction";
import { GET_PLAIN_TAX_ITEMS, GET_PLAIN_TAX_ITEMS_FULFILLED } from "../actions";

export interface TaxesState {
  items?: Tax[];
  updatingItems?: boolean;
}

const initial: TaxesState = {
  items: [],
  updatingItems: false
};

export const taxesReducer = (state: TaxesState = initial, action: IAction<any>): any => {
  switch (action.type) {
    case GET_PLAIN_TAX_ITEMS: {
      return {
        ...state,
        updatingItems: true
      };
    }

    case GET_PLAIN_TAX_ITEMS_FULFILLED: {
      return {
        ...state,
        ...action.payload,
        updatingItems: false
      };
    }

    default:
      return state;
  }
};
