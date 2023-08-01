import { UserRole, UserRoleApi } from "@api/model";
import { DefaultHttpService } from "../../../../../common/services/HttpService";

class UserRolesService {
  readonly userRoleApi = new UserRoleApi(new DefaultHttpService());

  public getUserRoles(): Promise<UserRole[]> {
    return this.userRoleApi.get();
  }

  public updateUserRole(userRole: UserRole): Promise<any> {
    return this.userRoleApi.update(userRole);
  }

  public removeUserRole(id: number): Promise<any> {
    return this.userRoleApi.remove(id);
  }
}

export default new UserRolesService();
