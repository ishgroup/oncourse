import {State} from "./State";
import {
  APPLY_CORPORATE_PASS, CHANGE_TAB, GET_CORPORATE_PASS_REQUEST, RESET_CORPORATE_PASS, RESET_PAYMENT_STATE,
  RESET_PAYMENT_STATE_ON_INIT,
  UPDATE_PAYMENT_STATUS, GENERATE_WAITING_COURSES_RESULT_DATA, SET_PAYMENT_DATA, PROCESS_PAYMENT_V2,
} from "../actions/Actions";
import {IAction} from "../../../../actions/IshAction";
import {FULFILLED} from "../../../../common/actions/ActionUtils";
import {INIT_REQUEST, SHOW_MESSAGES} from "../../../actions/Actions";

export const Reducer = (state: State = new State(), action: IAction<any>): State => {
  switch (action.type) {

    case SET_PAYMENT_DATA: {
      const { merchantReference, paymentFormUrl, sessionId } = action.payload;

      return {
        ...state,
        merchantReference,
        sessionId,
        iframeUrl: paymentFormUrl,
      };
    }

    case PROCESS_PAYMENT_V2: {
      return {
        ...state,
        fetching: true,
        iframeUrl: null
      }
    }

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
    case GET_CORPORATE_PASS_REQUEST:
      return {
        ...state,
        fetching: true,
      };

    case FULFILLED(GET_CORPORATE_PASS_REQUEST):
    case SHOW_MESSAGES:
    case INIT_REQUEST:
      return {
        ...state,
        fetching: false,
      };

    case RESET_PAYMENT_STATE:
      return new State();

    case RESET_PAYMENT_STATE_ON_INIT:
      return {
        ...state,
        resetOnInit: true,
      };

    case GENERATE_WAITING_COURSES_RESULT_DATA:
      return {
        ...state,
        result: action.payload,
      };

    default:
      return state;
  }
};
