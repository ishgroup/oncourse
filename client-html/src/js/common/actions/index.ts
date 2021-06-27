/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

/**
 * Common actions of App
 * */

import {
  LoginRequest,
  PermissionRequest,
  PreferenceEnum,
  UserPreference
} from "@api/model";
import { _toRequestType, FULFILLED, REJECTED } from "./ActionUtils";
import { LoginState } from "../../containers/login/reducers/state";
import { ShowConfirmCaller } from "../../model/common/Confirm";
import { IAction } from "./IshAction";
import { QueuedAction } from "../../model/common/ActionsQueue";
import { ApiMethods } from "../../model/common/apiHandlers";
import { AppMessage } from "../../model/common/Message";

export const CHECK_PERMISSIONS_REQUEST = _toRequestType("post/access");
export const CHECK_PERMISSIONS_REQUEST_FULFILLED = FULFILLED(CHECK_PERMISSIONS_REQUEST);

export const INTERRUPT_PROCESS = _toRequestType("delete/control");
export const INTERRUPT_PROCESS_FULFILLED = FULFILLED(INTERRUPT_PROCESS);

export const GET_SCRIPTS_REQUEST = _toRequestType("get/entity/scripts");
export const GET_SCRIPTS_FULFILLED = FULFILLED(GET_SCRIPTS_REQUEST);

export const GET_ON_DEMAND_SCRIPTS = _toRequestType("get/onDemand/scripts");
export const GET_ON_DEMAND_SCRIPTS_FULFILLED = FULFILLED(GET_ON_DEMAND_SCRIPTS);

export const GET_EMAIL_TEMPLATES_WITH_KEYCODE = _toRequestType("get/entity/emailTemplatesWithKeyCode");
export const GET_EMAIL_TEMPLATES_WITH_KEYCODE_FULFILLED = FULFILLED(GET_EMAIL_TEMPLATES_WITH_KEYCODE);

export const GET_LDAP_CONNECTION_REQUEST = _toRequestType("get/ldapConnection");

export const GET_MESSAGE_QUEUED_REQUEST = _toRequestType("get/messageQueued");
export const GET_MESSAGE_QUEUED_FULFILLED = FULFILLED(GET_MESSAGE_QUEUED_REQUEST);

export const POST_AUTHENTICATION_REQUEST = _toRequestType("post/authentication");
export const POST_AUTHENTICATION_FULFILLED = FULFILLED(POST_AUTHENTICATION_REQUEST);

export const POST_UPDATE_PASSWORD_REQUEST = _toRequestType("post/user/updatePassword");
export const POST_UPDATE_PASSWORD_FULFILLED = FULFILLED(POST_UPDATE_PASSWORD_REQUEST);

export const POST_CREATE_PASSWORD_REQUEST = _toRequestType("post/user/createPassword");
export const POST_CREATE_PASSWORD_FULFILLED = FULFILLED(POST_CREATE_PASSWORD_REQUEST);

export const GET_EMAIL_BY_TOKEN_REQUEST = _toRequestType("get/user/email");
export const GET_EMAIL_BY_TOKEN_FULFILLED = FULFILLED(GET_EMAIL_BY_TOKEN_REQUEST);

export const CHECK_PASSWORD_REQUEST = _toRequestType("get/login");
export const CHECK_PASSWORD_FULFILLED = FULFILLED(CHECK_PASSWORD_REQUEST);

export const GET_USER_PREFERENCES = _toRequestType("get/user/preference");
export const GET_USER_PREFERENCES_FULFILLED = FULFILLED(GET_USER_PREFERENCES);

export const SET_USER_PREFERENCE = _toRequestType("put/user/preference");

export const FETCH_START = "common/fetch/start";
export const FETCH_SUCCESS = FULFILLED("common/fetch");
export const FETCH_FAIL = REJECTED("common/fetch");

export const FETCH_CLEAR = "common/fetch/clearInfo";
export const FETCH_FINISH = "common/fetch/finish";

export const OPEN_DRAWER = "common/drawer/open";
export const CLOSE_DRAWER = "common/drawer/close";

export const OPEN_CONFIRM = "common/confirm/open";
export const CLOSE_CONFIRM = "common/confirm/close";

export const SHOW_MESSAGE = "common/message/show";
export const CLEAR_MESSAGE = "common/message/clear";

export const START_PROCESS = "start/process";
export const UPDATE_PROCESS = "update/process";
export const CLEAR_PROCESS = "clear/process";
export const CLEAR_ACTION_ON_FAIL = "clear/action/on/fail";

export const SET_LOGIN_STATE = "login/state/set";

export const SET_LAST_LOCATION = "set/lastLocation";

