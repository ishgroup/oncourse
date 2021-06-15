import { LoginState } from '../model/Login';
import { IAction } from '../model/IshAction';
import { SIGN_IN_FULFILLED } from '../actions/LoginActions';

const initial: LoginState = {
  isLogged: false
};

export default (state = initial, action: IAction) => {
  switch (action.type) {
    case SIGN_IN_FULFILLED:
      return {
        ...state,
        isLogged: true
      };

    default:
      return state;
  }
};
