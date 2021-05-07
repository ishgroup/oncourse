import { promiseResolve } from "../../MockAdapter";

export function SecurityApiMock() {
  this.api.onGet("v1/role").reply(config =>
    promiseResolve(config, this.db.getSecurityRoles()));

  this.api.onGet("v1/user").reply(config =>
    promiseResolve(config, this.db.getSecurityUsers()));
}
