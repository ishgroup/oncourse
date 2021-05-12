import {MockAdapter, promiseResolve} from "../MockAdapter";

export function BillingApiMock(this: MockAdapter) {
  this.api.onGet(new RegExp(`v1/college/.+`))
    .reply(config => promiseResolve(config, true))
}
