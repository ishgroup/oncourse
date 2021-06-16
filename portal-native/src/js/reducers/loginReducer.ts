import { LoginState } from '../model/Login';
import { IAction } from '../model/IshAction';
import { SIGN_IN_FULFILLED, SIGN_UP, SIGN_IN } from '../actions/LoginActions';

const initial: LoginState = {
  isLogged: false,
  loading: false
};

export default (state = initial, action: IAction) => {
  switch (action.type) {
    case SIGN_IN:
    case SIGN_UP:
      return {
        ...state,
        loading: true
      };

    case SIGN_IN_FULFILLED:
      return {
        ...state,
        loading: false,
        isLogged: true
      };

    default:
      return state;
  }
};
