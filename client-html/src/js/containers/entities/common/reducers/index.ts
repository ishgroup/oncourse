import { QuickSearchConcessionTypesState, QuickSearchCorporatePassState } from "./state";
import { IAction } from "../../../../common/actions/IshAction";
import {
  CLEAR_QUICK_SEARCH_CONCESSION_TYPE,
  CLEAR_QUICK_SEARCH_CORPORATE_PASSES,
  GET_QUICK_SEARCH_CONCESSION_TYPES_FULFILLED,
  GET_QUICK_SEARCH_CORPORATE_PASSES_FULFILLED
} from "../actions";

export const quickSearchConcessionTypeReducer = (
  state: QuickSearchConcessionTypesState = {},
  action: IAction<any>
): any => {
  switch (action.type) {
    case GET_QUICK_SEARCH_CONCESSION_TYPES_FULFILLED: {
      return {
        ...state,
        ...action.payload
      };
    }

    case CLEAR_QUICK_SEARCH_CONCESSION_TYPE: {
      return action.payload;
    }

    default:
      return state;
  }
};

export const quickSearchCorporatePassReducer = (
  state: QuickSearchCorporatePassState = {},
  action: IAction<any>
): any => {
  switch (action.type) {
    case GET_QUICK_SEARCH_CORPORATE_PASSES_FULFILLED: {
      return {
        ...state,
        ...action.payload
      };
    }

    case CLEAR_QUICK_SEARCH_CORPORATE_PASSES: {
      return action.payload;
    }

    default:
      return state;
  }
};
