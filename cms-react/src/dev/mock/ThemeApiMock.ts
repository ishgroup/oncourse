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
        schema: {
          top: [{
            id: 1,
            position: 1,
          }],
          middle1: [],
          middle2: [],
          middle3: [],
          footer: [{
            id: 2,
            position: 2,
          }],
        },
      },
      {
        id: 2,
        title: 'Default Theme',
        layout: 'User',
        schema: {
          top: [],
          middle1: [],
          middle2: [{
            id: 3,
            position: 1,
          },
          {
            id: 4,
            position: 1,
          }],
          middle3: [],
          footer: [{
            id: 5,
            position: 2,
          }],
        },
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
