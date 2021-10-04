import { FULFILLED, REJECTED } from './ActionUtils';

export const FETCH_START = 'common/fetch/start';
export const FETCH_SUCCESS = FULFILLED('common/fetch');
export const FETCH_FAIL = REJECTED('common/fetch');
export const FETCH_FINISH = 'common/fetch/finish';

export const SET_SERVER_ERROR_VALUE = 'SET_SERVER_ERROR_VALUE';

export const SHOW_MESSAGE = 'SHOW_MESSAGE';
export const CLEAR_MESSAGE = 'CLEAR_MESSAGE';

export const SET_IS_NEW_USER = 'SET_IS_NEW_USER';

export const RESET_STORE = 'RESET_STORE';
export const SET_LOADING_VALUE = 'SET_LOADING_VALUE';

export const setIsNewUser = (isNewUser: boolean) => ({
  type: SET_IS_NEW_USER,
  payload: isNewUser
});

export const showMessage = (payload: { message: string, success: boolean }) => ({
  type: SHOW_MESSAGE,
  payload
});

export const clearMessage = () => ({
  type: CLEAR_MESSAGE
});

export const setServerErrorValue = (payload: boolean) => ({
  type: SET_SERVER_ERROR_VALUE,
  payload
});

export const resetStore = () => ({
  type: RESET_STORE
});

export const setLoadingValue = (payload: boolean) => ({
  type: SET_LOADING_VALUE,
  payload
});
