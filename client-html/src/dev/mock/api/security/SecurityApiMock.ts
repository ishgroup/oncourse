import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function SecurityApiMock() {
  this.api.onGet("v1/role").reply(config =>
    promiseResolve(config, this.db.getSecurityRoles()));

  this.api.onPost("v1/role").reply(config => {
    return promiseResolve(config, this.db.getSecurityRoles());
  });

  this.api.onDelete(new RegExp(`v1/role/\\d+`)).reply(config => {
    const id = getParamsId(config);
    this.db.removeSecurityUserRole(id);
    return promiseResolve(config, this.db.getSecurityRoles());
  });

  this.api.onGet("v1/user").reply(config =>
    promiseResolve(config, this.db.getSecurityUsers()));

  this.api.onPost("v1/user").reply(config =>
    promiseResolve(config, this.db.getSecurityUsers()));

  this.api.onPut(new RegExp(`v1/user/resetPassword/\\d+`)).reply(config =>
    promiseResolve(config, this.db.getSecurityUsers()));

}
