import { User, UserRole } from "@api/model";

export interface SecurityState {
  userRoles?: UserRole[];
  users?: User[];
  activeUsers?: User[];
  newPassword?: string;
}
