import { User } from '@api/model';
import { SET_LOGIN_STATE, SET_SYSTEM_USER_DATA, } from '../../../common/actions';
import { IAction } from '../../../common/actions/IshAction';
import { CHECK_PASSWORD_FULFILLED, GET_EMAIL_BY_TOKEN_FULFILLED, POST_UPDATE_PASSWORD_FULFILLED } from '../actions';
import { LoginState } from './state';

const clearState: LoginState = {
  isTOTP: false,
  isNewTOTP: false,
  submittingSSOType: null,
  isBasic: false,
  isNewPassword: false,
  strongPasswordValidation: false,
  isEnableTOTP: false,
  isOptionalTOTP: false,
  isKickOut: false,
  isUpdatePassword: false,
  withNetworkFields: false,
  passwordChangeMessage: "",
  eulaUrl: undefined,
};

export const loginReducer = (state: LoginState = {}, action: IAction<any>): LoginState => {
  switch (action.type) {
    case SET_LOGIN_STATE: {
      return {
        ...state,
        ...clearState,
        ...action.payload
      };
    }

    case CHECK_PASSWORD_FULFILLED: {
      return {
        ...state,
        ...action.payload
      };
    }

    case POST_UPDATE_PASSWORD_FULFILLED: {
      if (!process.env.LOGIN_BUNDLE) {
        window.close();
      }

      return state;
    }

    case GET_EMAIL_BY_TOKEN_FULFILLED: {
      return {
        ...state,
        ...action.payload,
      };
    }

    default:
      return state;
  }
};

export const systemUserReducer = (state: User = {}, action: IAction<any>): User => {
  switch (action.type) {
    case SET_SYSTEM_USER_DATA: {
      return {
        ...state,
        ...action.payload,
      };
    }

    default:
      return state;
  }
}
