import { LoginState } from '../model/Login';
import { IAction } from '../model/IshAction';
import {
  EMAIL_LOGIN, SET_IS_LOGGED, SET_LOGIN_STAGE, SET_LOGIN_URL, SET_USER, SIGN_IN
} from '../actions/LoginActions';
import { FETCH_FAIL } from '../actions/FetchActions';

const initial: LoginState = {
  isLogged: false,
  loading: false,
  verificationUrl: null,
  stage: 0,
  user: null
};

export default (state = initial, action: IAction): LoginState => {
  switch (action.type) {
    case EMAIL_LOGIN:
    case SIGN_IN:
      return {
        ...state,
        loading: true
      };

    case SET_IS_LOGGED:
      return {
        ...state,
        loading: false,
        isLogged: action.payload
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

    case SET_USER:
      return {
        ...state,
        user: action.payload
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
