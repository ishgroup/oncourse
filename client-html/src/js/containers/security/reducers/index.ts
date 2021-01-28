import { IAction } from "../../../common/actions/IshAction";
import { SecurityState } from "./state";
import {
  GET_USER_ROLES_FULFILLED,
  POST_USER_ROLES_FULFILLED,
  DELETE_USER_ROLES_FULFILLED,
  GET_USERS_REQUEST_FULFILLED,
  POST_USER_REQUEST_FULFILLED,
  RESET_USER_PASSWORD_FULFILLED,
  CLEAR_USER_PASSWORD,
  DISABLE_USER_2FA_FULFILLED
} from "../actions";

export const securityReducer = (state: SecurityState = {}, action: IAction<any>): any => {
  switch (action.type) {
    case GET_USER_ROLES_FULFILLED:
    case POST_USER_ROLES_FULFILLED:
    case DELETE_USER_ROLES_FULFILLED:
    case GET_USERS_REQUEST_FULFILLED:
    case POST_USER_REQUEST_FULFILLED:
    case RESET_USER_PASSWORD_FULFILLED:
    case DISABLE_USER_2FA_FULFILLED: {
      return {
        ...state,
        ...action.payload
      };
    }

    default:
      return state;
  }
};
