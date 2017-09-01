import {promiseResolve} from "./MockAdapter";

export function authApiMock(mock) {
  mock.onPost('/getUser').reply(config => promiseResolve(config, {
    user: {
      id: 1,
      firstName: "Andrey",
      lastName: "Davidovich",
    },
  }));

  mock.onPost('/logout').reply(config => promiseResolve(config));
}
