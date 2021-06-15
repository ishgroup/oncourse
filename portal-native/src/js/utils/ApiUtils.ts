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
            formError: data,
            message: data.errorMessage || customMessage,
          },
        },
      ];

      //  Redirect if not logged in
      // case 401:
      //   const state: State = store.getState();
      //   const lastLocation = state.lastLocation || window.location.pathname;
      //   LSRemoveItem(APPLICATION_THEME_STORAGE_NAME);
      //   LSRemoveItem(DASHBOARD_ACTIVITY_STORAGE_NAME);
      //   history.push("/login");
      //
      //   return [
      //     {
      //       type: SET_LAST_LOCATION,
      //       payload: lastLocation
      //     },
      //     {
      //       type: GET_IS_LOGGED_FULFILLED,
      //       payload: false
      //     },
      //     ...(data["url"] && data["url"] === "/a/"
      //       ? []
      //       : [
      //         {
      //           type: FETCH_FAIL,
      //           payload: { message: "Unauthorized" }
      //         }
      //       ])
      //   ];

    case 403:
      return [
        {
          type: FETCH_FAIL,
          payload: {
            message: data.errorMessage || customMessage,
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
