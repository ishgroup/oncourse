import {MockAdapter, promiseResolve} from "../MockAdapter";
import {Site} from "../../../js/models/Site";

export function BillingApiMock(this: MockAdapter) {
  this.api.onGet(new RegExp(`v1/college/.+`))
    .reply(config => promiseResolve(config, true))

  this.api.onGet(new RegExp(`v1/user/.+`))
    .reply(config => promiseResolve(config, true))

  this.api.onGet(new RegExp(`v1/sites/.+`))
    .reply(config => promiseResolve<Site[]>(config, [
      {
        id: 1,
        name: "Site 2",
        key: "nida-2",
        prefix: "nida",
        domains: ["www.corporate.nida.edu.au", "www.open.nida.edu.au"]
      },
      {
        id: 2,
        name: "Site 3",
        key: "nida-3",
        prefix: "nida",
        domains: ["www.corporate.nida.edu.au", "www.open.nida.edu.au"]
      }
    ]))
}
