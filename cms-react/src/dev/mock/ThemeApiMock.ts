import {promiseReject, promiseResolve} from "./MockAdapter";
import {API} from "../../js/constants/Config";

export function themeApiMock(mock) {
  mock.onGet(API.GET_THEMES).reply(config => promiseResolve(
    config,
    [
      {
        id: 1,
        title: 'Custom Theme',
      },
      {
        id: 2,
        title: 'Default Theme',
      },
    ],
  ));
}
