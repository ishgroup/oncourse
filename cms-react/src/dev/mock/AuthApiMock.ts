import {promiseResolve} from "./MockAdapter";
import {API} from "../../js/constants/Config";

export function authApiMock(mock) {
  mock.onPost(API.LOGIN).reply(config => promiseResolve(config, {
    user: {
      id: 1,
      firstName: "John",
      lastName: "Doe",
    },
  }));

  mock.onPost(API.LOGOUT).reply(config => promiseResolve(config));
}
