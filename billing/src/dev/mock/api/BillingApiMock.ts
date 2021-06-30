import {MockAdapter, promiseResolve} from "../MockAdapter";
import {SiteDTO} from "@api/model";

export function BillingApiMock(this: MockAdapter) {
  this.api.onGet(new RegExp(`v1/college/(?!sites)[^/]+$`))
    .reply(config => promiseResolve(config, true))

  this.api.onGet("/v1/collegeKey")
    .reply(config => promiseResolve(config, "nida"))

  this.api.onGet("/v1/college/sites")
    .reply(config => promiseResolve<SiteDTO[]>(config, [
      {
        id: 1,
        name: "Site 2",
        key: "nida-2",
        domains: ["www.corporate.nida.edu.au", "www.open.nida.edu.au"]
      },
      {
        id: 2,
        name: "Site 3",
        key: "nida-3",
        domains: ["www.corporate.nida.edu.au", "www.open.nida.edu.au"]
      }
    ]))

  this.api.onPost("/v1/college/sites")
    .reply(config => promiseResolve(config))
}
