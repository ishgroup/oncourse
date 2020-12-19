import { PermissionResponse } from "@api/model";

export function mockAccessApi() {
  this.getAccessApi = (): PermissionResponse => this.accessApi;

  return {
    "hasAccess": true,
    "cause": null
  };
}
