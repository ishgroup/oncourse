import { PermissionResponse } from "@api/model";

export function mockAccessApi() {
  this.accessApi = (): PermissionResponse => ({
    "hasAccess": true,
    "cause": null
  });
}
