import { LoginRequest, LoginResponse } from '@api/model';
import { FULFILLED, toRequestType } from '../utils/ActionUtils';

export const SIGN_IN = toRequestType('login/signIn');
export const SIGN_IN_FULFILLED = FULFILLED(SIGN_IN);

export const SIGN_UP = toRequestType('login/signUp');

export const CONNECT = toRequestType('login/connect');


export const signIn = (request: LoginRequest) => ({
  type: SIGN_IN,
  payload: request
});

export const signUp = (request: LoginRequest) => ({
  type: SIGN_UP,
  payload: request
});

export const signInFulfilled = (response: LoginResponse) => ({
  type: SIGN_IN_FULFILLED,
  payload: response
});
