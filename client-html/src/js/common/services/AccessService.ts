import { DefaultHttpService } from "./HttpService";
import { AccessApi, PermissionRequest, PermissionResponse } from "@api/model";

class AccessService {
  readonly accessApi = new AccessApi(new DefaultHttpService());

  public checkPermissions(permissionRequest: PermissionRequest): Promise<PermissionResponse> {
    return this.accessApi.checkPermissions(permissionRequest);
  }
}

export default new AccessService();
