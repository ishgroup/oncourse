import {promiseReject, promiseResolve} from "./MockAdapter";
import {API} from "../../js/constants/Config";

export function blockApiMock(mock) {
  mock.onGet(API.GET_BLOCKS).reply(config => promiseResolve(
    config,
    [
      {
        id: 1,
        title: 'Block - 1',
      },
      {
        id: 2,
        title: 'Block - 2',
      },
      {
        id: 3,
        title: 'Block - 3',
      },
    ],
  ));

  mock.onPost(API.SAVE_BLOCK).reply(config => {
    const request = JSON.parse(config.data);

    if (!request.title) {
      return promiseReject(config, {message: 'Title can not be blank'});
    }

    return promiseResolve(
      config,
      JSON.parse(config.data),
    );
  });


  mock.onPost(API.DELETE_BLOCK).reply(config => {
    return promiseResolve(
      config,
      null,
    );
  });
}
