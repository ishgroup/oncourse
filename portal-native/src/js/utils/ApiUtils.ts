import { AxiosResponse } from 'axios';
import { IAction } from '../model/IshAction';
import { FETCH_FAIL } from '../actions/FetchActions';

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
            message: data.errorMessage || customMessage || 'Something went wrong',
          },
        },
      ];

    case 403:
      return [
        {
          type: FETCH_FAIL,
          payload: {
            message: data.errorMessage || customMessage || 'Something went wrong',
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
