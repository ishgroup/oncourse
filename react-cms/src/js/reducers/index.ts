import {combineReducers} from "redux";
import {AuthState, State} from "./State";
import {IAction} from "../actions/IshAction";
import {SUBMIT_LOGIN_FORM} from "../containers/login/Actions/index";

const authReducer = (state: AuthState = new AuthState(), action: IAction<any>): AuthState => {
  switch (action.type) {
    case SUBMIT_LOGIN_FORM:
      return {
        ...state,
        token: '1111-2222-3333',
        isAuthenticated: true,
      }

    default:
      return state;
  }
};

export const combinedReducers = combineReducers({
  auth: authReducer,
});
