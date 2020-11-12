import { IAction } from "../../../../common/actions/IshAction";
import {
  GET_INCOME_ACCOUNTS,
  GET_INCOME_ACCOUNTS_FULFILLED,
  GET_LIABILITY_ACCOUNTS,
  GET_LIABILITY_ACCOUNTS_FULFILLED,
  GET_PLAIN_ACCOUNTS_FULFILLED
} from "../actions";
import { AccountsState } from "./state";

const accountsInitial: AccountsState = {
  items: [],
  incomeItems: [],
  updatingIncomeItems: false,
  liabilityItems: [],
  updatingLiabilityItems: false
};

export const accountEntityReducer = (state: AccountsState = accountsInitial, action: IAction<any>): any => {
  switch (action.type) {
    case GET_INCOME_ACCOUNTS: {
      return {
        ...state,
        updatingIncomeItems: true
      };
    }

    case GET_INCOME_ACCOUNTS_FULFILLED: {
      return {
        ...state,
        ...action.payload,
        updatingIncomeItems: false
      };
    }

    case GET_LIABILITY_ACCOUNTS: {
      return {
        ...state,
        updatingLiabilityItems: true
      };
    }

    case GET_LIABILITY_ACCOUNTS_FULFILLED: {
      return {
        ...state,
        ...action.payload,
        updatingLiabilityItems: false
      };
    }

    case GET_PLAIN_ACCOUNTS_FULFILLED: {
      return {
        ...state,
        ...action.payload
      };
    }

    default:
      return state;
  }
};
