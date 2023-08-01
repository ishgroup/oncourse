import { IAction } from "../../../../common/actions/IshAction";
import { GET_DEPOSIT_ACCOUNTS_FULFILLED, GET_DEPOSIT_PAYMENTS_FULFILLED, UPDATE_BANKING_ACCOUNT_ID } from "../actions";
import { BankingState } from "./state";

class BankingStateClass implements BankingState {
  selectedAccountId = -1;
}

export const bankingReducer = (state: BankingState = new BankingStateClass(), action: IAction<any>): any => {
  switch (action.type) {
    case GET_DEPOSIT_PAYMENTS_FULFILLED: {
      return {
        ...state,
        ...action.payload
      };
    }

    case UPDATE_BANKING_ACCOUNT_ID: {
      return {
        ...state,
        selectedAccountId: action.payload
      };
    }

    case GET_DEPOSIT_ACCOUNTS_FULFILLED: {
      return {
        ...state,
        ...action.payload
      };
    }

    default:
      return state;
  }
};
