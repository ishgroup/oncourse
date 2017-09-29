import {promiseReject, promiseResolve} from "./MockAdapter";
import {API} from "../../js/constants/Config";

export function themeApiMock(mock) {
  mock.onGet(API.GET_THEMES).reply(config => promiseResolve(
    config,
    [
      {
        id: 1,
        title: 'Custom Theme',
        layout: 'Custom',
      },
      {
        id: 2,
        title: 'Default Theme',
        layout: 'User',
      },
    ],
  ));

  mock.onPost(API.SAVE_THEME).reply(config => {
    const request = JSON.parse(config.data);

    if (!request.title) {
      return promiseReject(config, {message: 'Title can not be blank'});
    }

    return promiseResolve(
      config,
      JSON.parse(config.data),
    );
  });


  mock.onPost(API.DELETE_THEME).reply(config => {
    return promiseResolve(
      config,
      null,
    );
  });
}
