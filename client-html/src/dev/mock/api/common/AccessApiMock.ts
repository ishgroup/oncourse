import { promiseResolve } from "../../MockAdapter";

export function AccessApiMock() {
  this.api.onPost("/v1/access").reply(config => promiseResolve(config, this.db.getAccessApi()));
}
