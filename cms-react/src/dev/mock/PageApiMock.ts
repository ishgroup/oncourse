import {promiseResolve} from "./MockAdapter";
import {API} from "../../js/constants/Config";

export function pageApiMock(mock) {
  mock.onGet(API.GET_PAGES).reply(config => promiseResolve(
    config,
    [
      {
        id: 1,
        title: 'Page - 1',
        visible: true,
        url: '/page1',
      },
      {
        id: 2,
        title: 'Page - 2',
        visible: true,
        url: '/page2',
      },
      {
        id: 3,
        title: 'Page - 3',
        visible: true,
        url: '/page3',
      },
    ],
  ));
}
