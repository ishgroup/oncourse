import {AuthState} from "./State";
import {IAction} from "../../../actions/IshAction";
import {SUBMIT_LOGIN_FORM_FULFILLED, LOG_OUT_FULFILLED, GET_USER_FULFILLED} from "../actions";
import {User} from "../../../model";

export const authReducer = (state: AuthState = new AuthState(), action: IAction<any>): AuthState => {
  switch (action.type) {
    case SUBMIT_LOGIN_FORM_FULFILLED:
      const { credentials } = action.payload;

      return {
        ...state,
        credentials,
        token: '',
        isAuthenticated: true,
      };

    case LOG_OUT_FULFILLED:
      return {
        ...state,
        token: null,
        isAuthenticated: false,
        user: {} as User,
      };
      
    case GET_USER_FULFILLED:
      return {
        ...state,
        user: {
          firstName: action.payload.firstName,
          lastName: action.payload.lastName,
        },
      };

    default:
      return state;
  }
};
