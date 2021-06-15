import { LoginRequest } from '@api/model';
import { FULFILLED, toRequestType } from '../utils/ActionUtils';

export const SIGN_IN = toRequestType('login/signIn');
export const SIGN_IN_FULFILLED = FULFILLED(SIGN_IN);

export const SIGN_UP = toRequestType('login/signUp');
export const SIGN_UP_FULFILLED = FULFILLED(SIGN_UP);

export const signIn = (request: LoginRequest) => ({
  type: SIGN_IN,
  payload: request
});

export const signUp = (request: LoginRequest) => ({
  type: SIGN_UP,
  payload: request
});
