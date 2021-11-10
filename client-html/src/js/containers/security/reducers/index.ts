import { IAction } from "../../../common/actions/IshAction";
import { SecurityState } from "./state";
import {
  DELETE_USER_ROLES_FULFILLED,
  DISABLE_USER_2FA_FULFILLED,
  GET_ACTIVE_USERS_REQUEST_FULFILLED,
  GET_USER_ROLES_FULFILLED,
  GET_USERS_REQUEST_FULFILLED,
  POST_USER_REQUEST_FULFILLED,
  POST_USER_ROLES_FULFILLED,
  RESET_USER_PASSWORD_FULFILLED
} from "../actions";

export const securityReducer = (state: SecurityState = {}, action: IAction<any>): any => {
  switch (action.type) {
    case GET_USER_ROLES_FULFILLED:
    case POST_USER_ROLES_FULFILLED:
    case DELETE_USER_ROLES_FULFILLED:
    case GET_USERS_REQUEST_FULFILLED:
    case POST_USER_REQUEST_FULFILLED:
    case RESET_USER_PASSWORD_FULFILLED:
    case GET_ACTIVE_USERS_REQUEST_FULFILLED:
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
