import { combineEpics } from "redux-observable";
import { EpicDisableUser2FA } from "./EpicDisableUser2FA";
import { EpicGetActiveUsers } from "./EpicGetActiveUsers";
import { EpicGetUsers } from "./EpicGetUsers";
import { EpicResetUserPassword } from "./EpicResetUserPassword";
import { EpicUpdateUser } from "./EpicUpdateUser";
import { EpicValidateUserPassword } from "./EpicValidateUserPassword";

export const EpicUsers = combineEpics(
  EpicGetUsers,
  EpicGetActiveUsers,
  EpicUpdateUser,
  EpicValidateUserPassword,
  EpicResetUserPassword,
  EpicDisableUser2FA
);
