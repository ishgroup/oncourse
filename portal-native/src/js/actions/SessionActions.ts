import { Session } from '@api/model';
import { FULFILLED, toRequestType } from '../utils/ActionUtils';

export const GET_USER_SESSIONS = toRequestType('get/user/sessions');
export const GET_USER_SESSIONS_FULFILLED = FULFILLED(GET_USER_SESSIONS);

export const getUserSessions = () => ({
  type: GET_USER_SESSIONS,
});

export const getUserSessionsFulfilled = (sessions: Session[]) => ({
  type: GET_USER_SESSIONS_FULFILLED,
  payload: sessions
});
