import { ApiToken, User, UserRole } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../common/actions/ActionUtils";

export const GET_USER_ROLES_REQUEST = _toRequestType("get/role");
export const GET_USER_ROLES_FULFILLED = FULFILLED(GET_USER_ROLES_REQUEST);

export const POST_USER_ROLES_REQUEST = _toRequestType("post/role");
export const POST_USER_ROLES_FULFILLED = FULFILLED(POST_USER_ROLES_REQUEST);

export const DELETE_USER_ROLES_REQUEST = _toRequestType("delete/role");
export const DELETE_USER_ROLES_FULFILLED = FULFILLED(DELETE_USER_ROLES_REQUEST);

export const GET_USERS_REQUEST = _toRequestType("get/user");
export const GET_USERS_REQUEST_FULFILLED = FULFILLED(GET_USERS_REQUEST);

export const GET_ACTIVE_USERS_REQUEST = _toRequestType("get/user/active");
export const GET_ACTIVE_USERS_REQUEST_FULFILLED = FULFILLED(GET_ACTIVE_USERS_REQUEST);

export const POST_USER_REQUEST = _toRequestType("post/user");
export const POST_USER_REQUEST_FULFILLED = FULFILLED(POST_USER_REQUEST);

export const VALIDATE_USER_PASSWORD = _toRequestType("get/user/passwordValidity");
export const VALIDATE_USER_PASSWORD_FULFILLED = FULFILLED(VALIDATE_USER_PASSWORD);

export const RESET_USER_PASSWORD = _toRequestType("put/user/resetPassword");
export const RESET_USER_PASSWORD_FULFILLED = FULFILLED(RESET_USER_PASSWORD);

export const DISABLE_USER_2FA = _toRequestType("put/user/disableTFA");
export const DISABLE_USER_2FA_FULFILLED = FULFILLED(DISABLE_USER_2FA);

export const GET_API_TOKENS_REQUEST = _toRequestType("get/tokens");

export const UPDATE_API_TOKENS_REQUEST = _toRequestType("post/tokens");

export const DELETE_API_TOKEN_REQUEST = _toRequestType("delete/token");

export const CLEAR_USER_PASSWORD = "user/password/clear";

export const disableUser2FA = (id: number) => ({
  type: DISABLE_USER_2FA,
  payload: id
});

export const resetUserPassword = (id: number) => ({
  type: RESET_USER_PASSWORD,
  payload: id
});

export const updateApiTokens = (tokens: ApiToken[]) => ({
  type: UPDATE_API_TOKENS_REQUEST,
  payload: tokens
});

export const deleteApiToken = (tokenId: number) => ({
  type: DELETE_API_TOKEN_REQUEST,
  payload: tokenId
});

export const getApiTokens = () => ({
  type: GET_API_TOKENS_REQUEST
});

export const validateNewUserPassword = (value: string) => ({
  type: VALIDATE_USER_PASSWORD,
  payload: value
});

export const getUsers = () => ({
  type: GET_USERS_REQUEST
});

export const getActiveUsers = () => ({
  type: GET_ACTIVE_USERS_REQUEST
});

export const updateUser = (user: User) => ({
  type: POST_USER_REQUEST,
  payload: user
});

export const getUserRoles = () => ({
  type: GET_USER_ROLES_REQUEST
});

export const updateUserRole = (userRole: UserRole) => ({
  type: POST_USER_ROLES_REQUEST,
  payload: userRole
});

export const removeUserRole = (id: number) => ({
  type: DELETE_USER_ROLES_REQUEST,
  payload: id
});
