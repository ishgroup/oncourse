import { combineEpics } from "redux-observable";
import { EpicGetUsers } from "./EpicGetUsers";
import { EpicUpdateUser } from "./EpicUpdateUser";
import { EpicValidateUserPassword } from "./EpicValidateUserPassword";
import { EpicResetUserPassword } from "./EpicResetUserPassword";
import { EpicDisableUser2FA } from "./EpicDisableUser2FA";

export const EpicUsers = combineEpics(
  EpicGetUsers,
  EpicUpdateUser,
  EpicValidateUserPassword,
  EpicResetUserPassword,
  EpicDisableUser2FA
);
