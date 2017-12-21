import {AuthState} from "./State";
import {IAction} from "../../../actions/IshAction";
import {SUBMIT_LOGIN_FORM_FULFILLED, LOG_OUT_FULFILLED} from "../actions";
import {User} from "../../../model";

export const authReducer = (state: AuthState = new AuthState(), action: IAction<any>): AuthState => {
  switch (action.type) {
    case SUBMIT_LOGIN_FORM_FULFILLED:
      return {
        ...state,
        token: '',
        isAuthenticated: true,
        user: {
          firstName: action.payload.firstName,
          lastName: action.payload.lastName,
        },
      };

    case LOG_OUT_FULFILLED:
      return {
        ...state,
        token: null,
        isAuthenticated: false,
        user: new User(),
      };

    default:
      return state;
  }
};