export const CLEAR_LAST_LOCATION = "clear/lastLocation";

export const ADD_ACTION_TO_QUEUE = "add/actionsQueue/action";

export const REMOVE_ACTIONS_FROM_QUEUE = "remove/actionsQueue/action";

export const EXECUTE_ACTIONS_QUEUE = "execute/actionsQueue";

export const CLEAR_ACTIONS_QUEUE = "clear/actionsQueue";

export const NEXT_LOCATION = 'nextLocation';

export const addActionToQueue = (
  actionBody: IAction,
  method: ApiMethods,
  entity: string,
  id: number | string,
  bindedActionId?: number | string
): IAction<QueuedAction> => ({
  type: ADD_ACTION_TO_QUEUE,
  payload: {
    actionBody, method, entity, id, bindedActionId
  }
});

interface RemoveQueuedActionMeta {
  entity: string;
  id?: number | string;
}

export const removeActionsFromQueue = (meta: RemoveQueuedActionMeta[]) => ({
  type: REMOVE_ACTIONS_FROM_QUEUE,
  payload: { meta }
});

export const executeActionsQueue = () => ({
  type: EXECUTE_ACTIONS_QUEUE
});

export const clearActionsQueue = () => ({
  type: CLEAR_ACTIONS_QUEUE
});

export const showConfirm: ShowConfirmCaller = (payload) => {
  return {
    type: OPEN_CONFIRM,
    payload
  };
};

export const closeConfirm = () => ({
  type: CLOSE_CONFIRM
});

export const setLastLocation = () => ({
  type: SET_LAST_LOCATION
});

export const clearLastLocation = () => ({
  type: CLEAR_LAST_LOCATION
});

export const checkPassword = (value: string, host?: string, port?: number) => ({
  type: CHECK_PASSWORD_REQUEST,
  payload: {
    value,
    host,
    port
  }
});

export const setLoginState = (payload: LoginState) => ({
  payload,
  type: SET_LOGIN_STATE
});

export const openDrawer = () => ({
  type: OPEN_DRAWER
});

export const closeDrawer = () => ({
  type: CLOSE_DRAWER
});

export const updatePasswordRequest = (value: string) => ({
  type: POST_UPDATE_PASSWORD_REQUEST,
  payload: { value }
});

export const postLoginRequest = (body: LoginRequest, host, port) => ({
  type: POST_AUTHENTICATION_REQUEST,
  payload: { body, host, port }
});

export const createPasswordRequest = (token: string, password: string) => ({
  type: POST_CREATE_PASSWORD_REQUEST,
  payload: { token, password }
});

export const getEmailByToken = (value: string) => ({
  type: GET_EMAIL_BY_TOKEN_REQUEST,
  payload: { value }
});

export const getScripts = (entity: string) => ({
  type: GET_SCRIPTS_REQUEST,
  payload: { entity }
});

export const getOnDemandScripts = () => ({
  type: GET_ON_DEMAND_SCRIPTS
});

export const getEmailTemplatesWithKeyCode = (entities: string[]) => ({
  type: GET_EMAIL_TEMPLATES_WITH_KEYCODE,
  payload: { entities }
});

export const getLdapConnection = (host: string, port: string, isSsl: string, baseDn: string, user: string) => ({
  type: GET_LDAP_CONNECTION_REQUEST,
  payload: {
    host, port, isSsl, baseDn, user
  }
});

export const getMessageQueued = (type: string) => ({
  type: GET_MESSAGE_QUEUED_REQUEST,
  payload: { type }
});

export const clearFetch = () => ({
  type: FETCH_CLEAR
});

export const getProcessStatus = (processId: string, actions: any[]) => ({
  type: START_PROCESS,
  payload: { processId, actions }
});

export const interruptProcess = (processId: string) => ({
  type: INTERRUPT_PROCESS,
  payload: { processId }
});

export const clearProcess = () => ({
  type: CLEAR_PROCESS
});

export const checkPermissions = (permissionRequest: PermissionRequest, onComplete?: IAction[]) => ({
  type: CHECK_PERMISSIONS_REQUEST,
  payload: { permissionRequest, onComplete }
});

export const getUserPreferences = (keys: PreferenceEnum[]) => ({
  type: GET_USER_PREFERENCES,
  payload: keys
});

export const setUserPreference = (userPreference: UserPreference) => ({
  type: SET_USER_PREFERENCE,
  payload: userPreference
});

export const showMessage = (message: AppMessage) => ({
  type: SHOW_MESSAGE,
  payload: message
});

export const clearMessage = () => ({
  type: CLEAR_MESSAGE
});

export const setNextLocation = (nextLocation: string) => ({
  type: NEXT_LOCATION,
  payload: nextLocation
})
