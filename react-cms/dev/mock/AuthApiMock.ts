import {promiseResolve} from "./MockAdapter";

export function authApiMock(mock) {
  mock.onPost('/getUser').reply(config => promiseResolve(config, {user: config.data}));
}
