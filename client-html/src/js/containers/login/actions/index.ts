/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { LoginRequest } from '@api/model';
import { _toRequestType, FULFILLED } from '../../../common/actions/ActionUtils';

export const POST_CREATE_PASSWORD_REQUEST = _toRequestType("post/user/createPassword");
export const POST_CREATE_PASSWORD_FULFILLED = FULFILLED(POST_CREATE_PASSWORD_REQUEST);

export const GET_EMAIL_BY_TOKEN_REQUEST = _toRequestType("get/user/email");
export const GET_EMAIL_BY_TOKEN_FULFILLED = FULFILLED(GET_EMAIL_BY_TOKEN_REQUEST);

export const POST_AUTHENTICATION_REQUEST = _toRequestType("post/authentication");
export const POST_SSO_AUTHENTICATION_REQUEST = _toRequestType("post/sso/authentication");
export const POST_AUTHENTICATION_FULFILLED = FULFILLED(POST_AUTHENTICATION_REQUEST);

export const POST_UPDATE_PASSWORD_REQUEST = _toRequestType("post/user/updatePassword");
export const POST_UPDATE_PASSWORD_FULFILLED = FULFILLED(POST_UPDATE_PASSWORD_REQUEST);

export const CHECK_PASSWORD_REQUEST = _toRequestType("get/login");
export const CHECK_PASSWORD_FULFILLED = FULFILLED(CHECK_PASSWORD_REQUEST);

export const GET_SSO_INTEGRATIONS = _toRequestType("get/sso/integrations");
export const GET_SSO_INTEGRATIONS_FULFILLED = FULFILLED(GET_SSO_INTEGRATIONS);

export const GET_SYSTEM_USER_DATA = "get/systemUser/data";

export const getSsoIntegrations = () => ({
  type: GET_SSO_INTEGRATIONS
});

export const getEmailByToken = (value: string) => ({
  type: GET_EMAIL_BY_TOKEN_REQUEST,
  payload: { value }
});

export const checkPassword = (value: string, host?: string, port?: number) => ({
  type: CHECK_PASSWORD_REQUEST,
  payload: {
    value,
    host,
    port
  }
});

export const updatePasswordRequest = (value: string) => ({
  type: POST_UPDATE_PASSWORD_REQUEST,
  payload: { value }
});

export const postLoginRequest = (body: LoginRequest, host, port) => ({
  type: POST_AUTHENTICATION_REQUEST,
  payload: { body, host, port }
});

export const postSsoAuthenticationRequest = (ssoType: string, code: string, kickOut?: boolean) => ({
  type: POST_SSO_AUTHENTICATION_REQUEST,
  payload: { ssoType, code, kickOut }
});

export const getSystemUserData = () => ({
  type: GET_SYSTEM_USER_DATA,
});

export const createPasswordRequest = (token: string, password: string) => ({
  type: POST_CREATE_PASSWORD_REQUEST,
  payload: { token, password }
});