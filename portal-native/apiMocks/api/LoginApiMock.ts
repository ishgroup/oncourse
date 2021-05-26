import {MockAdapter} from "../MockAdapter";
import {promiseResolve} from "../utils";

export function LoginApiMock(this: MockAdapter) {
  this.api.onGet("/v1/login/")
    .reply(config => promiseResolve(config, true))
}
