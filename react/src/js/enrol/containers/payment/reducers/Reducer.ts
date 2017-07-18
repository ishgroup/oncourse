import {State} from "./State";
import {
  APPLY_CORPORATE_PASS, CHANGE_TAB, RESET_CORPORATE_PASS, RESET_PAYMENT_STATE, TOGGLE_CORPORATE_PASS_AVAILABILITY,
  UPDATE_PAYMENT_STATUS,
} from "../actions/Actions";
import {IAction} from "../../../../actions/IshAction";

export const Reducer = (state: State = new State(), action: IAction<any>): State => {
  switch (action.type) {

    case UPDATE_PAYMENT_STATUS:
      return {
        ...state,
        value: action.payload,
      };

    case APPLY_CORPORATE_PASS:
      return {
        ...state,
        corporatePass: action.payload,
      };

    case RESET_CORPORATE_PASS:
      return {
        ...state,
        corporatePass: {},
      };

    case CHANGE_TAB:
      return {
        ...state,
        currentTab: action.payload,
      };

    case TOGGLE_CORPORATE_PASS_AVAILABILITY:
      return {
        ...state,
        corporatePassAvailable: action.payload,
      };

    case RESET_PAYMENT_STATE:
      return new State();

    default:
      return state;
  }
};
