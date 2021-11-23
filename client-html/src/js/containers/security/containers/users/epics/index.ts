import { combineEpics } from "redux-observable";
import { EpicGetUsers } from "./EpicGetUsers";
import { EpicUpdateUser } from "./EpicUpdateUser";
import { EpicValidateUserPassword } from "./EpicValidateUserPassword";
import { EpicResetUserPassword } from "./EpicResetUserPassword";
import { EpicDisableUser2FA } from "./EpicDisableUser2FA";
import { EpicGetActiveUsers } from "./EpicGetActiveUsers";

export const EpicUsers = combineEpics(
  EpicGetUsers,
  EpicGetActiveUsers,
  EpicUpdateUser,
  EpicValidateUserPassword,
  EpicResetUserPassword,
  EpicDisableUser2FA
);
