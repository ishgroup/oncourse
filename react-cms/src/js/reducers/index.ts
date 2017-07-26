import {State} from "./State";
import {IAction} from "../actions/IshAction";
import {SUBMIT_LOGIN_FORM} from "../containers/Login/Actions/index";

export const reducer = (state: State = new State(), action: IAction<any>): State => {
  console.log(action);
  switch (action.type) {

    case SUBMIT_LOGIN_FORM:
      return {
        ...state,
        auth: {
          token: '1111-2222-3333',
          isAuthenticated: true,
        },
      }

    default:
      return state;
  }
};
