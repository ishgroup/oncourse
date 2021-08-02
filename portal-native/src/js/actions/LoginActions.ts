import { LoginRequest } from '@api/model';
import { toRequestType } from '../utils/ActionUtils';
import { LoginStages } from '../model/Login';

export const SIGN_IN = toRequestType('login/signIn');
export const EMAIL_LOGIN = toRequestType('login/email');
export const CONNECT = toRequestType('login/connect');

export const SET_LOGIN_STAGE = 'set/login/stage';

export const SET_LOGIN_URL = 'set/login/url';

export const SET_IS_LOGGED = 'set/login/isLogged';

export const signIn = (request: LoginRequest) => ({
  type: SIGN_IN,
  payload: request
});

export const emailLogin = (email: string) => ({
  type: EMAIL_LOGIN,
  payload: email
});

export const setLoginUrl = (url: string) => ({
  type: SET_LOGIN_URL,
  payload: url
});

export const setIsLogged = (isLogged: boolean) => ({
  type: SET_IS_LOGGED,
  payload: isLogged
});

export const setLoginStage = (stage: LoginStages) => ({
  type: SET_LOGIN_STAGE,
  payload: stage
});
