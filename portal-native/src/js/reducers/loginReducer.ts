import { LoginState } from '../model/Login';
import { IAction } from '../model/IshAction';
import { EMAIL_LOGIN, SET_LOGIN_STAGE, SET_LOGIN_URL, SIGN_IN, SIGN_IN_FULFILLED } from '../actions/LoginActions';
import { FETCH_FAIL } from '../actions/FetchActions';

const initial: LoginState = {
  isLogged: false,
  loading: false,
  verificationUrl: null,
  stage: 0
};

export default (state = initial, action: IAction) => {
  switch (action.type) {
    case EMAIL_LOGIN:
    case SIGN_IN:
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

    case SET_LOGIN_STAGE:
      return {
        ...state,
        stage: action.payload,
        loading: false
      };

    case SET_LOGIN_URL:
      return {
        ...state,
        verificationUrl: action.payload
      };

    case FETCH_FAIL:
      return {
        ...state,
        loading: false
      };

    default:
      return state;
  }
};
