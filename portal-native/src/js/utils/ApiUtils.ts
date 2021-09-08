import { AxiosResponse } from 'axios';
import { Dispatch } from 'redux';
import { IAction } from '../model/IshAction';
import { FETCH_FAIL } from '../actions/FetchActions';
import { SET_MESSAGE } from '../actions/MessageActions';
import { setIsLogged } from '../actions/LoginActions';
import { removeToken } from './SessionStorageUtils';

export const FetchErrorHandler = (response: AxiosResponse, customMessage?: string): IAction<any>[] => {
  if (!response) {
    return [
      {
        type: FETCH_FAIL,
        payload: { message: customMessage || 'Something went wrong' },
      },
    ];
  }

  const { data, status } = response;

  switch (status) {
    case 400:
      return [
        {
          type: FETCH_FAIL,
          payload: {
            message: data?.message || customMessage || 'Something went wrong',
          },
        },
      ];

    case 401:
      removeToken();
      return [
        setIsLogged(false),
        {
          type: FETCH_FAIL,
          payload: {
            message: data?.message || customMessage || 'Something went wrong',
          },
        },
      ];

    case 403:
      return [
        {
          type: FETCH_FAIL,
          payload: {
            message: data?.message || customMessage || 'Something went wrong',
          },
        },
      ];

    default:
      console.error(response);

      return [
        {
          type: FETCH_FAIL,
          payload: { message: customMessage || 'Something went wrong' },
        },
      ];
  }
};

export const LoginErrorHandler = (response: AxiosResponse, customMessage?: string): IAction<any>[] => {
  removeToken();
  return [
    setIsLogged(false),
    {
      type: FETCH_FAIL,
      payload: {
        message: response?.data?.message || customMessage || 'Something went wrong',
      },
    },
  ];
};

export const instantFetchErrorHandler = (
  dispatch: Dispatch,
  response: AxiosResponse,
  message = 'Something went wrong'
) => {
  if (!response) {
    dispatch({
      type: SET_MESSAGE,
      payload: { message }
    });
    return;
  }

  const { data, status } = response;

  if (status) {
    if (status === 401) {
      removeToken();
      dispatch(setIsLogged(false));
    }

    dispatch({
      type: SET_MESSAGE,
      payload: {
        message: data?.message || message
      }
    });
  } else {
    dispatch({
      type: SET_MESSAGE,
      payload: {
        message: response
      }
    });
  }
};

export default instantFetchErrorHandler;
