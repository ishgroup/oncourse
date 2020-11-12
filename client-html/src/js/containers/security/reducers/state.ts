import { UserRole, User, Site } from "@api/model";

export interface SecurityState {
  userRoles?: UserRole[];
  users?: User[];
  newPassword?: string;
}
